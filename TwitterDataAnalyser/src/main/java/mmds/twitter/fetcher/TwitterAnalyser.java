package mmds.twitter.fetcher;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

public class TwitterAnalyser {
	private static Logger logger = LoggerFactory.getLogger(TwitterAnalyser.class);

	public static void main(String[] args) {
		List<String> tweets = new ArrayList<String>();
		String twitterFilePath = "00.json";
		try {
			tweets = Resources.readLines(Resources.getResource(twitterFilePath), StandardCharsets.UTF_8);
		} catch (IOException e) {
			logger.error("Error while reading tweets", e);
		}

		for (String tweet : tweets) {
			try {
				Status status = TwitterObjectFactory.createStatus(tweet);
				logger.info(status.toString());
			} catch (TwitterException e) {
				logger.error("Error while parsing tweet", e);
			}
		}
	}
}
