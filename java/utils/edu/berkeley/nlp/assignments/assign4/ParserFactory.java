package edu.berkeley.nlp.assignments.assign4;

import java.util.List;

import edu.berkeley.nlp.ling.Tree;
import edu.berkeley.nlp.util.Indexer;

public interface ParserFactory
{
	Parser getParser(List<Tree<String>> trainTrees, Indexer<FeatureIndexable> featureIndexer, Weights weights);

}
