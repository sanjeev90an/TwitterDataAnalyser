package mmds.twitter.analyser.dataprocessor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.User;

public class TwitterRawDataProcessor {

	private static final Logger logger = LoggerFactory.getLogger(TwitterRawDataProcessor.class);

	private DatabaseManager databaseManager;
	private String inputDir;

	public TwitterRawDataProcessor(String inputDir) {
		this.inputDir = inputDir;
		this.databaseManager = new DatabaseManager();
		this.databaseManager.setEntityClasses(Arrays.asList(UserEntity.class, TwitterStatusEntity.class));
		try {
			databaseManager.init();
		} catch (DatabaseManagerException e) {
			logger.error("Error while initilizing db", e);
		}
	}

	public void processDirRecursively() {
		processDirRecursively(new File(inputDir));
	}

	private void processDirRecursively(File dir) {
		logger.info("Inside directory :{}", dir.getAbsolutePath());
		File[] files = dir.listFiles((FileFilter) FileFilterUtils.fileFileFilter());
		File[] dirs = dir.listFiles((FileFilter) FileFilterUtils.directoryFileFilter());
		Arrays.stream(dirs).parallel().forEach(d -> processDirRecursively(d));
		Arrays.stream(files).parallel().forEach(file -> processFile(file));
		logger.info("Processed {} files and {} dirs inside directory :{}", files.length, dirs.length,
				dir.getAbsolutePath());
	}

	private void processFile(File file) {
		logger.info("Processing tweet file:{}", file.getAbsolutePath());
		try (CompressorInputStream compressorInputStream = new CompressorStreamFactory()
				.createCompressorInputStream(new BufferedInputStream(new FileInputStream(file)));) {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(compressorInputStream, StandardCharsets.UTF_8));
			bufferedReader.lines().forEach(line -> processTweetJson(line));
		} catch (Exception e) {
			logger.error("Error while processing file:{}", file.getAbsolutePath(), e);
		}
		logger.info("Successfully processed tweet file:{}", file.getAbsolutePath());
	}

	private void processTweetJson(String tweetJson) {
		try {
			Status tweetStatus = TwitterObjectFactory.createStatus(tweetJson);
			if (tweetStatus.isRetweet()) {
				logger.warn("Not saving tweet as it was retweeted, id:{}, text:{}", tweetStatus.getId(),
						tweetStatus.getText());
				return;
			}
			User user = tweetStatus.getUser();
			UserEntity userEntity;
			try {
				userEntity = databaseManager.get(UserEntity.class, user.getId());
			} catch (EntityNotFoundException e) {
				userEntity = new UserEntity(user);
				databaseManager.save(userEntity);
			}
			TwitterStatusEntity twitterStatusEntity = new TwitterStatusEntity(tweetStatus);
			twitterStatusEntity.setUserEntity(userEntity);
			databaseManager.saveOrUpdate(twitterStatusEntity);
			logger.info("Successfully processed tweet. id:{}, text:{}", tweetStatus.getId(), tweetStatus.getText());
		} catch (TwitterException e) {
			logger.debug("Error while processing tweet json");
		} catch (DatabaseManagerException e) {
			logger.error("Error while saving twitter entity", e);
		}
	}

}
