package edu.berkeley.nlp.assignments.assign4;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.berkeley.nlp.ling.Tree;
import edu.berkeley.nlp.parser.EnglishPennTreebankParseEvaluator;
import edu.berkeley.nlp.parser.LabeledConstituent;
import edu.berkeley.nlp.parser.EnglishPennTreebankParseEvaluator.LabeledConstituentEval;
import edu.berkeley.nlp.util.Counter;

/**
 * Parsers are required to map sentences to trees. How a parser is constructed
 * and trained is not specified.
 */
public interface Parser
{
	Tree<String> getBestParse(List<String> sentence);

	public static class StaticMethods
	{

		/**
		 * Gets the feature vector corresponding to a parse tree. Note that the features that apply to rules in the tree are 
		 * determined by Grammar and cannot be changed.
		 * (word, tag) pairs, however, can are featurized by lexicon.getLexiconFeaturizer().
		 * */
		public static Counter<Integer> getFeatures(Grammar grammar, Lexicon lexicon, Tree<String> tree) {
			Counter<Integer> features = new Counter<Integer>();
			grammar.addFeatures(features, tree);
			lexicon.addFeatures(features, tree);
			return features;
		}

		/**
		 * Computes the number of incorrect labeled spans in guessTree relative
		 * to goldTree.
		 * 
		 * @param goldTree
		 * @param guessTree
		 * @return
		 */
		public static double getHammingLoss(Tree<String> goldTree, Tree<String> guessTree) {
			LabeledConstituentEval<String> labeledConstituentEval = new EnglishPennTreebankParseEvaluator.LabeledConstituentEval<String>(
				Collections.<String> emptySet(), Collections.<String> emptySet());
			Set<LabeledConstituent<String>> guessObjects = labeledConstituentEval.makeObjects(guessTree);
			Set<LabeledConstituent<String>> goldObjects = labeledConstituentEval.makeObjects(goldTree);
			guessObjects.removeAll(goldObjects);
			return guessObjects.size();
		}

	}
}