package data.algorithms;

import java.util.List;
import java.util.Set;

import data.algorithms.util.PDFALanguageType;
import data.automaton.definitions.DFA;
import data.automaton.definitions.State;
import data.automaton.implementations.AutomatonFactory;
import data.exceptions.DFASEqualException;
import data.regex.definitions.Regex;

/**
 * This class provides several Algorithms for deterministic finite automata.
 *
 */
public class DFAAlgorithms {

	private DFAAlgorithms() {}

	private static final String EMSG1 = "The input dfa must not be null!"; 
	private static final String EMSG2 = "The input dfas must not be null!"; 

	/**
	 * Returns a regular expression re with L(re) = L(dfa).
	 * @param dfa the dfa for which to create an equivalent regular expression.
	 * @return an equivalent regular expression.
	 */
	public static Regex getEquivalentRegex(DFA dfa) {
		if(dfa == null)
			throw new IllegalArgumentException(EMSG1);
		return Regex.convertToRegex(dfa);
	}
	
	/**
	 * This Method returns whether or not two dfas are equivalent, meaning decide the same language
	 * @param dfa1 first dfa
	 * @param dfa2 second dfa
 	 * @return true if they are equivalent, false if they are not.
	 */
	public static boolean areEquivalent(DFA dfa1, DFA dfa2) {
		if(dfa1 == null || dfa2 == null)
			throw new IllegalArgumentException(EMSG2);
		return (minimizeDFA(
				productDFA(getCompleteMinimizedDFA(dfa1),getCompleteMinimizedDFA(dfa2), PDFALanguageType.SYMMETRICDIFFERENCE)
				).getAcceptingStates().isEmpty());
	}
	
	/**
	 * Returns a word that is accepted by the DFA dfa1 but is not accepted by dfa2.
	 * This word will incidentally be of minimal size.
	 * @param dfa1 The DFA that shall accept the word
	 * @param dfa2 The DFA that shall reject the word
	 * @throws DFASEqualException if and only if L(dfa1) is subset of L(dfa2) (or equal)
	 * @return a word that is accepted by dfa1 and rejected by dfs2
	 */
	public static String getWordAcceptedByOneRejectedByTwo(DFA dfa1, DFA dfa2) throws DFASEqualException{
		if(decidesSubset(dfa1,dfa2))
			throw new DFASEqualException("The first dfa decides a subset of the second dfa's language!");
		DFA product = minimizeDFA(productDFADifference(getCompleteMinimizedDFA(dfa1),getCompleteMinimizedDFA(dfa2)));
		for(int i = 0; true; i++) {
			Set<String> accepted = product.acceptsOfLength(i);
			if(!accepted.isEmpty())
				return accepted.iterator().next();
		}
	}

	/**
	 * Returns whether or not L(dfa1) is a subset of L(dfa2) or L(dfa2) is subset of L(dfa1)
	 * @param dfa1 a dfa.
	 * @param dfa2 a second dfa.
	 * @return true if and only if L(dfa1) is a subset of L(dfa2) or vice versa
	 */
	public static boolean decidesSubsetBidirectional(DFA dfa1, DFA dfa2) {
		if(dfa1 == null || dfa2 == null)
			throw new IllegalArgumentException(EMSG2);
		return minimizeDFA(productDFADifference(getCompleteMinimizedDFA(dfa1),getCompleteMinimizedDFA(dfa2))).getAcceptingStates().isEmpty()
				|| minimizeDFA(productDFADifference(getCompleteMinimizedDFA(dfa2),getCompleteMinimizedDFA(dfa1))).getAcceptingStates().isEmpty();
	}
	
	/**
	 * Returns whether or not L(dfa1) is a subset of L(dfa2).
	 * @param dfa1 the dfa whose language is the designated subset.
	 * @param dfa2 the dfa whose language is the designated containing set.
	 * @return true if and only if L(dfa1) is a subset of L(dfa2)
	 */
	public static boolean decidesSubset(DFA dfa1, DFA dfa2) {
		if(dfa1 == null || dfa2 == null)
			throw new IllegalArgumentException(EMSG2);
		return minimizeDFA(
				productDFADifference(getCompleteMinimizedDFA(dfa1),getCompleteMinimizedDFA(dfa2))
				).getAcceptingStates().isEmpty();
	}
	
