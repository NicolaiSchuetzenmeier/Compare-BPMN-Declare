package data.regex.definitions;

import java.util.Set;

import data.automaton.definitions.Alphabet;
import data.automaton.definitions.State;
import data.math.definitions.BinaryTuple;

/**
 * An interface for a dfa-regex hybrid that is used in the process of creating an equivalent regex to a dfa.
 */
public interface RegexDFA {

	/**
	 * @return the set of states of this DEA
	 */
	public Set<State> getStates();
	
	/**
	 * @return the alphabet of this DEA
	 */
	public Alphabet getAlphabet();
	
	/**
	 * @return the regex transitions of the DEA
	 */
	public Set<BinaryTuple<BinaryTuple<State,Regex>,State>> getTransitions();
	
	/**
	 * @return the start state of this DEA
	 */
	public State getStartState();
	
	/**
	 * @return the set of accepting states of this DEA
	 */
	public Set<State> getAcceptingStates();
	
	/**
	 * @return the regex of this dfa
	 */
	public Regex getRegex();
}
