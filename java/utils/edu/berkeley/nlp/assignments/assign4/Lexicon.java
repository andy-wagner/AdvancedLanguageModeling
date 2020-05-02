package edu.berkeley.nlp.assignments.assign4;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import edu.berkeley.nlp.ling.Tree;
import edu.berkeley.nlp.util.Counter;
import edu.berkeley.nlp.util.CounterMap;
import edu.berkeley.nlp.util.Indexer;
import edu.berkeley.nlp.util.IntCounter;

/**
 * Simple default implementation of a lexicon, which scores word, tag pairs with
 * a smoothed estimate of P(tag|word)/P(tag).
 */
public class Lexicon
{
	public LexiconFeaturizer getLexiconFeaturizer() {
		return lexiconFeaturizer;
	}

	CounterMap<String, String> wordToTagCounters = new CounterMap<String, String>();

	int totalTokens = 0;

	int totalWordTypes = 0;

	Counter<String> tagCounter = new Counter<String>();

	Counter<String> wordCounter = new Counter<String>();

	Counter<String> typeTagCounter = new Counter<String>();

	private Indexer<FeatureIndexable> featureIndexer;

	private LexiconFeaturizer lexiconFeaturizer;

	private Weights weights;

	public Set<String> getAllTags() {
		return tagCounter.keySet();
	}

	public boolean isKnown(String word) {
		return wordCounter.keySet().contains(word);
	}

	/**
	 * Be careful to check this return value for Double.NaN and Double.NEGATIVE_INFINITY, both of 
	 * which indicate that the given tagging has 0 probability.
	 * @param word
	 * @param tag
	 * @return
	 */
	public double scoreTagging(String word, String tag) {
		return weights.dotProduct(lexiconFeaturizer.getFeatures(this, word, tag, featureIndexer));

	}

	public Lexicon(List<Tree<String>> trainTrees, Indexer<FeatureIndexable> featureIndexer, LexiconFeaturizer lexiconFeaturizer, Weights weights) {
		this.featureIndexer = featureIndexer;
		this.weights = weights;
		this.lexiconFeaturizer = lexiconFeaturizer;
		for (Tree<String> trainTree : trainTrees) {
			List<String> words = trainTree.getYield();
			List<String> tags = trainTree.getPreTerminalYield();
			for (int position = 0; position < words.size(); position++) {
				String word = words.get(position);
				String tag = tags.get(position);
				tallyTagging(word, tag);
			}
		}
	}

	private void tallyTagging(String word, String tag) {
		if (!isKnown(word)) {
			totalWordTypes += 1.0;
			typeTagCounter.incrementCount(tag, 1.0);
		}
		totalTokens += 1.0;
		tagCounter.incrementCount(tag, 1.0);
		wordCounter.incrementCount(word, 1.0);
		wordToTagCounters.incrementCount(word, tag, 1.0);
	}

	public int getTotalTokens() {
		return totalTokens;
	}

	public Counter<String> getTypeTagCounter() {
		return typeTagCounter;
	}

	public double getTotalWordTypes() {
		return totalWordTypes;
	}

	public Counter<String> getTagCounter() {
		return tagCounter;
	}

	public Counter<String> getWordCounter() {
		return wordCounter;
	}

	public CounterMap<String, String> getWordToTagCounters() {
		return wordToTagCounters;
	}

	public void addFeatures(Counter<Integer> features, Tree<String> tree) {
		for (Tree<String> t : tree.getPostOrderTraversal()) {
			if (!t.isPreTerminal()) continue;
			Counter<Integer> currFeatures = lexiconFeaturizer.getFeatures(this, t.getChildren().get(0).getLabel(), t.getLabel(), featureIndexer);
			features.incrementAll(currFeatures);
		}
	}
}