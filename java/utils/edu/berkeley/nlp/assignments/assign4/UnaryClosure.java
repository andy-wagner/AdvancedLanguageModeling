package edu.berkeley.nlp.assignments.assign4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.berkeley.nlp.util.CollectionUtils;
import edu.berkeley.nlp.util.Counter;
import edu.berkeley.nlp.util.Indexer;

/**
 * Calculates and provides accessors for the REFLEXIVE, TRANSITIVE closure of
 * the unary rules in the provided Grammar. Each rule in this closure stands for
 * zero or more unary rules in the original grammar. Use the getPath() method to
 * retrieve the full sequence of symbols (from parent to child) which support
 * that path.
 */
public class UnaryClosure
{

	public static class PositiveCycleException extends RuntimeException
	{

		/**
		 * @param message
		 */
		public PositiveCycleException(String message) {
			super(message);
			// TODO Auto-generated constructor stub
		}
	}

	Map<String, List<UnaryRule>> closedUnaryRulesByChild = new HashMap<String, List<UnaryRule>>();

	Map<String, List<UnaryRule>> closedUnaryRulesByParent = new HashMap<String, List<UnaryRule>>();

	Map<UnaryRule, List<String>> pathMap = new HashMap<UnaryRule, List<String>>();

	public List<UnaryRule> getClosedUnaryRulesByChild(String child) {
		return CollectionUtils.getValueList(closedUnaryRulesByChild, child);
	}

	public List<UnaryRule> getClosedUnaryRulesByParent(String parent) {
		return CollectionUtils.getValueList(closedUnaryRulesByParent, parent);
	}

