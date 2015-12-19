package mmds.twitter.analyser;

import mmds.twitter.analyser.dataprocessor.TwitterDataGroupingProcessor;
import mmds.twitter.analyser.dataprocessor.TwitterRawDataProcessor;

public class TwitterDataProcessor {

	public static void main(String[] args) {
		System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "10");
		String operation = args[0];
		if ("buildDB".equals(operation)) {
			String baseDirName = args[1];
			new TwitterRawDataProcessor(baseDirName).processDirRecursively();
		} else if ("groupTweets".equals(operation)) {
			String outputFileName = args[1];
			new TwitterDataGroupingProcessor(outputFileName).groupTweetsByUser();
		} else {
			System.out.println("Invalid option. Valid options: buildDB, groupTweets");
		}
		System.exit(0);
	}
}
