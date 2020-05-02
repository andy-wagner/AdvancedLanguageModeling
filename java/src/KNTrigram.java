package edu.berkeley.nlp.assignments.assign1.student;

import edu.berkeley.nlp.langmodel.NgramLanguageModel;

import java.util.ArrayList;
import java.util.List;
//import LmFactory;

/**
 * Kneser-Ney trigram model.
 * 
 * 
 * @author jakemdaly
 * 
 * 
 */
public class KNTrigram implements NgramLanguageModel
{

	
	// Internal data here
	KNTrigramDB db;
	

	public KNTrigram(Iterable<List<String>> sentenceCollection) {
		System.out.println("Building KNTrigram model. . .");

		db = new KNTrigramDB();
		int sent = 0;

		for (List<String> sentence : sentenceCollection) {
			sent++;
			if (sent % 100000 == 0) {
				System.out.println("On sentence " + sent);
				//				System.out.println(db.UnigramEmpirical.size());
				//				System.out.println(db.UnigramCountHead);
				//				System.out.println(db.BigramIndexer.CountHead);
				//				System.out.println(db.TrigramEmpirical.CountHead);
			}

			List<String> stoppedSentence = new ArrayList<String>(sentence);
			stoppedSentence.add(0, NgramLanguageModel.START);
			stoppedSentence.add(0, NgramLanguageModel.START);
			stoppedSentence.add(STOP);


			long index_w0 = db.UnigramEmpirical.addAndGetIndex(stoppedSentence.get(0));
			db.UnigramCounts[Math.toIntExact(index_w0)][0]++;
			long index_w1 = db.UnigramEmpirical.addAndGetIndex(stoppedSentence.get(1));
			db.UnigramCounts[Math.toIntExact(index_w1)][0]++;

			long w0w1 = db.twoBinStrsToInt(db.intToBinary(index_w0), db.intToBinary(index_w1));
			long index_w0w1 = db.BigramIndexer.addAndGetIndex(w0w1);
			db.BigramIndexer.Count[Math.toIntExact(index_w0w1)][0]++;

			for (int w = 2; w < stoppedSentence.size(); w++) {
				long index_prev1 = db.UnigramEmpirical.addAndGetIndex(stoppedSentence.get(w-1));
				long index_prev2 = db.UnigramEmpirical.addAndGetIndex(stoppedSentence.get(w-2));
				long prev2_prev1 = db.twoBinStrsToInt(db.intToBinary(index_prev2), db.intToBinary(index_prev1));

				// Unigram
				int didUnigramEmpricalCountsGrow = db.UnigramEmpirical.size();
				long index_i = db.UnigramEmpirical.addAndGetIndex(stoppedSentence.get(w));
				db.UnigramCounts[Math.toIntExact(index_i)][0]++;
				didUnigramEmpricalCountsGrow = db.UnigramEmpirical.size() - didUnigramEmpricalCountsGrow;
				if (didUnigramEmpricalCountsGrow==1) {
					db.UnigramCountHead++;
				}

				// Bigram
				int didCountHeadAdvanceBigram = db.BigramIndexer.CountHead;
				long prev1_i = db.twoBinStrsToInt(db.intToBinary(index_prev1), db.intToBinary(index_i));
				long index_prev1_i = db.BigramIndexer.addAndGetIndex(prev1_i);
				db.BigramIndexer.Count[Math.toIntExact(index_prev1_i)][0]++;
				didCountHeadAdvanceBigram = db.BigramIndexer.CountHead - didCountHeadAdvanceBigram;

				// Trigram
				int didCountHeadAdvanceTrigram = db.TrigramEmpirical.CountHead;
				long prev2_prev1_i = db.threeBinStrsToInt("0000", db.intToBinary(index_prev2), db.intToBinary(index_prev1), db.intToBinary(index_i));
				long index_prev2_prev1_i = db.TrigramEmpirical.addAndGetIndex(prev2_prev1_i);
				db.TrigramEmpirical.Count[Math.toIntExact(index_prev2_prev1_i)]++;
				didCountHeadAdvanceTrigram = db.TrigramEmpirical.CountHead - didCountHeadAdvanceTrigram;


				// If it was an unseen bigram, it will be both a new DOTUnigram as well as UnigramDOT
				if (didCountHeadAdvanceBigram > 0) {
					db.UnigramCounts[Math.toIntExact(index_i)][3]++;
					db.UnigramCounts[Math.toIntExact(index_prev1)][1]++;
				}


				if (didCountHeadAdvanceTrigram > 0){
					long index_dotbigram = db.BigramIndexer.addAndGetIndex(prev1_i);
					db.BigramIndexer.Count[Math.toIntExact(index_dotbigram)][2]++;
					long index_bigramdot = db.BigramIndexer.addAndGetIndex(prev2_prev1);
					db.BigramIndexer.Count[Math.toIntExact(index_bigramdot)][1]++;
					// Also implies that we haven't seen the DOTunigramDOT before either
					db.UnigramCounts[Math.toIntExact(index_prev1)][2]++;
				}


			}

		}

		// Convert trigram indexes to counts
		for (int i = 0; i < db.TrigramEmpirical.indexes.values.length; i++) {
			int index = db.TrigramEmpirical.indexes.values[i];
			if (index >= 0) {
				db.TrigramEmpirical.indexes.values[i] = db.TrigramEmpirical.Count[index];
			}
		}
		db.TrigramEmpirical.Count = null;
		
		System.out.println("Done building KNTrigram.");
		
	}