	public List<String> getPath(UnaryRule unaryRule) {
		return pathMap.get(unaryRule);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String parent : closedUnaryRulesByParent.keySet()) {
			for (UnaryRule unaryRule : getClosedUnaryRulesByParent(parent)) {
				List<String> path = getPath(unaryRule);
				//          if (path.size() == 2) continue;
				sb.append(unaryRule);
				sb.append("  ");
				sb.append(path);
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	public UnaryClosure(Collection<UnaryRule> unaryRules, Weights weights, Indexer<FeatureIndexable> featureIndexer) {
		Map<UnaryRule, List<String>> closureMap = computeUnaryClosure(unaryRules, weights, featureIndexer);
		for (UnaryRule unaryRule : closureMap.keySet()) {
			addUnary(unaryRule, closureMap.get(unaryRule));
		}
	}

	public UnaryClosure(Grammar grammar, Weights weights, Indexer<FeatureIndexable> featureIndexer) {
		this(grammar.getUnaryRules(), weights, featureIndexer);
	}

	private void addUnary(UnaryRule unaryRule, List<String> path) {
		CollectionUtils.addToValueList(closedUnaryRulesByChild, unaryRule.getChild(), unaryRule);
		CollectionUtils.addToValueList(closedUnaryRulesByParent, unaryRule.getParent(), unaryRule);
		pathMap.put(unaryRule, path);
	}

	private static Map<UnaryRule, List<String>> computeUnaryClosure(Collection<UnaryRule> unaryRules, Weights weights, Indexer<FeatureIndexable> featureIndexer) {

		Map<UnaryRule, String> intermediateStates = new HashMap<UnaryRule, String>();
		Counter<UnaryRule> pathCosts = new Counter<UnaryRule>();
		Map<String, List<UnaryRule>> closedUnaryRulesByChild = new HashMap<String, List<UnaryRule>>();
		Map<String, List<UnaryRule>> closedUnaryRulesByParent = new HashMap<String, List<UnaryRule>>();

		Set<String> states = new HashSet<String>();

		for (UnaryRule unaryRule : unaryRules) {
			relax(pathCosts, intermediateStates, closedUnaryRulesByChild, closedUnaryRulesByParent, new UnaryRule(unaryRule.getParent(), unaryRule.getChild()),
				null, unaryRule.getScore(weights));
			states.add(unaryRule.getParent());
			states.add(unaryRule.getChild());
		}

		for (String intermediateState : states) {
			List<UnaryRule> incomingRules = closedUnaryRulesByChild.get(intermediateState);
			List<UnaryRule> outgoingRules = closedUnaryRulesByParent.get(intermediateState);
			if (incomingRules == null || outgoingRules == null) continue;
			for (UnaryRule incomingRule : incomingRules) {
				for (UnaryRule outgoingRule : outgoingRules) {
					UnaryRule rule = new UnaryRule(incomingRule.getParent(), outgoingRule.getChild());
					double newScore = pathCosts.getCount(incomingRule) + pathCosts.getCount(outgoingRule);
					relax(pathCosts, intermediateStates, closedUnaryRulesByChild, closedUnaryRulesByParent, rule, intermediateState, newScore);
				}
			}
		}

		for (String state : states) {
			UnaryRule selfLoopRule = new UnaryRule(state, state);
			relax(pathCosts, intermediateStates, closedUnaryRulesByChild, closedUnaryRulesByParent, selfLoopRule, null, 0.0);
		}

		Map<UnaryRule, List<String>> closureMap = new HashMap<UnaryRule, List<String>>();

		for (UnaryRule unaryRule : pathCosts.keySet()) {
			unaryRule.setScore(null, pathCosts.getCount(unaryRule));
			List<String> path = extractPath(unaryRule, intermediateStates, featureIndexer, new HashSet<String>());
			closureMap.put(unaryRule, path);
		}

		return closureMap;

	}

	private static List<String> extractPath(UnaryRule unaryRule, Map<UnaryRule, String> intermediateStates, Indexer<FeatureIndexable> featureIndexer,
		Set<String> exploredStates) {
		List<String> path = new ArrayList<String>();
		path.add(unaryRule.getParent());
		String intermediateState = intermediateStates.get(unaryRule);
		if (intermediateState != null) {
			if (exploredStates.contains(intermediateState)) { throw new PositiveCycleException("Looks like there is a positive cycle of unaries for rule "
				+ unaryRule); }
			exploredStates.add(intermediateState);
			List<String> parentPath = extractPath(new UnaryRule(unaryRule.getParent(), intermediateState, featureIndexer), intermediateStates, featureIndexer,
				exploredStates);
			for (int i = 1; i < parentPath.size() - 1; i++) {
				String state = parentPath.get(i);
				path.add(state);
			}
			path.add(intermediateState);
			List<String> childPath = extractPath(new UnaryRule(intermediateState, unaryRule.getChild(), featureIndexer), intermediateStates, featureIndexer,
				exploredStates);
			for (int i = 1; i < childPath.size() - 1; i++) {
				String state = childPath.get(i);
				path.add(state);
			}
		}
		if (path.size() == 1 && unaryRule.getParent().equals(unaryRule.getChild())) return path;
		path.add(unaryRule.getChild());
		return path;
	}

	private static void relax(Counter<UnaryRule> pathCosts, Map<UnaryRule, String> intermediateStates, Map<String, List<UnaryRule>> closedUnaryRulesByChild,
		Map<String, List<UnaryRule>> closedUnaryRulesByParent, UnaryRule unaryRule, String intermediateState, double newScore) {
		if (intermediateState != null && (intermediateState.equals(unaryRule.getParent()) || intermediateState.equals(unaryRule.getChild()))) return;
		boolean isNewRule = !pathCosts.containsKey(unaryRule);
		double oldScore = (isNewRule ? Double.NEGATIVE_INFINITY : pathCosts.getCount(unaryRule));
		if (oldScore > newScore) return;
		if (isNewRule) {
			CollectionUtils.addToValueList(closedUnaryRulesByChild, unaryRule.getChild(), unaryRule);
			CollectionUtils.addToValueList(closedUnaryRulesByParent, unaryRule.getParent(), unaryRule);
		}
		pathCosts.setCount(unaryRule, newScore);
		intermediateStates.put(unaryRule, intermediateState);
	}

}