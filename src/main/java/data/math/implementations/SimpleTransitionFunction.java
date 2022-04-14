package data.math.implementations;

import java.util.HashSet;
import java.util.Set;

import data.automaton.definitions.State;
import data.automaton.definitions.Symbol;
import data.math.definitions.TransitionFunction;
import data.math.exceptions.OutOfDomainException;
import data.math.definitions.BinaryTuple;

class SimpleTransitionFunction extends SimpleFunction<BinaryTuple<State,Symbol>, State> implements TransitionFunction {
	
	SimpleTransitionFunction(Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> tupels) {
		super(tupels);
	}

	@Override
	public State apply(State state, Symbol symbol)  throws OutOfDomainException{
		return apply(MathFactory.createBinaryTuple(state, symbol));
	}
	
	@Override
	public String toString() {
		String s = "[";
		int i = 0;
		for(BinaryTuple<BinaryTuple<State,Symbol>, State> tupel: getTuples()) {
			s += "(" + tupel.getFirst().getFirst().getName() 
					+ ", " + tupel.getFirst().getSecond().getChar()
					+ ") -> " + tupel.getSecond().getName();
			if(i < getTuples().size() - 1)
					s += ", ";
			i++;
		}
		return s + "]";
	}

	@Override
	public Set<State> getDomainStates() {
		Set<State> domainStates = new HashSet<>();
		for(BinaryTuple<State,Symbol> tuple: getDomain())
			domainStates.add(tuple.getFirst());		
		return domainStates;
	}

	@Override
	public Set<State> getPreimageStates(State target) {
		if(target == null)
			throw new IllegalArgumentException("The target state must not be null!");
		
		Set<State> preimageStates = new HashSet<>();
		for(BinaryTuple<BinaryTuple<State,Symbol>,State> tuple: getTuples())
			if(tuple.getSecond().equals(target))
				preimageStates.add(tuple.getFirst().getFirst());
		
		return preimageStates;
	}

	@Override
	public Set<State> getPreimageStates(Symbol symbol, State target) {
		if(target == null)
			throw new IllegalArgumentException("The target state must not be null!");
		
		Set<State> preimageStates = new HashSet<>();
		for(BinaryTuple<BinaryTuple<State,Symbol>,State> tuple: getTuples())
			if(tuple.getSecond().equals(target) && tuple.getFirst().getSecond().equals(symbol))
				preimageStates.add(tuple.getFirst().getFirst());
		
		return preimageStates;
	}
}
