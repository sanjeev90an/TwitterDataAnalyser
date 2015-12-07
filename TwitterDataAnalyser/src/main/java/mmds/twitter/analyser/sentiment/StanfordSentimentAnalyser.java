package mmds.twitter.analyser.sentiment;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Properties;

import org.ejml.simple.SimpleMatrix;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class StanfordSentimentAnalyser implements ISentimentAnalyser {

	private static final NumberFormat NF = new DecimalFormat("0.0000");

	public StanfordSentimentAnalyser() {

	}

	public SentimentEnum getSentimentForText(String text) {
		Properties tokenizerProps = new Properties();
		tokenizerProps.setProperty("annotators", "tokenize, ssplit");
		StanfordCoreNLP tokenizer = new StanfordCoreNLP(tokenizerProps);
		Properties pipelineProps = new Properties();
		pipelineProps.setProperty("annotators", "parse, sentiment");
		pipelineProps.setProperty("enforceRequirements", "false");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(pipelineProps);
		Annotation annotation = tokenizer.process(text);
		pipeline.annotate(annotation);
		for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
			Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
			Tree copy = tree.deepCopy();
			setIndexLabels(copy, 0);
			System.out.println(copy);
			outputTreeScores(System.out, tree, 0);
		}
		return null;
	}

	static int setIndexLabels(Tree tree, int index) {
		if (tree.isLeaf()) {
			return index;
		}

		tree.label().setValue(Integer.toString(index));
		index++;
		for (Tree child : tree.children()) {
			index = setIndexLabels(child, index);
		}
		return index;
	}

	static int outputTreeScores(PrintStream out, Tree tree, int index) {
		if (tree.isLeaf()) {
			return index;
		}

		out.print("  " + index + ":");
		SimpleMatrix vector = RNNCoreAnnotations.getPredictions(tree);
		for (int i = 0; i < vector.getNumElements(); ++i) {
			out.print("  " + NF.format(vector.get(i)));
		}
		out.println();
		index++;
		for (Tree child : tree.children()) {
			index = outputTreeScores(out, child, index);
		}
		return index;
	}
}
