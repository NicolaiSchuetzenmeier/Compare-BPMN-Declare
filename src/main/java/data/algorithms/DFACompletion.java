package data.algorithms;

import java.util.HashSet;
import java.util.Set;

import data.automaton.definitions.DFA;
import data.automaton.definitions.State;
import data.automaton.definitions.Symbol;
import data.automaton.implementations.AutomatonFactory;
import data.math.definitions.BinaryTuple;
import data.math.implementations.MathFactory;

public class DFACompletion {
	private static final String EMSG1 = "The input dfa must not be null!"; 

	private DFACompletion() {}
	
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
	static DFA getCompleteDFA(DFA dfa, State trash) {
		if(dfa == null)
			throw new IllegalArgumentException(EMSG1);

		if(trash == null)
			throw new IllegalArgumentException("The trash state must not be null!");
		
		if(dfa.getStates().contains(trash)) {
			if(dfa.getAcceptingStates().contains(trash))
				throw new IllegalArgumentException("The trash state is in the state set and accepting!");
			dfa.getTransitionFunction().getTuples().forEach(n->{
				if(n.getFirst().getFirst().equals(trash) && !n.getSecond().equals(trash))
					throw new IllegalArgumentException("The trash state is in the state set and has outgoing transitions!");
				});
		}
		
		if(dfa.isComplete())
			return AutomatonFactory.createDFA(dfa);
		
		return getCompleteDFAUtil(dfa,trash);
	}
	
	/**
	 * Utility method for getCompleteDFA.
	 * This method does the actual work of the getCompleteDFA method.
	 */
	private static DFA getCompleteDFAUtil(DFA dfa, State trash) {
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> transitions = new HashSet<>(dfa.getTransitionFunction().getTuples());
		Set<State> states = new HashSet<>(dfa.getStates());
		states.add(trash);
		
		for(State state: dfa.getStates())
			for(Symbol symbol: dfa.getAlphabet())
				if(!dfa.getTransitionFunction().getDomain().contains(MathFactory.createBinaryTuple(state, symbol)))
					transitions.add(MathFactory.createTransition(state, symbol, trash));
		for(Symbol symbol: dfa.getAlphabet())
			transitions.add(MathFactory.createTransition(trash, symbol, trash));
		return AutomatonFactory.createDFA(states, dfa.getAlphabet(), MathFactory.createTransitionFunction(transitions), dfa.getStartState(), dfa.getAcceptingStates());
	}
	
	/**
	 * This method returns a complete dfa equivalent to the given dfa. 
	 * It creates and adds a trash state for this purpose except if the given dfa is complete already.
	 * @param dfa the dfa to create a complete version for.
	 * @return a new complete dfa
	 */
	static DFA getCompleteDFA(DFA dfa) {
		if(dfa == null)
			throw new IllegalArgumentException(EMSG1);

		return getCompleteDFA(dfa,AutomatonFactory.createState(getTrashStateName(dfa)));
	}
	
	/**
	 * Returns a valid trash state name, that is not already used by any state in the dfa's state set
	 * @param dfa dfa to find valid trash state name for
	 * @return a valid trash state name
	 */
	private static String getTrashStateName(DFA dfa) {
		String name = "trash";
		
		while(dfa.getStates().contains(AutomatonFactory.createState(name)))
			name += "!";
		
		return name;
	}
}
