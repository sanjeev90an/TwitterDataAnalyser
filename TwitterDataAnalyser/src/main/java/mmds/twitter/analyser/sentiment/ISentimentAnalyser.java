package mmds.twitter.analyser.sentiment;

public interface ISentimentAnalyser {

	SentimentEnum getSentimentForText(String text);
}