	public int getOrder() {
		return 3;
	}

	public double getNgramLogProbability(int[] ngram, int from, int to) {

		double DISC_TRI = .75;
		double DISC_BIG = .75;


		if (to - from == 3) {

			// Get index representations
			String w0_binary = db.intToBinary(ngram[from]);
			String w1_binary = db.intToBinary(ngram[from+1]);
			String w2_binary = db.intToBinary(ngram[from+2]);
			long w0w1 = db.twoBinStrsToInt(w0_binary, w1_binary);
			long w1w2 = db.twoBinStrsToInt(w1_binary, w2_binary);
			long w0w1w2 = db.threeBinStrsToInt("0000", w0_binary, w1_binary, w2_binary);

			double trig_numerator = 0;
			double trig_denominator = 0;
			// Determine if we've seen it or not. If not, do nothing, if yes, set that as the numerator
			int trig_numerator_index = db.TrigramEmpirical.indexes.get(w0w1w2);
			if (trig_numerator_index >= 0) { trig_numerator = trig_numerator_index;}
			int trig_denominator_index = db.BigramIndexer.indexes.get(w0w1);
			if (trig_denominator_index >= 0) {trig_denominator = db.BigramIndexer.Count[trig_denominator_index][0];}
			double TRIG_TERM;
			double ALPHA_TRI;
			trig_numerator = Math.max(trig_numerator-DISC_TRI, 0);
			if ((trig_numerator != 0) && (trig_denominator != 0)) {
				TRIG_TERM = trig_numerator/trig_denominator;
			} else {
				TRIG_TERM = 0;
			}
			if (db.BigramIndexer.indexes.get(w0w1) < 0 || db.BigramIndexer.indexes.get(w0w1) >= db.BigramIndexer.CountHead){
				ALPHA_TRI = 1;
			} else {
				ALPHA_TRI = DISC_TRI * db.BigramIndexer.Count[db.BigramIndexer.indexes.get(w0w1)][1] / trig_denominator;
			}


			// Bigram term
			double big_numerator = 0;
			int big_denominator = 0;
			int big_numerator_index = db.BigramIndexer.indexes.get(w1w2);
			if (big_numerator_index>=0) {big_numerator = db.BigramIndexer.Count[big_numerator_index][2];}
			int big_denominator_index = db.UnigramCounts[ngram[from+1]][2];
			if (big_denominator_index > 0) {big_denominator = db.UnigramCounts[ngram[from+1]][2];}
			double BIG_TERM;
			double ALPHA_BI;
			big_numerator = Math.max(big_numerator-DISC_BIG, 0);
			if ((big_numerator > 0) && (big_denominator > 0)){
				BIG_TERM = big_numerator/big_denominator;
			} else {
				BIG_TERM = 0;
			}
			if (db.UnigramCounts[ngram[from+1]][1] == 0) {
				ALPHA_BI = 1;
			} else {
				ALPHA_BI = DISC_BIG * db.UnigramCounts[ngram[from+1]][1] / big_denominator;
			}

			// Unigram term
			double uni_numerator = 0;
			int uni_numerator_index = db.UnigramCounts[ngram[from+2]][3];
			if (uni_numerator_index >= 0) {uni_numerator = db.UnigramCounts[ngram[from+2]][3];}
			int uni_denominator = db.BigramIndexer.CountHead-1;
			double UNI_TERM;
			if ((uni_numerator > 0) && (uni_denominator > 0)){
				UNI_TERM = uni_numerator/uni_denominator;
			} else {
				UNI_TERM = 0;
			}

			// Compute probability from the terms above. If it's 0, return a very small log probability
			double Prob = TRIG_TERM + ALPHA_TRI * (BIG_TERM + ALPHA_BI * (UNI_TERM));
			if (Prob == 0){
				return -1000;
			}

			double LogProb = Math.log(Prob);

			return LogProb;


		} else if (to - from == 2) {

			// Get index representations
			String w0_binary = db.intToBinary(ngram[from]);
			String w1_binary = db.intToBinary(ngram[from+1]);
			long w0w1 = db.twoBinStrsToInt(w0_binary, w1_binary);

			// Bigram term
			double big_numerator = 0;
			int big_denominator = 0;
			int big_numerator_index = db.BigramIndexer.indexes.get(w0w1);
			if (big_numerator_index>=0) {big_numerator = db.BigramIndexer.Count[big_numerator_index][0];}
			big_denominator = db.UnigramCounts[ngram[from]][0];
			double BIG_TERM;
			double ALPHA_BI;
			big_numerator = Math.max(big_numerator-DISC_BIG, 0);
			if (big_denominator < big_numerator) {
				System.out.println("Bad probability");
			}
			if ((big_numerator > 0) && (big_denominator > 0)){
				BIG_TERM = big_numerator/big_denominator;
			} else {
				BIG_TERM = 0;
			}
			if (db.UnigramCounts[ngram[from]][1] == 0) {
				ALPHA_BI = 1;
			} else {
				ALPHA_BI = DISC_BIG*db.UnigramCounts[ngram[from]][1]/big_denominator; // CHECK UNIGRAMDOT
			}


			// Unigram term
			double uni_numerator = 0;
			int uni_numerator_index = db.UnigramCounts[ngram[from+1]][3];
			if (uni_numerator_index > 0) {uni_numerator = db.UnigramCounts[ngram[from+1]][3];}
			int uni_denominator = db.BigramIndexer.CountHead-1; // MAKE SURE COUNT HEAD IS SET TO SIZE OF BIGRAM TABLE
			double UNI_TERM;
			if ((uni_numerator > 0) && (uni_denominator > 0)){
				UNI_TERM = uni_numerator/uni_denominator;
			} else {
				UNI_TERM = 0;
			}

			double Prob = (BIG_TERM + ALPHA_BI * (UNI_TERM));

			if (Prob == 0){
				return -1000;
			}

			double LogProb = Math.log(Prob);

			return LogProb;

		} else if (to - from == 1) {
			return -1000;
		}
		
		return (Double) null;
	}

