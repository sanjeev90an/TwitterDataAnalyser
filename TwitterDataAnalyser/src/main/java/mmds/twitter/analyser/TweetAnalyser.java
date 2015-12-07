package mmds.twitter.analyser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;

import mmds.twitter.analyser.sentiment.ISentimentAnalyser;
import mmds.twitter.analyser.sentiment.SentimentEnum;
import mmds.twitter.analyser.sentiment.StanfordSentimentAnalyser;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

public class TweetAnalyser {
	private static Logger logger = LoggerFactory.getLogger(TweetAnalyser.class);

	private ISentimentAnalyser sentimentAnalyser;

	public TweetAnalyser() {
		sentimentAnalyser = new StanfordSentimentAnalyser();
	}

	public void analyse() {
		List<String> tweets = new ArrayList<String>();
		String twitterFilePath = "00.json";
		try {
			tweets = Resources.readLines(Resources.getResource(twitterFilePath), StandardCharsets.UTF_8);
		} catch (IOException e) {
			logger.error("Error while reading tweets", e);
		}

		List<Status> tweetStatusList = new ArrayList<Status>();
		for (String tweet : tweets) {
			try {
				Status status = TwitterObjectFactory.createStatus(tweet);
				tweetStatusList.add(status);
				logger.info(status.toString());
			} catch (TwitterException e) {
				logger.error("Error while parsing tweet:{}", tweet);
			}
		}

		for (Status tweetStatus : tweetStatusList) {
			String text = tweetStatus.getText();
			SentimentEnum sentimentForText = sentimentAnalyser.getSentimentForText(text);
			logger.info("Tweet text:{}, Sentiment:{}", text, sentimentForText);
		}
	}

	public static void main(String[] args) {
		new TweetAnalyser().analyse();
	}
}
