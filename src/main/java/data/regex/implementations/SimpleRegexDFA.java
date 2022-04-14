package data.regex.implementations;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import data.algorithms.util.SetOperations;
import data.automaton.definitions.Alphabet;
import data.automaton.definitions.DFA;
import data.automaton.definitions.State;
import data.automaton.definitions.Symbol;
import data.automaton.implementations.AutomatonFactory;
import data.math.definitions.BinaryTuple;
import data.math.implementations.MathFactory;
import data.regex.definitions.Regex;
import data.regex.definitions.RegexDFA;

/**
 * A simple implementation of a regex DFA
 *
 */
class SimpleRegexDFA implements RegexDFA {


	private Set<State> states;
	private Set<State> acceptingStates;
	private Alphabet alphabet;
	private Set<BinaryTuple<BinaryTuple<State, Regex>, State>> transitions;
	private State startState;
	
	private Regex regex;

	SimpleRegexDFA(DFA dfa){
		if(dfa == null)
			throw new IllegalArgumentException("The input dfa must not be null!");
		states = new HashSet<>(dfa.getStates());
		alphabet = dfa.getAlphabet();
		startState = dfa.getStartState();
		acceptingStates = new HashSet<>(dfa.getAcceptingStates());
		transitions = new HashSet<>();
		
		for(BinaryTuple<BinaryTuple<State, Symbol>, State> tuple: dfa.getTransitionFunction().getTuples()) {
			transitions.add(MathFactory.createBinaryTuple(
					MathFactory.createBinaryTuple(tuple.getFirst().getFirst(), 
							Regex.getSingleSymbolRegex(tuple.getFirst().getSecond())), tuple.getSecond()));		
		}

		for(State state1: states) 		
			for(State state2: states)
				combineTransitions(state1, state2);
		
		transform();
		regex = calculateRegex();
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
				+ "\"Transitions\": " + transitions.toString() + ", " + System.lineSeparator()
				+ "\"StartState\": " + startState.toString() + ", " + System.lineSeparator()
				+ "\"AcceptingStates\": " + acceptingStates.toString() + System.lineSeparator() + "}";
	}
	
	private void combineTransitions(State state1, State state2) {
		Set<BinaryTuple<BinaryTuple<State,Regex>,State>> outgoings = getAllOutgoingTransitions(state1,state2);
		if(!outgoings.isEmpty()) {
			Regex newRegex = Regex.getEmptySetRegex();
			for(BinaryTuple<BinaryTuple<State,Regex>,State> outgoing: outgoings) {
				newRegex = Regex.alternate(newRegex, outgoing.getFirst().getSecond());					
			}
			transitions.removeAll(outgoings);
			transitions.add(MathFactory.createBinaryTuple(MathFactory.createBinaryTuple(state1, newRegex), state2));
		}
	}
	
	private void transform() {
		Set<State> removableStates = new HashSet<>(SetOperations.complement(states, acceptingStates));
		removableStates.remove(startState);
		while(!removableStates.isEmpty())
			removeOneState(removableStates);
		
		Set<State> treatableAcceptingStates = new HashSet<>(acceptingStates);
		treatableAcceptingStates.remove(startState);
		while(!treatableAcceptingStates.isEmpty())
				treatOneAccState(treatableAcceptingStates);
	}
	
	private void removeOneState(Set<State> removableStates) {
		if(removableStates.isEmpty()) return;
		State toRemove = removableStates.iterator().next();
		Set<BinaryTuple<BinaryTuple<State,Regex>,State>> incomings = getAllIncomingTransitions(toRemove);
		Set<BinaryTuple<BinaryTuple<State,Regex>,State>> outgoings = getAllOutgoingTransitions(toRemove);
		Set<BinaryTuple<BinaryTuple<State,Regex>,State>> loops = getAllLoopTransitions(toRemove);
		Set<BinaryTuple<BinaryTuple<State,Regex>,State>> newTrans = new HashSet<>(); 
		
		Regex loopRegex = Regex.getEmptySetRegex();
		for(BinaryTuple<BinaryTuple<State,Regex>,State> loop: loops)
			loopRegex = Regex.alternate(loopRegex, loop.getFirst().getSecond());
		
		for(BinaryTuple<BinaryTuple<State,Regex>,State> incoming: incomings) {
			Regex incRegex;
			incRegex = Regex.kleeneStar(loopRegex);
			incRegex = Regex.concatenate(incoming.getFirst().getSecond(), incRegex);
			
			for(BinaryTuple<BinaryTuple<State,Regex>,State> outgoing: outgoings) {
				Regex tmpRegex = Regex.concatenate(incRegex, outgoing.getFirst().getSecond());
				newTrans.add(MathFactory.createBinaryTuple(MathFactory.createBinaryTuple(
						incoming.getFirst().getFirst(), tmpRegex), outgoing.getSecond()));
			}
		}
		transitions.removeAll(incomings);
		transitions.removeAll(outgoings);
		transitions.removeAll(loops);
		transitions.addAll(newTrans);
		removableStates.remove(toRemove);
		states.remove(toRemove);
	}
	