	public long getCount(int[] ngram) {

		if (ngram.length > 3) return 0;


		if (ngram.length == 3){
			String w0_binary = db.intToBinary(ngram[0]);
			String w1_binary = db.intToBinary(ngram[1]);
			String w2_binary = db.intToBinary(ngram[2]);
			long w0w1w2 = db.threeBinStrsToInt("0000", w0_binary, w1_binary, w2_binary);
			int index_w0w1w2 = db.TrigramEmpirical.indexes.get(w0w1w2);
			if (index_w0w1w2==-1){
				return 0;
			} else {
				int COUNT = index_w0w1w2;
				return COUNT;
			}
		}
		if (ngram.length == 2) {
			String w0_binary = db.intToBinary(ngram[0]);
			String w1_binary = db.intToBinary(ngram[1]);
			long w0w1 = db.twoBinStrsToInt(w0_binary, w1_binary);
			int index_w0w1 = db.BigramIndexer.indexes.get(w0w1);
			if (index_w0w1==-1) {
				return 0;
			} else {
				int COUNT = db.BigramIndexer.Count[index_w0w1][0];

				return COUNT;
			}
		}
		if (ngram.length == 1){
			if (ngram[0] >= db.UnigramCounts.length) {
				return 0;
			} else {
				int COUNT = db.UnigramCounts[ngram[0]][0];
				return COUNT;
			}
		} else {
			System.out.println("Invalid ngram");
		}

		return (Long) null;
	}
}