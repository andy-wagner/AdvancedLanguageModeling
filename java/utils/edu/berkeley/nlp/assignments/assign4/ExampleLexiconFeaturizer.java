package edu.berkeley.nlp.assignments.assign4;

import edu.berkeley.nlp.util.Counter;
import edu.berkeley.nlp.util.Indexer;

/**
 * An example of a lexicon featurizer with one feature template.
 * 
 * @author adampauls
 * 
 */
public class ExampleLexiconFeaturizer implements LexiconFeaturizer
{

	
	private static class FirstLetterFeatureTemplate implements FeatureIndexable
	{
		private String tag;

		private String firstLetter;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((firstLetter == null) ? 0 : firstLetter.hashCode());
			result = prime * result + ((tag == null) ? 0 : tag.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			FirstLetterFeatureTemplate other = (FirstLetterFeatureTemplate) obj;
			if (firstLetter == null) {
				if (other.firstLetter != null) return false;
			} else if (!firstLetter.equals(other.firstLetter)) return false;
			if (tag == null) {
				if (other.tag != null) return false;
			} else if (!tag.equals(other.tag)) return false;
			return true;
		}

		/**
		 * @param tag
		 * @param firstLetter
		 */
		public FirstLetterFeatureTemplate(String tag, String firstLetter) {
			super();
			this.tag = tag;
			this.firstLetter = firstLetter;
		}

	}

	public Counter<Integer> getFeatures(Lexicon lexicon, String word, String tag, Indexer<FeatureIndexable> featureIndexer) {

		Counter<Integer> features = new Counter<Integer>();
		features.setCount(featureIndexer.addAndGetIndex(new FirstLetterFeatureTemplate(tag, word.substring(0, 1))), 1.0);
		return features;
	}
}
