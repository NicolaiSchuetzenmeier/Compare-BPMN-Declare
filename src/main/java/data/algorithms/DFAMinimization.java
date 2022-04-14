package data.algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import data.algorithms.util.DFAUtil;
import data.algorithms.util.SetOperations;
import data.automaton.definitions.DFA;
import data.automaton.definitions.State;
import data.automaton.definitions.Symbol;
import data.automaton.implementations.AutomatonFactory;
import data.math.definitions.BinaryTuple;
import data.math.implementations.MathFactory;

class DFAMinimization {
	
	private DFAMinimization() {}
	
	/**
	 * This method returns a minimized equivalent DFA using the hopcroft minimization algorithm.
	 * @param inputDfa the dfa to minimize
	 * @return a new dfa, that is minimized and equivalent to the input dfa
	 */
	static DFA minimizeDFA(DFA inputDfa) {
		DFA dfa = DFAAlgorithms.getCompleteDFA(removeUnreachableStates(inputDfa));
		List<Set<State>> classes = new ArrayList<>(hopcroftMinimization(dfa));

		List<State> newStates = new ArrayList<>(dfa.getStates().size());
		Set<State> newAccStates = new HashSet<>();
		State newStartState = dfa.getStartState();
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> newTupels = new HashSet<>();
		
		for(Set<State> clas: classes) {
			State newState = clas.iterator().next();
			newStates.add(newState);
			
			if(dfa.getAcceptingStates().contains(newState))
				newAccStates.add(newState);
			
			if(clas.contains(dfa.getStartState()))
				newStartState = newState;
		}

		for(BinaryTuple<BinaryTuple<State,Symbol>,State> tuple: dfa.getTransitionFunction().getTuples()) {
			State newOrigin = null;
			State newDestination = null;
			
			for(int i = 0; i < classes.size(); i++) {
				if(classes.get(i).contains(tuple.getFirst().getFirst()))
					newOrigin = newStates.get(i);
				if(classes.get(i).contains(tuple.getSecond())) 
					newDestination = newStates.get(i);
			}
			
			newTupels.add(MathFactory.createBinaryTuple(
					MathFactory.createBinaryTuple(newOrigin, tuple.getFirst().getSecond()),newDestination));
		}
			
		
		return AutomatonFactory.createDFA(new HashSet<>(newStates), dfa.getAlphabet(), MathFactory.createTransitionFunction(newTupels), newStartState, newAccStates);
	}
	
	/**
	 * This method returns a reduced equivalent DFA that has dead states removed.
	 * If the start state is a dead state, then this method will return a minimized, equivalent dfa, that decides the empty set.
	 * @param dfa the dfa that is supposed to be reduced
	 * @return a new equivalent dfa without dead states
	 */
	static DFA removeDeadStates(DFA dfa) {
		Set<State> newStates = new HashSet<>(dfa.getStates());	
		Set<State> newAcceptingStates = new HashSet<>(dfa.getAcceptingStates());
		Set<State> toRemove = new HashSet<>(dfa.getStates());
		
		/*
		 * Be Di the set of states that cannot reach an accepting state i.
		 * All dead states are included in Q intersect D0 intersect ... intersect Dn
		 */
		for(State acceptingState: dfa.getAcceptingStates()) {
			toRemove = SetOperations.intersection(toRemove,DFAUtil.getAllNonReachingStates(dfa,acceptingState));
		}
		
		newAcceptingStates.removeAll(toRemove);
		newStates.removeAll(toRemove);
		
		if(!newStates.contains(dfa.getStartState()))
			return AutomatonFactory.createEmptySetDFA(dfa.getAlphabet(),dfa.getStartState());
		
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> newTupels = new HashSet<>();
		for(BinaryTuple<BinaryTuple<State,Symbol>,State> tupel: dfa.getTransitionFunction().getTuples())
			if(newStates.contains(tupel.getFirst().getFirst()) && newStates.contains(tupel.getSecond()))
				newTupels.add(tupel);		
		
		return AutomatonFactory.createDFA(newStates, 
				AutomatonFactory.createAlphabet(dfa.getAlphabet()), 
				MathFactory.createTransitionFunction(newTupels), 
				dfa.getStartState(), newAcceptingStates);
	}

