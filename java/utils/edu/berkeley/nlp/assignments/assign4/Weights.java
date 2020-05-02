package edu.berkeley.nlp.assignments.assign4;

import java.util.Arrays;
import java.util.Map.Entry;

import edu.berkeley.nlp.util.Counter;
import edu.berkeley.nlp.util.IndexerObserver;
import edu.berkeley.nlp.util.IntCounter;

/**
 * Stores weights for use in parsing. This weight vector size grows as the
 * featureIndexer grows.
 * 
 * We construct an instance of Weights for you in PCFGParserTester. It's
 * proabably easiest to modify that one instance when training the weight and
 * not construct an instance yourself.
 * 
 * @author adampauls
 * 
 */
public class Weights implements IndexerObserver<FeatureIndexable>
{

	private double[] weights = new double[10];

	private int maxFeatureIndex = -1;

	public Weights() {
	}

	public Weights(double[] weights) {
		this.weights = weights;
		this.maxFeatureIndex = weights.length - 1;
	}
	
	/**
	 * Copies
	 * @param weights
	 */
	public Weights(Weights weights) {
		this.weights = weights.weights;
		this.maxFeatureIndex = weights.weights.length - 1;
		
	}

	public double getWeight(int index) {
		if (index > maxFeatureIndex) throw new IndexOutOfBoundsException("Feature index never added to weights");
		return weights[index];
	}

	public void addIndex(int index) {
		if (index >= weights.length) weights = Arrays.copyOf(weights, Math.max(index + 1, weights.length * 3 / 2));
		maxFeatureIndex = Math.max(maxFeatureIndex, index);
	}

	public void setScore(int index, double score) {
		addIndex(index);
		weights[index] = score;
	}

	public void handleIndexAdd(FeatureIndexable element, int index) {
		addIndex(index);
	}

	public void setWeight(int index, double w) {
		addIndex(index);
		weights[index] = w;
	}

	public double dotProduct(IntCounter delta) {
		return delta.dotProduct(weights);
	}

	public double dotProduct(Counter<Integer> features) {
		double score = 0.0;
		for (Entry<Integer, Double> entry : features.getEntrySet()) {
			if (entry.getKey() >= weights.length) {
				addIndex(entry.getKey());
			}
			score += weights[entry.getKey()] * entry.getValue();
		}
		return score;
	}

	/**
	 * Dot product with self.
	 * 
	 * @return
	 */
	public double normSquared() {
		double squaredSum = 0;
		double[] a = weights;
		for (int i = 0; i < a.length; i++) {
			squaredSum += a[i] * a[i];
		}
		return squaredSum;
	}

	/**
	 * Adds all entries stored in the sparse vector delta, multiplied by scale
	 * 
	 * @param delta
	 * @param scale
	 */
	public void sparseUpdate(IntCounter delta, double scale) {
		if (scale == 0.0) return;
		IntCounter.incrementDenseArray(weights, delta, scale);
	}

	/**
	 * Adds all entries stored in the sparse vector delta, multiplied by scale
	 * 
	 * @param delta
	 * @param scale
	 */
	public void sparseUpdate(Counter<Integer> delta, double scale) {
		if (scale == 0.0) return;
		for (Entry<Integer, Double> entry : delta.getEntrySet()) {
			if (entry.getKey() >= weights.length) {
				addIndex(entry.getKey());
			}
			weights[entry.getKey()] += scale * entry.getValue();
		}
	}

	public void zero() {
		Arrays.fill(weights, 0.0);
	}

	/**
	 * Gives access to the underlying array. Note that the underlying array gets
	 * reallocated from time to time, so don't keep references tho this thing
	 * around.
	 * 
	 * @return
	 */
	public double[] getArray() {
		return weights;
	}

}
