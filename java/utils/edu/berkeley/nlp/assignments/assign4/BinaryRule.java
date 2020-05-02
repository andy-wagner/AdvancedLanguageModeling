package edu.berkeley.nlp.assignments.assign4;

import edu.berkeley.nlp.util.Indexer;

public class BinaryRule implements FeatureIndexable
{
	public final String parent;

	public final String leftChild;

	public final String rightChild;

	private final int index;

	public String getParent() {
		return parent;
	}

	public String getLeftChild() {
		return leftChild;
	}

	public String getRightChild() {
		return rightChild;
	}

	public double getScore(Weights weights) {
		return weights.getWeight(index);
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BinaryRule)) return false;

		final BinaryRule binaryRule = (BinaryRule) o;

		if (leftChild != null ? !leftChild.equals(binaryRule.leftChild) : binaryRule.leftChild != null) return false;
		if (parent != null ? !parent.equals(binaryRule.parent) : binaryRule.parent != null) return false;
		if (rightChild != null ? !rightChild.equals(binaryRule.rightChild) : binaryRule.rightChild != null) return false;

		return true;
	}

	public int hashCode() {
		int result;
		result = (parent != null ? parent.hashCode() : 0);
		result = 29 * result + (leftChild != null ? leftChild.hashCode() : 0);
		result = 29 * result + (rightChild != null ? rightChild.hashCode() : 0);
		return result;
	}

	public String toString() {
		return parent + " -> " + leftChild + " " + rightChild;
	}

	public BinaryRule(String parent, String leftChild, String rightChild, Indexer<FeatureIndexable> indexer) {
		this.parent = parent;
		this.leftChild = leftChild;
		this.rightChild = rightChild;
		this.index = indexer.addAndGetIndex(this);
	}

	public void setScore(Weights weights, double score) {
		weights.setScore(index, score);
	}
}