	/**
	 * This method returns a reduced equivalent DFA that has unreachable states removed.
	 * @param dfa the dfa that is supposed to be reduced
	 * @return a new equivalent dfa without unreachable states
	 */
	static DFA removeUnreachableStates(DFA dfa) {
		Set<State> newStates = new HashSet<>(dfa.getStates());	
		Set<State> newAcceptingStates = new HashSet<>(dfa.getAcceptingStates());	
		Set<State> toRemove = DFAUtil.getAllUnReachableStates(dfa,dfa.getStartState());
		
		newStates.removeAll(toRemove);
		newAcceptingStates.removeAll(toRemove);

		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> newTupels = new HashSet<>(dfa.getTransitionFunction().getTuples());
		for(BinaryTuple<BinaryTuple<State,Symbol>,State> tupel: dfa.getTransitionFunction().getTuples())
			if(!newStates.contains(tupel.getFirst().getFirst()) || !newStates.contains(tupel.getSecond()))
				newTupels.remove(tupel);		
		
		return AutomatonFactory.createDFA(newStates, 
				AutomatonFactory.createAlphabet(dfa.getAlphabet()), 
				MathFactory.createTransitionFunction(newTupels), 
				dfa.getStartState(), newAcceptingStates);
	}	

	/**
	 * This algorithm returns equivalence classes for the Myhill-Nerode equivalence relation of the input DFA. 
	 * @param dfa the dfa for which equivalence classes are constructed
	 * @return the set of equivalence classes
	 */
	public static Set<Set<State>> hopcroftMinimization(DFA dfa) {
		Set<State> nonAcceptingStates = SetOperations.complement(dfa.getStates(),dfa.getAcceptingStates());
		if(dfa.getAcceptingStates().isEmpty() || nonAcceptingStates.isEmpty()) {
			return getSingleClassEquivalenceClasses(dfa);
		}
		
		Set<Set<State>> p = new HashSet<>();
		Set<Set<State>> w = new HashSet<>();
		
		p.add(dfa.getAcceptingStates());
		p.add(nonAcceptingStates);
		w.add(dfa.getAcceptingStates());
		w.add(nonAcceptingStates);
		
		while(!w.isEmpty()) {
			Set<State> s = w.iterator().next();
			w.remove(s);
			
			for(Symbol a: dfa.getAlphabet()) {
				Set<State> la = DFAUtil.getAllDirectlyReachingStates(dfa,s,a);
				for(Set<State> r: new HashSet<>(p)) {
					if(!SetOperations.intersection(r,la).isEmpty() && !la.containsAll(r)) {
						Set<State> r1 = SetOperations.intersection(la, r);
						Set<State> r2 = SetOperations.complement(r,r1);
						p.remove(r);
						p.add(r1);
						p.add(r2);
						if(w.contains(r)) {
							w.remove(r);
							w.add(r1);
							w.add(r2);
						} else {
							if(r1.size() <= r2.size())
								w.add(r1);
							else
								w.add(r2);
						}
					}
				}
			}
		}
		return p;			
	}
	
	/**
	 * Returns a set of equivalence classes with a single equivalence class containing all states.
	 * @param dfa dfa for which equivalence classes are being built
	 * @return a set of equivalence classes with a single equivalence class containing all states.
	 */
	private static Set<Set<State>> getSingleClassEquivalenceClasses(DFA dfa){
		Set<Set<State>> classes = new HashSet<>();
		Set<State> clas = new HashSet<>();
		clas.addAll(dfa.getStates());
		classes.add(clas);		
		return classes;
	}
	
}
