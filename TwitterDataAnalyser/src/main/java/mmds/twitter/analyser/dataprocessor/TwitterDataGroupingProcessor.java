package mmds.twitter.analyser.dataprocessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TwitterDataGroupingProcessor {

	private static final Logger logger = LoggerFactory.getLogger(TwitterDataGroupingProcessor.class);

	private static String TWEET_FILE = "tweets";
	private static String JSON_FILE_EXT = ".json";

	private ObjectMapper objectMapper = new ObjectMapper();
	private DatabaseManager databaseManager;
	private String outputDir;

	private Map<String, FixedSizeSequentialFileWriter> langVsFileWriter = new ConcurrentHashMap<>();

	public TwitterDataGroupingProcessor(String outputDir) {
		this.outputDir = outputDir;
		this.databaseManager = new DatabaseManager();
		this.databaseManager.setEntityClasses(Arrays.asList(UserVsTweetCountEntity.class, ProcessedFileInfoEntity.class,
				UserEntity.class, TwitterStatusEntity.class));
		try {
			databaseManager.init();
		} catch (DatabaseManagerException e) {
			logger.error("Error while initilizing db", e);
		}
	}

	public void groupTweetsByUser() {
		SessionFactory sessionFactory;
		try {
			sessionFactory = databaseManager.getSessionFactory();
		} catch (DatabaseManagerException e1) {
			return;
		}
		int offset = 0;
		int maxResults = 1000;
		Session session = sessionFactory.openSession();
		try {
			while (true) {
				List<UserEntity> list;
				Query createQuery = session.createQuery("from " + UserEntity.class.getName());
				createQuery.setFirstResult(offset * maxResults).setMaxResults(maxResults);
				offset++;
				list = createQuery.list();
				if (list.size() == 0) {
					return;
				} else {
					list.stream().filter((userEntity) -> {
						return "en".equals(userEntity.getLang());
					}).parallel().forEach(this::updateTweetCountForUser);
				}
			}
		} finally {
			closeSession(session);
		}
	}

	void updateTweetCountForUser(UserEntity userEntity) {
		SessionFactory sessionFactory;
		try {
			sessionFactory = databaseManager.getSessionFactory();
		} catch (DatabaseManagerException e) {
			return;
		}
		Session session = sessionFactory.openSession();
		Long count = 0l;
		try {
			count = (Long) session.createCriteria(TwitterStatusEntity.class)
					.add(Restrictions.eq("userEntity", userEntity)).setProjection(Projections.rowCount())
					.uniqueResult();

		} finally {
			closeSession(session);
		}
		try {
			databaseManager.save(new UserVsTweetCountEntity(userEntity.getId(), count));
		} catch (DatabaseManagerException e) {
			logger.error("Error while saving tweet count", e);
		}

	}

	private void closeSession(Session session) {
		if (session != null) {
			try {
				session.close();
			} catch (HibernateException e) {
				logger.error("Error while closing session: {}", e.getMessage(), e);
			}
		}
	}

	private void saveAllTweetsForUser(UserEntity userEntity) {
		logger.info("Processing tweets for user Id:{}, screen name:{} ", userEntity.getId(),
				userEntity.getScreenName());
		SessionFactory sessionFactory;
		try {
			sessionFactory = databaseManager.getSessionFactory();
		} catch (DatabaseManagerException e1) {
			return;
		}
		Session session = sessionFactory.openSession();
		try {
			Query query = session.createQuery(
					"from " + TwitterStatusEntity.class.getName() + " where userEntity=" + userEntity.getId());
			List list = query.list();
			userEntity.setTweets(list);
			String userTweetsJson = objectMapper.writeValueAsString(userEntity);
			String lang = userEntity.getLang();
			if (!langVsFileWriter.containsKey(lang)) {
				langVsFileWriter.putIfAbsent(lang, new FixedSizeSequentialFileWriter(outputDir + File.separator + lang,
						TWEET_FILE, JSON_FILE_EXT));
			}
			FixedSizeSequentialFileWriter fixedSizeSequentialFileWriter = langVsFileWriter.get(lang);
			fixedSizeSequentialFileWriter.write(userTweetsJson);

			logger.info("Successfully processed {} tweets for user Id:{}, screen name:{} ", list.size(),
					userEntity.getId(), userEntity.getScreenName());
		} catch (JsonProcessingException e) {
			logger.error("Error while getting json for tweet", e);
		} catch (IOException e) {
			logger.error("Error while writing tweet files", e);
		} finally {
			closeSession(session);
		}
	}

	static class FixedSizeSequentialFileWriter {
		private String parentFolderName;
		private String filePrefix;
		private String fileSuffix;
		private int fileSeq = 0;
		private long fileSize = 100 * 1024 * 1024;
		private long bytesWritten = 0;
		private FileOutputStream currentFileOutputtream;

		public FixedSizeSequentialFileWriter(String parentFolderName, String filePrefix, String fileSuffix) {
			this.parentFolderName = parentFolderName;
			this.filePrefix = filePrefix;
			this.fileSuffix = fileSuffix;
			File parentFolder = new File(parentFolderName);
			parentFolder.mkdirs();
		}

		public synchronized void write(String data) throws IOException {
			data = data + "\n";
			if (currentFileOutputtream == null) {
				this.currentFileOutputtream = new FileOutputStream(new File(getNextFileName()));
			}

			if (data.length() + bytesWritten > fileSize) {
				currentFileOutputtream.close();
				currentFileOutputtream = new FileOutputStream(new File(getNextFileName()));
				bytesWritten = 0;
			}
			currentFileOutputtream.write(data.getBytes(StandardCharsets.UTF_8));
			bytesWritten += data.length();
		}

		private String getNextFileName() {
			String fileName = parentFolderName + File.separator + filePrefix + fileSeq + fileSuffix;
			fileSeq++;
			return fileName;
		}
	}
}
