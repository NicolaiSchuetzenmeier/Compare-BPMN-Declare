package data.automaton.implementations;

import java.util.HashSet;
import java.util.Set;

import data.automaton.definitions.*;
import data.math.definitions.TransitionFunction;
import data.math.implementations.MathFactory;

/**
 * A factory for automaton specific objects.
 *
 */
public class AutomatonFactory {
	
	private AutomatonFactory() {}

	/**
	 * Creates a DFA. 
	 * @param states state set
	 * @param alphabet alphabet
	 * @param transitionFunction the transition function. has to be defined with states in the state set and symbols in the alphabet.
	 * @param startState the start state. Has to be element of states
	 * @param acceptingStates the set of accepting states. Has to be a subset of states
	 * @return a new DFA
	 */
	public static DFA createDFA(Set<State> states, Alphabet alphabet
			, TransitionFunction transitionFunction, State startState, Set<State> acceptingStates) {
		return new SimpleDFA(states,alphabet,transitionFunction,startState,acceptingStates);
	}

	/**
	 * Creates a DFA as a copy of a given dfa. 
	 * @param dfa the dfa to copy
	 * @return a new DFA
	 */
	public static DFA createDFA(DFA dfa) {
		return new SimpleDFA(dfa.getStates(),dfa.getAlphabet(),dfa.getTransitionFunction(),dfa.getStartState(),dfa.getAcceptingStates());
	}

	/**
	 * Creates a DFA that decides A* for alphabet A. 
	 * The start_state will get an arbitrary name.
	 * @param alphabet alphabet A
	 * @return a new DFA that decides A*
	 */
	public static DFA createKleeneStartDFA(Alphabet alphabet) {
		return createKleeneStartDFA(alphabet,AutomatonFactory.createState("q0"));
	}
	
	/**
	 * Creates a DFA that decides A* for alphabet A. 
	 * @param alphabet alphabet
	 * @param startState the start state. The only state of the dfa.
	 * @return a new DFA that decides the empty set
	 */
	public static DFA createKleeneStartDFA(Alphabet alphabet,State startState) {
		Set<State> states = new HashSet<>();
		Set<State> accStates = new HashSet<>();
		TransitionFunction function = MathFactory.createTransitionFunction(new HashSet<>());
		states.add(startState);
		accStates.add(startState);
		
		return new SimpleDFA(states,alphabet,function,startState,accStates);
	}
	
	/**
	 * Creates a DFA that decides the empty set over alphabet A. 
	 * The start_state will get an arbitrary name.
	 * @param alphabet alphabet
	 * @return a new DFA that decides the empty set
	 */
	public static DFA createEmptySetDFA(Alphabet alphabet) {
		return createEmptySetDFA(alphabet,AutomatonFactory.createState("q0"));
	}
	
	/**
	 * Creates a DFA that decides the empty set over alphabet A. 
	 * @param alphabet alphabet
	 * @param startState the start state. The only state of the dfa.
	 * @return a new DFA that decides the empty set
	 */
	public static DFA createEmptySetDFA(Alphabet alphabet,State startState) {
		Set<State> states = new HashSet<>();
		Set<State> accStates = new HashSet<>();
		TransitionFunction function = MathFactory.createTransitionFunction(new HashSet<>());
		states.add(startState);
		
		return new SimpleDFA(states,alphabet,function,startState,accStates);
	}
	
	/**
	 * Creates a new Alphabet
	 * @param symbols set of symbols that define this alphabet
	 * @return a new alphabet
	 */
	public static Alphabet createAlphabet(Set<Symbol> symbols) {
		return new SimpleAlphabet(symbols);
	}

	/**
	 * Creates a copy of an alphabet 
	 * @param alphabet the alphabet to copy
	 * @throws IllegalArgumentException if the input alphabet is null
	 * @return a new alphabet identical to the input alphabet
	 */
	public static Alphabet createAlphabet(Alphabet alphabet) {
		if(alphabet == null)
			throw new IllegalArgumentException("The input alphabet must not be null!");
		return new SimpleAlphabet(alphabet.getSymbols());
	}
	
	/**
	 * Creates a new State
	 * @param name name of the state
	 * @return a new state with the given name
	 */
	public static State createState(String name) {
		return new SimpleState(name);
	}
	
	/**
	 * Creates a new symbol
	 * @param character character of this symbol
	 * @return a new symbol, that references the given character
	 */
	public static Symbol createSymbol(char character) {
		return new SimpleSymbol(character);
	}
}