	/**
	 * Creates a dfa that decides the complement of the input dfa language. (Relative to the Alphabet)
	 * Be A the alphabet of the original DFA (That decides the language L) then the complement DFA will decide the Language A*\L
	 * @param dfa the dfa which language to complement
	 * @return the complement dfa
	 */
	public static DFA getComplementDFA(DFA dfa) {
		if(dfa == null)
			throw new IllegalArgumentException(EMSG1);
		DFA cdfa = getCompleteDFA(dfa);
		return AutomatonFactory.createDFA(cdfa.getStates(),cdfa.getAlphabet(),cdfa.getTransitionFunction(),cdfa.getStartState(),cdfa.getNonAcceptingStates());
	}
	
	/**
	 * This method take a List of n DFAs and returns a minimized DFA
	 * for the language L(dfa1) intersect ... intersect L(dfan). Before each iteration and at the end
	 * the resulting dfas are minimized.  
	 * @param dfas the list of dfas to be multiplied in order
	 * @return the dfa as described above
	 */
	public static DFA minimizedProductDFA(List<DFA> dfas) {
		if(dfas == null)
			throw new IllegalArgumentException("The input dfa list must not be null!");
		if(dfas.contains(null))
			throw new IllegalArgumentException("The input dfa list must not contain null!");
		if(dfas.size() < 2)
			throw new IllegalArgumentException("The input dfa list must contain at least 2 dfas!");
		
		DFA dfa = dfas.get(0);
		for(int i = 1; i < dfas.size(); i++) 
			dfa = productDFA(getCompleteMinimizedDFA(dfa),getCompleteMinimizedDFA(dfas.get(i)),PDFALanguageType.INTERSECTION);
		return minimizeDFA(dfa);
	}
	
	/**
	 * This method take a List of n DFAs and returns a minimized DFA
	 * for the language L(dfa1) intersect ... intersect L(dfan). Before each iteration and at the end
	 * the resulting dfas are minimized.  
	 * @param dfas the list of dfas to be multiplied in order
	 * @param type the type of accepting state generation
	 * @return the dfa as described above
	 */
	public static DFA minimizedProductDFA(List<DFA> dfas,PDFALanguageType type) {
		if(dfas == null)
			throw new IllegalArgumentException("The input dfa list must not be null!");
		if(dfas.contains(null))
			throw new IllegalArgumentException("The input dfa list must not contain null!");
		if(dfas.size() < 2)
			throw new IllegalArgumentException("The input dfa list must contain at least 2 dfas!");
		if(type == null)
			throw new IllegalArgumentException("The type must not be null!");
		
		DFA dfa = dfas.get(0);
		for(int i = 1; i < dfas.size(); i++) 
			dfa = productDFA(getCompleteMinimizedDFA(dfa),getCompleteMinimizedDFA(dfas.get(i)),type);
		return minimizeDFA(dfa);
	}

	/**
	 * This method returns a product dfa = dfa1 x dfa2, that decides L(dfa1) - L(dfa2)
	 * @param dfa1 the first dfa of the operation
	 * @param dfa2 the second dfa of the operation
	 * @throws IllegalArgumentException if the alphabets of the input dfas are not equal.
	 * @return the product dfa that decides L(dfa1) - L(dfa2)
	 */
	public static DFA productDFADifference(DFA dfa1, DFA dfa2) {
		if(dfa1 == null || dfa2 == null)
			throw new IllegalArgumentException(EMSG2);

		return productDFA(dfa1,dfa2,PDFALanguageType.ONEMINUSTWO);
	}

	/**
	 * This method returns a product dfa = dfa1 x dfa2, that decides the union of their language
	 * @param dfa1 the first dfa of the operation
	 * @param dfa2 the second dfa of the operation
	 * @throws IllegalArgumentException if the alphabets of the input dfas are not equal.
	 * @return the product dfa
	 */
	public static DFA productDFAIntersect(DFA dfa1, DFA dfa2) {
		if(dfa1 == null || dfa2 == null)
			throw new IllegalArgumentException(EMSG2);

		return productDFA(dfa1,dfa2,PDFALanguageType.INTERSECTION);
	}