	private void treatOneAccState(Set<State> treatableAcceptingStates) {
		if(treatableAcceptingStates.isEmpty()) return;
		State toRemove = treatableAcceptingStates.iterator().next();
		Set<BinaryTuple<BinaryTuple<State,Regex>,State>> incomings = getAllIncomingTransitions(toRemove);
		Set<BinaryTuple<BinaryTuple<State,Regex>,State>> outgoings = getAllOutgoingTransitions(toRemove);
		Set<BinaryTuple<BinaryTuple<State,Regex>,State>> loops = getAllLoopTransitions(toRemove);
		Set<BinaryTuple<BinaryTuple<State,Regex>,State>> newTrans = new HashSet<>(); 
		
		Regex loopRegex = Regex.getEmptySetRegex();
		for(BinaryTuple<BinaryTuple<State,Regex>,State> loop: loops)
			loopRegex = Regex.alternate(loopRegex, loop.getFirst().getSecond());

		int i = 0;
		for(BinaryTuple<BinaryTuple<State,Regex>,State> incoming: incomings) {
			Regex incRegex;
			incRegex = Regex.kleeneStar(loopRegex);
			incRegex = Regex.concatenate(incoming.getFirst().getSecond(), incRegex);
			
			State newState = AutomatonFactory.createState(getNewAccStateName(toRemove,i));
			states.add(newState);
			acceptingStates.add(newState);
			newTrans.add(MathFactory.createBinaryTuple(MathFactory.createBinaryTuple(
					incoming.getFirst().getFirst(), incRegex), newState));
			
			for(BinaryTuple<BinaryTuple<State,Regex>,State> outgoing: outgoings) {
				Regex tmpRegex = Regex.concatenate(incRegex, outgoing.getFirst().getSecond());
				newTrans.add(MathFactory.createBinaryTuple(MathFactory.createBinaryTuple(
						incoming.getFirst().getFirst(), tmpRegex), outgoing.getSecond()));
			}
			i++;
		}
		
		transitions.removeAll(incomings);
		transitions.removeAll(outgoings);
		transitions.removeAll(loops);
		transitions.addAll(newTrans);
		treatableAcceptingStates.remove(toRemove);
		acceptingStates.remove(toRemove);
		states.remove(toRemove);
	}
	
	//TODO CHANGE
	private String getNewAccStateName(State state, int i) {
		String newName = state.getName() + "--" + i;
		while(states.contains(AutomatonFactory.createState(newName)))
			newName += "!";
		return newName;
	}
	
	private Set<BinaryTuple<BinaryTuple<State,Regex>,State>> getAllIncomingTransitions(State state){
		Set<BinaryTuple<BinaryTuple<State,Regex>,State>> incoming = new HashSet<>();
		for(BinaryTuple<BinaryTuple<State,Regex>,State> transition: transitions)
			if(!transition.getFirst().getFirst().equals(state) && transition.getSecond().equals(state))
				incoming.add(transition);
		return incoming;
	}
	
	private Set<BinaryTuple<BinaryTuple<State,Regex>,State>> getAllOutgoingTransitions(State state){
		Set<BinaryTuple<BinaryTuple<State,Regex>,State>> outgoing = new HashSet<>();
		for(BinaryTuple<BinaryTuple<State,Regex>,State> transition: transitions)
			if(transition.getFirst().getFirst().equals(state) && !transition.getSecond().equals(state))
				outgoing.add(transition);
		return outgoing;
	}
	
	private Set<BinaryTuple<BinaryTuple<State,Regex>,State>> getAllOutgoingTransitions(State state, State target){
		Set<BinaryTuple<BinaryTuple<State,Regex>,State>> outgoing = new HashSet<>();
		for(BinaryTuple<BinaryTuple<State,Regex>,State> transition: transitions)
			if(transition.getFirst().getFirst().equals(state) && transition.getSecond().equals(target))
				outgoing.add(transition);
		return outgoing;
	}

	private Set<BinaryTuple<BinaryTuple<State,Regex>,State>> getAllLoopTransitions(State state){
		Set<BinaryTuple<BinaryTuple<State,Regex>,State>> loops = new HashSet<>();
		for(BinaryTuple<BinaryTuple<State,Regex>,State> transition: transitions)
			if(transition.getFirst().getFirst().equals(state) && transition.getSecond().equals(state))
				loops.add(transition);
		return loops;
	}
	
	private Regex calculateRegex() {
		Regex regexStart = Regex.getEmptySetRegex();
		for(BinaryTuple<BinaryTuple<State,Regex>,State> loop: getAllLoopTransitions(startState))
			regexStart = Regex.alternate(regexStart, loop.getFirst().getSecond());
		regexStart = Regex.kleeneStar(regexStart);		
		
		Regex regexOut = (acceptingStates.contains(startState))? Regex.getEmptyWordRegex(): Regex.getEmptySetRegex();		
		for(BinaryTuple<BinaryTuple<State,Regex>,State> out: getAllOutgoingTransitions(startState))
			regexOut = Regex.alternate(regexOut, out.getFirst().getSecond());
		
		if(regexOut.isEmptySetRegex())
			return regexStart;
		return Regex.concatenate(regexStart, regexOut);
	}
}
