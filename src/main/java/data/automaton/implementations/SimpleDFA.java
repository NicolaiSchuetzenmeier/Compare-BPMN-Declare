package data.automaton.implementations;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import data.algorithms.util.SetOperations;
import data.automaton.definitions.Alphabet;
import data.automaton.definitions.DFA;
import data.automaton.definitions.State;
import data.automaton.definitions.Symbol;
import data.math.definitions.Relation;
import data.math.definitions.TransitionFunction;
import data.math.exceptions.OutOfDomainException;
import data.math.definitions.BinaryTuple;
import data.math.implementations.MathFactory;
/**
 * This class is a simple implementation of the DFA interface.
 * It represents a DFA and is immutable after creation.
 */
class SimpleDFA implements DFA {

	private Set<State> states;
	private Alphabet alphabet;
	private TransitionFunction transitionFunction;
	private State startState;
	private Set<State> acceptingStates;
	
	/**
	 * Creates a new immutable DFA.
	 * @throws IllegalArgumentException if the parameter either do not define a DFA or include null values.
	 * @param states the state set
	 * @param alphabet the alphabet
	 * @param transitionFunction the transition function. has to be defined with states in the state set and symbols in the alphabet.
	 * @param startState the start state. Has to be element of states
	 * @param acceptingStates the set of accepting states. Has to be a subset of states
	 */
	SimpleDFA(Set<State> states, Alphabet alphabet
			, TransitionFunction transitionFunction, State startState, Set<State> acceptingStates) {
		if(states == null)
			throw new IllegalArgumentException("The set of states for a FA must not be null!");
		if(states.contains(null))
			throw new IllegalArgumentException("The set of states for a FA must not include null!");
		if(alphabet == null)
			throw new IllegalArgumentException("The alphabet for a FA must not be null!");
		if(startState == null)
			throw new IllegalArgumentException("The start state for a FA must not be null!");
		if(acceptingStates == null)
			throw new IllegalArgumentException("The set of accepting states for a FA must not be null!");
		if(acceptingStates.contains(null))
			throw new IllegalArgumentException("The set of accepting states for a FA must not include null!");
		if(transitionFunction == null)
			throw new IllegalArgumentException("The transition function for a FA must not be null!");
		
		
		if(!states.containsAll(acceptingStates))
			throw new IllegalArgumentException("The set of accepting states must be a subset of the state set!");

		if(!states.contains(startState))
			throw new IllegalArgumentException("The start state must be element of the state set!");
		
		if(!states.containsAll(transitionFunction.getCodomain()))
			throw new IllegalArgumentException("The transition function uses states not defined in the definition of this automaton!");
		
		for(BinaryTuple<State,Symbol> tupel: transitionFunction.getDomain()) {
			if(!states.contains(tupel.getFirst()))
				throw new IllegalArgumentException("The transition function uses states not defined in the definition of this automaton!");
			if(!alphabet.contains(tupel.getSecond()))
				throw new IllegalArgumentException("The transition function uses symbols not defined in the definition of this automaton!");
		}
		
		this.states = new HashSet<>(states);
		this.alphabet = alphabet;
		this.transitionFunction = transitionFunction;
		this.startState = startState;
		this.acceptingStates = new HashSet<>(acceptingStates);
		
	}
	
	@Override
	public Set<State> getStates() {
		return Collections.unmodifiableSet(states);
	}

	@Override
	public Alphabet getAlphabet() {
		return alphabet;
	}

	@Override
	public TransitionFunction getTransitionFunction() {
		return transitionFunction;
	}

	@Override
	public State getStartState() {
		return startState;
	}

	@Override
	public Set<State> getAcceptingStates() {
		return Collections.unmodifiableSet(acceptingStates);
	}

	@Override
	public Set<State> getNonAcceptingStates() {
		return Collections.unmodifiableSet(SetOperations.complement(getStates(), getAcceptingStates()));
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(!(o instanceof DFA)) return false;
		DFA f = (DFA) o;
		return states.equals(f.getStates())
				&& alphabet.equals(f.getAlphabet())
				&& transitionFunction.equals(f.getTransitionFunction())
				&& startState.equals(f.getStartState())
				&& acceptingStates.equals(f.getAcceptingStates());
	}
	
	@Override 
	public int hashCode() {
		return states.hashCode() 
				+ alphabet.hashCode() 
				+ transitionFunction.hashCode()
				+ startState.hashCode()
				+ acceptingStates.hashCode();
	}
	
	@Override
	public String toString() {
		return "{" + System.lineSeparator()
				+ "\"States\": " + states.toString() + "," + System.lineSeparator()
				+ "\"Alphabet\": " + alphabet.toString() + ", " + System.lineSeparator()
				+ "\"TransitionFunction\": " + transitionFunction.toString() + ", " + System.lineSeparator()
				+ "\"StartState\": " + startState.toString() + ", " + System.lineSeparator()
				+ "\"AcceptingStates\": " + acceptingStates.toString() + System.lineSeparator() + "}";
	}

	@Override
	public Relation<BinaryTuple<State, Symbol>, State> getInverseTransitionRelation() {
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> inverse = new HashSet<>();
		
		for(BinaryTuple<BinaryTuple<State,Symbol>,State> tupel: getTransitionFunction().getTuples())
			inverse.add(MathFactory.createTransition(tupel.getSecond(), tupel.getFirst().getSecond(), 
					tupel.getFirst().getFirst()));
		
		return MathFactory.createRelation(inverse);
	}

	@Override
	public boolean accepts(String word) {
		if(word == null)
			throw new IllegalArgumentException("The word must not be null!");
		if(!alphabet.isInKleeneStar(word))
			throw new IllegalArgumentException("The word is not in the kleene star of this alphabet!");
		State endState;
		try {
			endState = visit(getStartState(),asSymbolList(word));
		} catch (OutOfDomainException e) {
			return false;
		}		
		return getAcceptingStates().contains(endState);
	}
	
	private List<Symbol> asSymbolList(String word){
		List<Symbol> list = new LinkedList<>();
		for(char c: word.toCharArray())
			list.add(AutomatonFactory.createSymbol(c));
		return list;
	}

	@Override
	public boolean isComplete() {		
		for(State state: getStates())
			for(Symbol symbol: getAlphabet())
				if(!getTransitionFunction().getDomain().contains(MathFactory.createBinaryTuple(state, symbol)))
					return false;
		return true;
	}

	@Override
	public Set<String> acceptsOfLength(int length) {
		Set<String> accepted = new HashSet<>();
		visit(accepted,getStartState(),"",0,length,true);
		return accepted;
	}

	@Override
	public Set<String> acceptsUntilLength(int length) {
		Set<String> accepted = new HashSet<>();
		visit(accepted,getStartState(),"",0,length,false);
		return accepted;
	}

	private void visit(Set<String> accepted, State currentState, String currentWord, int currentDepth, int maxDepth, boolean strictLength) {
		if(currentDepth > maxDepth)
			return;
		if((!strictLength || currentDepth == maxDepth) && acceptingStates.contains(currentState))
			accepted.add(currentWord);

		for(Symbol symbol: getAlphabet())
			try {
				visit(accepted, 
						getTransitionFunction().apply(currentState,symbol),
						currentWord + symbol.getChar(),
						currentDepth + 1, maxDepth, strictLength);
			} catch (OutOfDomainException e) {}
	}
	
	private State visit(State current, List<Symbol> wordRest) throws OutOfDomainException {
		if(wordRest.isEmpty())
			return current;
		return visit(getTransitionFunction().apply(current,wordRest.remove(0)), wordRest);
	}
	
}
