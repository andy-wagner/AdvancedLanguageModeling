package edu.berkeley.nlp.assignments.assign4;

import edu.berkeley.nlp.util.Indexer;

public class UnaryRule implements FeatureIndexable
{
	public final String parent;

	public final String child;

	private final int index;

	/**
	 * Only used if this rule is a "fake" unary rule that is not featurized.
	 * Otherwise, the score lives in the Weights vector
	 */
	private double score;

	public String getParent() {
		return parent;
	}

	public String getChild() {
		return child;
	}

	public double getScore(Weights weights) {
		return index < 0 ? score : weights.getWeight(index);
	}

	public void setScore(Weights weights, double score) {
		if (index < 0)
			this.score = score;
		else
			weights.setScore(index, score);
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof UnaryRule)) return false;

		final UnaryRule unaryRule = (UnaryRule) o;

		if (child != null ? !child.equals(unaryRule.child) : unaryRule.child != null) return false;
		if (parent != null ? !parent.equals(unaryRule.parent) : unaryRule.parent != null) return false;

		return true;
	}

	public int hashCode() {
		int result;
		result = (parent != null ? parent.hashCode() : 0);
		result = 29 * result + (child != null ? child.hashCode() : 0);
		return result;
	}

	public String toString() {
		return parent + " -> " + child;
	}

	/**
	 * Used for fake unary rules created by UnaryClosure
	 * 
	 * @param parent
	 * @param child
	 */
	public UnaryRule(String parent, String child) {
		this.index = -1;
		this.parent = parent;
		this.child = child;
	}

	public UnaryRule(String parent, String child, Indexer<FeatureIndexable> featureIndexer) {
		this.parent = parent;
		this.child = child;
		this.index = featureIndexer.addAndGetIndex(this);
	}

}