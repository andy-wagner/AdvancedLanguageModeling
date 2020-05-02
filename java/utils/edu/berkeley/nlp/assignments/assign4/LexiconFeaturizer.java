package edu.berkeley.nlp.assignments.assign4;

import edu.berkeley.nlp.util.Counter;
import edu.berkeley.nlp.util.Indexer;

/**
 * Featurizes (word, tag) pairs. You should make your own implementation do get
 * interesting features on word/tag pairs.
 * 
 * @author adampauls
 * 
 */
public interface LexiconFeaturizer
{

	/**
	 * Extracts features for a (word, tag pair)
	 * 
	 * @param lexicon
	 * @param word
	 * @param tag
	 * @param featureIndexer
	 * @return
	 */
	public Counter<Integer> getFeatures(Lexicon lexicon, String word, String tag, Indexer<FeatureIndexable> featureIndexer);

}
