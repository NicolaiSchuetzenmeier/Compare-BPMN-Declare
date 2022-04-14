package data.regex.implementations;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import data.automaton.definitions.Alphabet;
import data.automaton.definitions.DFA;
import data.automaton.definitions.State;
import data.automaton.definitions.Symbol;
import data.automaton.implementations.AutomatonFactory;
import data.math.definitions.BinaryTuple;
import data.math.implementations.MathFactory;
import data.regex.definitions.Regex;
import data.regex.definitions.RegexDFA;

class FastRegexDFA implements RegexDFA {


	private Set<State> states;
	private Set<State> acceptingStates;
	private Alphabet alphabet;
	private Set<BinaryTuple<BinaryTuple<State, Regex>, State>> transitions;
	private Map<BinaryTuple<State,State>,Regex> transitionMap;
	private State startState;
	
	private Regex regex;

	FastRegexDFA(DFA dfa){
		if(dfa == null)
			throw new IllegalArgumentException("The input dfa must not be null!");
		states = new HashSet<>(dfa.getStates());
		alphabet = dfa.getAlphabet();
		startState = dfa.getStartState();
		acceptingStates = new HashSet<>(dfa.getAcceptingStates());
		transitionMap = new HashMap<>();
		
		for(BinaryTuple<BinaryTuple<State, Symbol>, State> tuple: dfa.getTransitionFunction().getTuples()) {
			BinaryTuple<State,State> key = MathFactory.createBinaryTuple(tuple.getFirst().getFirst(),tuple.getSecond());
			Regex transition = Regex.getSingleSymbolRegex(tuple.getFirst().getSecond());
			if(transitionMap.containsKey(key))
				transitionMap.put(key, Regex.alternate(transitionMap.get(key),transition));
			else
				transitionMap.put(key, transition);	
		}
		
		transform();
		calculateRegex();
		calculateTransitionSet();
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
	public Set<BinaryTuple<BinaryTuple<State, Regex>, State>> getTransitions() {
		return Collections.unmodifiableSet(transitions);
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
	public Regex getRegex() {
		return regex;
	}

	@Override
	public String toString() {
		return "{" + System.lineSeparator()
				+ "\"States\": " + states.toString() + "," + System.lineSeparator()
				+ "\"Alphabet\": " + alphabet.toString() + ", " + System.lineSeparator()
				+ "\"Transitions\": " + getTransitions().toString() + ", " + System.lineSeparator()
				+ "\"StartState\": " + startState.toString() + ", " + System.lineSeparator()
				+ "\"AcceptingStates\": " + acceptingStates.toString() + System.lineSeparator() + "}";
	}
	
	private void transform() {
		Set<State> treatableStates = new HashSet<>(states);
		treatableStates.remove(startState);
		
		for(State state: treatableStates)
			treatState(state);
	}
	
	private void treatState(State toRemove) {
		Set<Map.Entry<BinaryTuple<State,State>,Regex>> incomings = getIncomingTransitions(toRemove);
		Set<Map.Entry<BinaryTuple<State,State>,Regex>> outgoings = getOutgoingTransitions(toRemove);
		
		int i = 0;
		for(Map.Entry<BinaryTuple<State,State>,Regex> incoming: incomings) {			
			Regex incRegex = getIncomingRegex(toRemove,incoming);
			
			if(acceptingStates.contains(toRemove))
				addNewAcceptingState(AutomatonFactory.createState(getNewAccStateName(toRemove,i)),
						incoming.getKey().getFirst(),incRegex);
			
			for(Map.Entry<BinaryTuple<State,State>,Regex> outgoing: outgoings) {
				Regex newRegex = Regex.concatenate(incRegex, outgoing.getValue());
				addNewTransition(incoming.getKey().getFirst(),outgoing.getKey().getSecond(),newRegex);
			}
			i++;
		}
		
		for(Map.Entry<BinaryTuple<State,State>,Regex> incoming: incomings) 
			transitionMap.remove(incoming.getKey());

		for(Map.Entry<BinaryTuple<State,State>,Regex> outgoing: outgoings) 
			transitionMap.remove(outgoing.getKey());
		
		acceptingStates.remove(toRemove);
		states.remove(toRemove);
	}
	
	private Regex getIncomingRegex(State toRemove, Map.Entry<BinaryTuple<State,State>,Regex> incoming) {
		BinaryTuple<State,State> loop = MathFactory.createBinaryTuple(toRemove, toRemove);
		
		Regex incRegex = Regex.getEmptyWordRegex();
		if(transitionMap.containsKey(loop))
			incRegex = Regex.kleeneStar(transitionMap.get(loop));
		incRegex = Regex.concatenate(incoming.getValue(), incRegex);
		
		return incRegex;
	}
	
	private void addNewAcceptingState(State newState, State origin, Regex regex) {
		states.add(newState);
		acceptingStates.add(newState);
		
		BinaryTuple<State,State> newTrans = MathFactory.createBinaryTuple(origin, newState);
		transitionMap.put(newTrans, regex);	
	}
	
	private void addNewTransition(State origin, State target, Regex newRegex) {
		BinaryTuple<State,State> newTransition = MathFactory.createBinaryTuple(origin, target);
		if(transitionMap.containsKey(newTransition))
			transitionMap.put(newTransition, Regex.alternate(transitionMap.get(newTransition), newRegex));
		else
			transitionMap.put(newTransition, newRegex);
	}
	
	private Set<Map.Entry<BinaryTuple<State,State>,Regex>> getIncomingTransitions(State state){
		Set<Map.Entry<BinaryTuple<State,State>,Regex>> incomings = new HashSet<>();
		
		transitionMap.entrySet().forEach(n->{
			if(!n.getKey().getFirst().equals(state) && n.getKey().getSecond().equals(state))
				incomings.add(n);
				});
		return incomings;
	}
	
	private Set<Map.Entry<BinaryTuple<State,State>,Regex>> getOutgoingTransitions(State state){
		Set<Map.Entry<BinaryTuple<State,State>,Regex>> outgoings = new HashSet<>();
		
		transitionMap.entrySet().forEach(n->{
			if(n.getKey().getFirst().equals(state) && !n.getKey().getSecond().equals(state))
				outgoings.add(n);
				});
		return outgoings;
	}
	
	//TODO CHANGE
	private String getNewAccStateName(State state, int i) {
		String newName = state.getName() + "--" + i;
		while(states.contains(AutomatonFactory.createState(newName)))
			newName += "!";
		return newName;
	}

	private void calculateRegex() {
		Regex startRegex = Regex.getEmptyWordRegex();
		if(transitionMap.containsKey(MathFactory.createBinaryTuple(startState, startState)))
			startRegex = Regex.kleeneStar(transitionMap.get(MathFactory.createBinaryTuple(startState, startState)));

		Regex regexOut = (acceptingStates.contains(startState))? Regex.getEmptyWordRegex(): Regex.getEmptySetRegex();		
		for(State state: acceptingStates)
			if(!state.equals(startState))
				regexOut = Regex.alternate(regexOut, transitionMap.get(MathFactory.createBinaryTuple(startState, state)));
		
		regex = (regexOut.isEmptySetRegex())? startRegex: Regex.concatenate(startRegex, regexOut);
	}
	
	private void calculateTransitionSet(){
		transitions = new HashSet<>();
		for(Map.Entry<BinaryTuple<State,State>,Regex> entry: transitionMap.entrySet())
			transitions.add(MathFactory.createPseudoTriple(entry.getKey().getFirst(), entry.getValue(),entry.getKey().getSecond()));
	}
}
