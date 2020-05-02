package edu.berkeley.nlp.assignments.assign1.student;

import edu.berkeley.nlp.langmodel.EnglishWordIndexer;
import edu.berkeley.nlp.util.StringIndexer;

import java.util.List;

public class KNTrigramDB {
	/**
	 * Database which stores all of the tables for the KNTrigram Model
	 */

	double loadFactor = .85;

	// The following int arrays are where counts are stored
	TriIndexer TrigramEmpirical = new TriIndexer(44000000, 47000000, loadFactor);
	// The following int arrays are where counts are store
	// Col1: Bigram Empirical	|	Col2: BigramDOT		|	Col3: DOTBigram
	BiIndexer BigramIndexer = new BiIndexer(10000000, 3, 10000000, loadFactor);
	// Col1: Unigram Empirical Counts | Col2: UnigramDOT | Col3: DOTUnigramDOT | Col4: DOTUnigram
	int[][] UnigramCounts = new int[600000][4];
	int UnigramCountHead = 0;
	StringIndexer UnigramEmpirical;

	public KNTrigramDB(){
		UnigramEmpirical = EnglishWordIndexer.getIndexer();
	}

	public String intToBinary(long INT) {

		String s = Long.toBinaryString(INT);
		if (s.length() > 20){
			System.out.println("[intToBinary]: INT was longer than 20");
			System.exit(-1);
		}
		while (s.length() < 20){
			s = "0" + s;
		}
		return s;
	}

	public long twoBinStrsToInt(String w1, String w2){

		String twoStrs = w1 + w2;
		long LInt = Long.parseLong(twoStrs, 2);

		return LInt;

	}

	public long threeBinStrsToInt(String prefix, String w0, String w1, String w2){

		String threeStrs = prefix + w0 + w1 + w2;
		long LInt = Long.parseLong(threeStrs, 2);

		return LInt;

	}
} 