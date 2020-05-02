package edu.berkeley.nlp.assignments.assign4;

import edu.berkeley.nlp.util.Counter;
import edu.berkeley.nlp.util.Indexer;

/**
 * Features (word, tag) pairs with only one feature. This feature is the log of
 * a smoothed estimate of a p(tag | word).
 * 
 * @author adampauls
 * 
 */
public class BasicLexiconFeaturizer implements LexiconFeaturizer
{
	public static final FeatureIndexable DEFAULT_LEXICON_FEATURE_OBJECT = new FeatureIndexable()
	{
		@Override
		public boolean equals(Object o) {
			return this == o;
		}

		@Override
		public int hashCode() {
			return System.identityHashCode(this);
		}
	};

	public Counter<Integer> getFeatures(Lexicon lexicon, String word, String tag, Indexer<FeatureIndexable> featureIndexer) {
		final int totalTokens = lexicon.getTotalTokens();
		double p_tag = lexicon.getTagCounter().getCount(tag) / totalTokens;
		double c_word = lexicon.getWordCounter().getCount(word);
		double c_tag_and_word = lexicon.getWordToTagCounters().getCount(word, tag);
		if (c_word < 10) { // rare or unknown
			c_word += 1.0;
			c_tag_and_word += lexicon.getTypeTagCounter().getCount(tag) / lexicon.getTotalWordTypes();
		}
		double p_word = (1.0 + c_word) / (totalTokens + 1.0);
		double p_tag_given_word = c_tag_and_word / c_word;
		Counter<Integer> features = new Counter<Integer>();
		final double score = Math.log(p_tag_given_word / p_tag * p_word);
		features.setCount(featureIndexer.addAndGetIndex(DEFAULT_LEXICON_FEATURE_OBJECT), score);
		return features;
	}
}
