package data.algorithms.util;

import java.util.Set;

import data.automaton.definitions.DFA;
import data.automaton.definitions.State;
import data.math.definitions.BinaryTuple;

/**
 * Enum class that represents some options of acceptance of a product dfa. 
 * For L(dfa1) and L(dfa2) the followin options are given:
	 * UNION: L(dfa1) union L(dfa2)
	 * UNIONCOMPLEMENT: !(L(dfa1) union L(dfa2))
	 * INTERSECTION: L(dfa1) intersect L(dfa2)
	 * Difference:
	 * 				ONEMINUSTWO:  L(dfa1) - L(dfa2)
	 * 				TWOMINUSONE:  L(dfa2) - L(dfa1)
	 * SYMMETRICDIFFERENCE: (L(dfa1) - L(dfa2)) union (L(dfa2) - L(dfa1))
	 * ORIGINAL1: L(dfa1)
	 * ORIGINAL2: L(dfa2)
 *
 */
public enum PDFALanguageType {
	INTERSECTION,
	ONEMINUSTWO,
	TWOMINUSONE,
	SYMMETRICDIFFERENCE,
	UNION,
	UNIONCOMPLEMENT,
	ORIGINAL1,
	ORIGINAL2;
	
	/**
	 * Returns a Set of tupels representing accepting states in a product automaton of two dfas.
	 * The type of this enum object defines the criteria of selecting accepting states.
	 * 
	 * This method requires the two input dfas to be complete except for the INTERSECTION case
	 * @param dfa1 original first dfa 
	 * @param dfa2 original second dfa
	 * @return tupels representing accepting states in a product automaton of the input dfas
	 */
	public Set<BinaryTuple<State,State>> getAcceptingStatesProduct(DFA dfa1, DFA dfa2){
		if(!this.equals(INTERSECTION) && (!dfa1.isComplete() || !dfa2.isComplete())){
			throw new IllegalArgumentException("The input dfas have to be complete!");
		}
		
		switch(this) {
		case INTERSECTION:
			return SetOperations.cartesianProduct(dfa1.getAcceptingStates(), dfa2.getAcceptingStates());
		case ONEMINUSTWO:
			return SetOperations.cartesianProduct(dfa1.getAcceptingStates(), dfa2.getNonAcceptingStates());
		case TWOMINUSONE:
			return SetOperations.cartesianProduct(dfa1.getNonAcceptingStates(), dfa2.getAcceptingStates());
		case UNIONCOMPLEMENT:
			return SetOperations.cartesianProduct(dfa1.getNonAcceptingStates(), dfa2.getNonAcceptingStates());
		case SYMMETRICDIFFERENCE:
			return SetOperations.union(
					PDFALanguageType.ONEMINUSTWO.getAcceptingStatesProduct(dfa1, dfa2),
					PDFALanguageType.TWOMINUSONE.getAcceptingStatesProduct(dfa1, dfa2));

		case UNION:
			return SetOperations.union(
					PDFALanguageType.SYMMETRICDIFFERENCE.getAcceptingStatesProduct(dfa1, dfa2),
					PDFALanguageType.INTERSECTION.getAcceptingStatesProduct(dfa1, dfa2));	
		case ORIGINAL1:
			return SetOperations.cartesianProduct(dfa1.getAcceptingStates(), dfa2.getStates());
		case ORIGINAL2:
			return SetOperations.cartesianProduct(dfa1.getStates(), dfa2.getAcceptingStates());
		default:
			return PDFALanguageType.INTERSECTION.getAcceptingStatesProduct(dfa1, dfa2);
		}
	}
}
