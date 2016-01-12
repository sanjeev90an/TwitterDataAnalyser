package mmds.twitter.analyser.dataprocessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserDataModel {

	private static final Logger logger = LoggerFactory.getLogger(TwitterRawDataProcessor.class);

	public static final UserDataModel INSTANCE = new UserDataModel();

	private Map<Long, List<TwitterStatusEntity>> userIdVsTweets = new HashMap<>();

	private List<Long> userIds = new ArrayList<>();

	private int noOfUsers = 500;
	private long minNoOfTweets = 60;
	private long maxNoOfTweets = 70;

	private static ObjectMapper objectMapper = new ObjectMapper();

	private AtomicInteger count = new AtomicInteger(0);

	private DatabaseManager databaseManager;

	@SuppressWarnings("unchecked")
	public UserDataModel() {
		databaseManager = new DatabaseManager();
		databaseManager.setEntityClasses(Arrays.asList(UserVsTweetCountEntity.class, UserEntity.class,
				TwitterStatusEntity.class, UserSurveyResultEntity.class));
		try {
			databaseManager.init();
			SessionFactory sessionFactory = databaseManager.getSessionFactory();
			Session session = sessionFactory.openSession();
			try {
				List<UserVsTweetCountEntity> list = session.createCriteria(UserVsTweetCountEntity.class)
						.add(Restrictions.gt("count", minNoOfTweets)).add(Restrictions.lt("count", maxNoOfTweets))
						.addOrder(Order.desc("count")).setMaxResults(noOfUsers).list();
				Set<Long> userIdSet = list.stream().map(UserVsTweetCountEntity::getId).collect(Collectors.toSet());
				List<UserEntity> userEntities = session.createCriteria(UserEntity.class)
						.add(Restrictions.in("id", userIdSet)).list();
				List<TwitterStatusEntity> tweets = session.createCriteria(TwitterStatusEntity.class)
						.add(Restrictions.in("userEntity", userEntities)).add(Restrictions.eq("lang", "en")).list();
				userIdVsTweets = tweets.stream().collect(Collectors.groupingBy((twitterStatusEntity) -> {
					return twitterStatusEntity.getUserEntity().getId();
				}));
				List<Entry<Long, List<TwitterStatusEntity>>> collect = userIdVsTweets.entrySet().stream()
						.filter((e) -> e.getValue() == null || e.getValue().size() < minNoOfTweets)
						.collect(Collectors.toList());
				userIdVsTweets.entrySet().removeAll(collect);
				userIds.addAll(userIdVsTweets.keySet());
			} finally {
				if (session != null) {
					session.close();
				}
			}
		} catch (DatabaseManagerException e) {
			logger.error("Error while intilizing array");
		}

	}

	public UserEntity getNextUser(int noOfTweets) {
		Long userId = userIds.get(count.getAndIncrement() % userIds.size());
		List<TwitterStatusEntity> tweets = userIdVsTweets.get(userId);
		Collections.shuffle(tweets);
		UserEntity userEntity = new UserEntity(userId,
				tweets.subList(0, noOfTweets < tweets.size() ? noOfTweets : tweets.size()));
		return userEntity;
	}

	public void submitSurveyForUser(long userId, List<String> surveyResults, String source) {
		String surveyResultString;
		try {
			surveyResultString = objectMapper.writeValueAsString(surveyResults);
		} catch (JsonProcessingException e) {
			logger.error("Error while serializing survey results", e);
			return;
		}
		UserSurveyResultEntity userSurveyResultEntity = new UserSurveyResultEntity(userId, surveyResultString, source);
		try {
			databaseManager.save(userSurveyResultEntity);
		} catch (DatabaseManagerException e) {
			logger.error("Error while saving survey results");
		}
	}

}
