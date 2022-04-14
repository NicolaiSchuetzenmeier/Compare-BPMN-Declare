package data.automaton.definitions;

import java.util.Set;

import data.math.definitions.*;

/**
 * An interface for a deterministic finite automaton DEA = (Q,X,d,s,A) with:
 * Q a set of states
 * X an alphabet
 * d a transition function d: (Q x X) -> Q
 * s in Q the start state
 * A subset of Q the set of accepting states 
 *
 */
public interface DFA {

	/**
	 * @return the set of states of this DEA
	 */
	public Set<State> getStates();
	
	/**
	 * @return the alphabet of this DEA
	 */
	public Alphabet getAlphabet();
	
	/**
	 * @return the transition function of the DEA
	 */
	public TransitionFunction getTransitionFunction();
	
	/**
	 * @return the start state of this DEA
	 */
	public State getStartState();
	
	/**
	 * @return the set of accepting states of this DEA
	 */
	public Set<State> getAcceptingStates();
	
	/**
	 * @return all states of this DEA that are not accepting
	 */
	public Set<State> getNonAcceptingStates();

	/**
	 * Get the inverse transition relation (meaning for each ((q,s),p) in f -> ((p,s),q) in f-1)
	 * f being the transition function as a relation
	 * f-1 being the inverse relation 
	 * @return the inverse transition relation.
	 */
	public Relation<BinaryTuple<State,Symbol>,State> getInverseTransitionRelation();
	
	/**
	 * Returns whether or not the given word is in the language decided by this dfa.
	 * @param word word to check
	 * @throws IllegalArgumentException if word is null or not in A* (A being the ALphabet)
	 * @return true if and only if this dfa accepts the word
	 */
	public boolean accepts(String word);	

	/**
	 * Returns all words w with |w| = length and w in L(dfa).
	 * @param length the length of the words to get
	 * @return a set of words that are accepted by this dfa and are of size length
	 */
	public Set<String> acceptsOfLength(int length);

	/**
	 * Returns all words w with |w| less or equal than length and w in L(dfa).
	 * @param length the length limit of the words to get
	 * @return a set of words that are accepted by this dfa and are limited in size by length
	 */
	public Set<String> acceptsUntilLength(int length);
	
	/**
	 * Checks if this dfa is complete.
	 * @return true if and only if this dfa is complete
	 */
	public boolean isComplete();
}