	/**
	 * This method returns a product dfa = dfa1 x dfa2. The Language that is decided by the product dfa is defined by the type parameter.
	 * @param dfa1 the first dfa of the operation
	 * @param dfa2 the second dfa of the operation
	 * @param type defines the language of the P-DFA
	 * @throws IllegalArgumentException if the alphabets of the input dfas are not equal.
	 * @return the product dfa
	 */
	public static DFA productDFA(DFA dfa1, DFA dfa2, PDFALanguageType type) {
		if(dfa1 == null || dfa2 == null)
			throw new IllegalArgumentException(EMSG2);
		if(type == null)
			throw new IllegalArgumentException("The pdfa language type must not be null!");
		return DFAProduct.productDFA(dfa1, dfa2, type);
	}	
	
	/**
	 * This method first completes the given dfa, then minimizes it and then (if the resulting dfa is not complete) makes it complete.
	 * The resulting dfa is most likely no longer minimized.
	 * @param dfa the dfa to reduce
	 * @return a complete dfa that is very close to being minimized.
	 */
	public static DFA getCompleteMinimizedDFA(DFA dfa) {
		if(dfa == null)
			throw new IllegalArgumentException(EMSG1);		
		return getCompleteDFA(minimizeDFA(getCompleteDFA(dfa)));
	}
	
	/**
	 * This method returns a minimized equivalent DFA using the hopcroft minimization algorithm
	 * and the algorithms for removing dead and unreachable states.
	 * @param dfa the dfa to minimize
	 * @return a new dfa, that is minimized and equivalent to the input dfa
	 */
	public static DFA minimizeDFA(DFA dfa) {
		if(dfa == null)
			throw new IllegalArgumentException(EMSG1);
		return DFAMinimization.minimizeDFA(dfa);
	}

	/**
	 * This method returns a reduced equivalent DFA that has dead states removed.
	 * @param dfa the dfa that is supposed to be reduced
	 * @return a new equivalent dfa without dead states
	 */
	public static DFA removeDeadStates(DFA dfa) {
		if(dfa == null)
			throw new IllegalArgumentException(EMSG1);
		return DFAMinimization.removeDeadStates(dfa);
	}

	/**
	 * This method returns a reduced equivalent DFA that has unreachable states removed.
	 * @param dfa the dfa that is supposed to be reduced
	 * @return a new equivalent dfa without unreachable states
	 */
	public static DFA removeUnreachableStates(DFA dfa) {
		if(dfa == null)
			throw new IllegalArgumentException(EMSG1);
		return DFAMinimization.removeUnreachableStates(dfa);
	}	

	/**
	 * This algorithm returns equivalence classes for the Myhill-Nerode equivalence relation of the input DFA. 
	 * @param dfa the dfa for which equivalence classes are constructed
	 * @return the set of equivalence classes
	 */
	public static Set<Set<State>> hopcroftMinimization(DFA dfa) {
		if(dfa == null)
			throw new IllegalArgumentException(EMSG1);
		return DFAMinimization.hopcroftMinimization(dfa);
	}
	

	/**
	 * This method returns a complete dfa equivalent to the given dfa. It also defines an existing state as trash state.
	 * It creates and adds a trash state for this purpose except if the given dfa is complete already.
	 * The given trash state can already be in the states set. If it is the following requirements must hold: 
	 * It must not have any outgoing edges except to itself.
	 * It must not be accepting.
	 * @param dfa the dfa to create a complete version for.
	 * @param trash the designated trash state
	 * @throws IllegalArgumentException if trash is null or it does not fulfill the requirements.
	 * @return a new complete dfa
	 */
	public static DFA getCompleteDFA(DFA dfa, State trash) {
		if(dfa == null)
			throw new IllegalArgumentException(EMSG1);
		if(trash == null)
			throw new IllegalArgumentException("The trash state must not be null!");
		return DFACompletion.getCompleteDFA(dfa,trash);
	}
	
	/**
	 * This method returns a complete dfa equivalent to the given dfa. 
	 * It creates and adds a trash state for this purpose except if the given dfa is complete already.
	 * @param dfa the dfa to create a complete version for.
	 * @return a new complete dfa
	 */
	public static DFA getCompleteDFA(DFA dfa) {
		if(dfa == null)
			throw new IllegalArgumentException(EMSG1);
		return DFACompletion.getCompleteDFA(dfa);
	}
}
