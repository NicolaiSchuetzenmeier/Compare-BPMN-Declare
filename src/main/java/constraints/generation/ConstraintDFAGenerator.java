package constraints.generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import constraints.definitions.Constraint;
import constraints.definitions.ModelDefinition;
import data.algorithms.DFAAlgorithms;
import data.algorithms.util.SetOperations;
import data.automaton.definitions.*;
import data.automaton.implementations.*;
import data.math.definitions.BinaryTuple;
import data.math.definitions.TransitionFunction;
import data.math.implementations.MathFactory;

/**
 * This class generates DFAs for certain declare constraints. *
 */
public class ConstraintDFAGenerator {
	
	private static final String EMSG1 = "The input parameters must not be null!";
	private static final String EMSG2 = "The label(s) must included in the alphabet!";
	private static final String EMSG3 = "N must be > 0!";
	
	private ConstraintDFAGenerator() {}
	
	public static DFA generateDFA(ModelDefinition definition) {
		if(definition == null)
			throw new IllegalArgumentException("This method does not allow null parameters!");
		
		List<DFA> dfas = new ArrayList<>(definition.getConstraints().size());
		for(Constraint constraint: definition.getConstraints())
			dfas.add(generateDFA(definition.getActivities(),constraint));
		
		if(dfas.size() == 1)
			return dfas.get(0);
		else
			return DFAAlgorithms.getCompleteDFA(DFAAlgorithms.minimizedProductDFA(dfas));
	}
	
	public static DFA generateDFA(Set<Symbol> alphabetSet, Constraint constraint) {
		if(alphabetSet == null || constraint == null)
			throw new IllegalArgumentException("This method does not allow null values as parameters!");
		
		if(constraint.getConstraintType().isCount()) {
			switch(constraint.getConstraintType()) {
			case ABSENCE:
				return absenceDFA(alphabetSet,constraint.getSymbolParameters().get(0),constraint.getIntegerParameter());
			case EXACTLY:
				return exactlyDFA(alphabetSet,constraint.getSymbolParameters().get(0),constraint.getIntegerParameter());
			case EXISTENCE:
				return existenceDFA(alphabetSet,constraint.getSymbolParameters().get(0),constraint.getIntegerParameter());
			default:
				throw new UnsupportedOperationException();	
			}
		}
		
		if(constraint.getConstraintType().isUnary()) {
			switch(constraint.getConstraintType()) {
			case INIT:
				return initDFA(alphabetSet,constraint.getSymbolParameters().get(0));
			case LAST:
				return lastDFA(alphabetSet,constraint.getSymbolParameters().get(0));
			default:
				throw new UnsupportedOperationException();	
			}
		}
		
		switch(constraint.getConstraintType()) {
		case ALTERNATE_PRECEDENCE:
			return alternatePrecedenceDFA(alphabetSet,constraint.getSymbolParameters().get(0),constraint.getSymbolParameters().get(1));
		case ALTERNATE_RESPONSE:
			return alternateResponseDFA(alphabetSet,constraint.getSymbolParameters().get(0),constraint.getSymbolParameters().get(1));
		case ALTERNATE_SUCCESSION:
			return alternateSuccessionDFA(alphabetSet,constraint.getSymbolParameters().get(0),constraint.getSymbolParameters().get(1));
		case CHAIN_PRECEDENCE:
			return chainPrecedenceDFA(alphabetSet,constraint.getSymbolParameters().get(0),constraint.getSymbolParameters().get(1));
		case CHAIN_RESPONSE:
			return chainResponseDFA(alphabetSet,constraint.getSymbolParameters().get(0),constraint.getSymbolParameters().get(1));
		case CHAIN_SUCCESSION:
			return chainSuccessionDFA(alphabetSet,constraint.getSymbolParameters().get(0),constraint.getSymbolParameters().get(1));
		case CHOICE1OF2:
			return choice1Of2DFA(alphabetSet,constraint.getSymbolParameters().get(0),constraint.getSymbolParameters().get(1));
		case CO_EXISTENCE:
			return coExistenceDFA(alphabetSet,constraint.getSymbolParameters().get(0),constraint.getSymbolParameters().get(1));
		case EXCLUSIVE_CHOICE1OF2:
			return exclusiveChoice1Of2DFA(alphabetSet,constraint.getSymbolParameters().get(0),constraint.getSymbolParameters().get(1));
		case NOT_CHAIN_SUCCESSION:
			return notChainSuccessionDFA(alphabetSet,constraint.getSymbolParameters().get(0),constraint.getSymbolParameters().get(1));
		case NOT_CO_EXISTENCE:
			return notCoExistenceDFA(alphabetSet,constraint.getSymbolParameters().get(0),constraint.getSymbolParameters().get(1));
		case NOT_RESPONDED_EXISTENCE:
			return notRespondedExistenceDFA(alphabetSet,constraint.getSymbolParameters().get(0),constraint.getSymbolParameters().get(1));
		case NOT_RESPONSE:
			return notResponseDFA(alphabetSet,constraint.getSymbolParameters().get(0),constraint.getSymbolParameters().get(1));
		case NOT_SUCCESSION:
			return notSuccessionDFA(alphabetSet,constraint.getSymbolParameters().get(0),constraint.getSymbolParameters().get(1));
		case PRECEDENCE:
			return precedenceDFA(alphabetSet,constraint.getSymbolParameters().get(0),constraint.getSymbolParameters().get(1));
		case RESPONDED_EXISTENCE:
			return respondedExistenceDFA(alphabetSet,constraint.getSymbolParameters().get(0),constraint.getSymbolParameters().get(1));
		case RESPONSE:
			return responseDFA(alphabetSet,constraint.getSymbolParameters().get(0),constraint.getSymbolParameters().get(1));
		case SUCCESSION:
			return successionDFA(alphabetSet,constraint.getSymbolParameters().get(0),constraint.getSymbolParameters().get(1));
		default:
			throw new UnsupportedOperationException();		
		}
	}
	
	/**
	 * Creates a DFA for the declare constraint init, with activity A.
	 * @param alphabetSet the alphabet of the constructed dfa as a set of symbols
	 * @param a the activity A as a symbol
	 * @return a dfa for init
	 */
	public static DFA initDFA(Set<Symbol> alphabetSet, Symbol a) {
		if(alphabetSet == null || a == null)
			throw new IllegalArgumentException(EMSG1);
		if(!alphabetSet.contains(a))
			throw new IllegalArgumentException(EMSG2);
		
		Alphabet alphabet = AutomatonFactory.createAlphabet(alphabetSet);
		
		State state0 = AutomatonFactory.createState("0");
		State state1 = AutomatonFactory.createState("1");
		State state2 = AutomatonFactory.createState("2");
		
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> transitions = new HashSet<>();
		transitions.add(MathFactory.createTransition(state0, a, state1));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a),state0,state2));
		transitions.addAll(createOtherwiseTransitions(alphabet,Collections.emptyList(),state1,state1));
		transitions.addAll(createOtherwiseTransitions(alphabet,Collections.emptyList(),state2,state2));
		
		TransitionFunction function = MathFactory.createTransitionFunction(transitions);
		
		Set<State> states = new HashSet<>(Arrays.asList(state0,state1,state2));
		Set<State> accStates = new HashSet<>(Arrays.asList(state0,state1));
		State startState = state0;
		
		return AutomatonFactory.createDFA(states,alphabet,function,startState,accStates);
	}

	/**
	 * Creates a DFA for the declare constraint last, with activity A.
	 * @param alphabetSet the alphabet of the constructed dfa as a set of symbols
	 * @param a the activity A as a symbol
	 * @return a dfa for last
	 */
	public static DFA lastDFA(Set<Symbol> alphabetSet, Symbol a) {
		if(alphabetSet == null || a == null)
			throw new IllegalArgumentException(EMSG1);
		if(!alphabetSet.contains(a))
			throw new IllegalArgumentException(EMSG2);
		
		Alphabet alphabet = AutomatonFactory.createAlphabet(alphabetSet);
		
		State state0 = AutomatonFactory.createState("0");
		State state1 = AutomatonFactory.createState("1");
		
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> transitions = new HashSet<>();
		transitions.add(MathFactory.createTransition(state0, a, state1));
		transitions.add(MathFactory.createTransition(state1, a, state1));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a),state0,state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a),state1,state0));
		
		TransitionFunction function = MathFactory.createTransitionFunction(transitions);
		
		Set<State> states = new HashSet<>(Arrays.asList(state0,state1));
		Set<State> accStates = new HashSet<>(Arrays.asList(state1));
		State startState = state0;
		
		return AutomatonFactory.createDFA(states,alphabet,function,startState,accStates);
	}

	/**
	 * Creates a DFA for the declare constraint existence, with activity A and a occurrence minimum n
	 * @param alphabetSet the alphabet of the constructed dfa as a set of symbols
	 * @param a the activity A as a symbol
	 * @param n the number defining how often a has to occur
	 * @return a dfa for existence
	 */
	public static DFA existenceDFA(Set<Symbol> alphabetSet, Symbol a, int n) {
		if(alphabetSet == null || a == null)
			throw new IllegalArgumentException(EMSG1);
		if(!alphabetSet.contains(a))
			throw new IllegalArgumentException(EMSG2);
		if(n < 1)
			throw new IllegalArgumentException(EMSG3);
		
		Alphabet alphabet = AutomatonFactory.createAlphabet(alphabetSet);
		
		State startState = AutomatonFactory.createState("0");
		State last = AutomatonFactory.createState("" + n);	
		
		Set<State> accStates = new HashSet<>(Arrays.asList(last));			
		List<State> states = new ArrayList<>(Arrays.asList(startState));
		
		for(int i = 1; i < n; i++)
			states.add(AutomatonFactory.createState("" + i));
		states.add(last);
		
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> transitions = new HashSet<>();
		
		for(int i = 0; i < n; i++) {
			transitions.add(MathFactory.createTransition(states.get(i), a, states.get(i+1)));
			transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a),states.get(i),states.get(i)));			
		}
		transitions.addAll(createOtherwiseTransitions(alphabet,Collections.emptyList(),last,last));
		
		TransitionFunction function = MathFactory.createTransitionFunction(transitions);
		
		
		return AutomatonFactory.createDFA(new HashSet<>(states),alphabet,function,startState,accStates);
	}
	
	public static DFA absenceDFA(Set<Symbol> alphabetSet, Symbol a, int n) {
		if(alphabetSet == null || a == null)
			throw new IllegalArgumentException(EMSG1);
		if(!alphabetSet.contains(a))
			throw new IllegalArgumentException(EMSG2);
		if(n < 1)
			throw new IllegalArgumentException(EMSG3);
		
		Alphabet alphabet = AutomatonFactory.createAlphabet(alphabetSet);
		
		State startState = AutomatonFactory.createState("0");
		State last = AutomatonFactory.createState("" + n);	
		
		Set<State> accStates = new HashSet<>(Arrays.asList(startState));			
		List<State> states = new ArrayList<>(Arrays.asList(startState));
		
		for(int i = 1; i < n; i++) {
			State tmp = AutomatonFactory.createState("" + i);
			states.add(tmp);
			accStates.add(tmp);
		}
		states.add(last);
		
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> transitions = new HashSet<>();
		
		for(int i = 0; i < n; i++) {
			transitions.add(MathFactory.createTransition(states.get(i), a, states.get(i+1)));
			transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a),states.get(i),states.get(i)));			
		}
		transitions.addAll(createOtherwiseTransitions(alphabet,Collections.emptyList(),last,last));
		
		TransitionFunction function = MathFactory.createTransitionFunction(transitions);		
		
		return AutomatonFactory.createDFA(new HashSet<>(states),alphabet,function,startState,accStates);
	}
	
	public static DFA exactlyDFA(Set<Symbol> alphabetSet, Symbol a, int n) {
		if(alphabetSet == null || a == null)
			throw new IllegalArgumentException(EMSG1);
		if(!alphabetSet.contains(a))
			throw new IllegalArgumentException(EMSG2);
		if(n < 1)
			throw new IllegalArgumentException(EMSG3);
		
		Alphabet alphabet = AutomatonFactory.createAlphabet(alphabetSet);
		
		State startState = AutomatonFactory.createState("0");
		State acc = AutomatonFactory.createState("" + n);	
		State trash = AutomatonFactory.createState("" + (n+1));	
		
		Set<State> accStates = new HashSet<>(Arrays.asList(acc));			
		List<State> states = new ArrayList<>(Arrays.asList(startState));
		
		for(int i = 1; i < n; i++)
			states.add(AutomatonFactory.createState("" + i));
		states.add(acc);
		states.add(trash);
		
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> transitions = new HashSet<>();
		
		for(int i = 0; i <= n; i++) {
			transitions.add(MathFactory.createTransition(states.get(i), a, states.get(i+1)));
			transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a),states.get(i),states.get(i)));			
		}
		transitions.addAll(createOtherwiseTransitions(alphabet,Collections.emptyList(),trash,trash));
		
		TransitionFunction function = MathFactory.createTransitionFunction(transitions);		
		
		return AutomatonFactory.createDFA(new HashSet<>(states),alphabet,function,startState,accStates);
	}
	
	public static DFA precedenceDFA(Set<Symbol> alphabetSet, Symbol a, Symbol b) {
		if(alphabetSet == null || a == null || b == null)
			throw new IllegalArgumentException(EMSG1);
		if(!alphabetSet.contains(a) || !alphabetSet.contains(b))
			throw new IllegalArgumentException(EMSG2);
		
		Alphabet alphabet = AutomatonFactory.createAlphabet(alphabetSet);
		
		State state0 = AutomatonFactory.createState("0");
		State state1 = AutomatonFactory.createState("1");
		State state2 = AutomatonFactory.createState("2");
		
		Set<State> states = new HashSet<>(Arrays.asList(state0,state1,state2));
		Set<State> accStates = new HashSet<>(Arrays.asList(state0,state1));
		State startState = state0;
		
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> transitions = new HashSet<>();
		transitions.add(MathFactory.createTransition(state0, a, state1));
		transitions.add(MathFactory.createTransition(state0, b, state2));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a,b),state0,state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Collections.emptyList(),state1,state1));
		transitions.addAll(createOtherwiseTransitions(alphabet,Collections.emptyList(),state2,state2));
		
		TransitionFunction function = MathFactory.createTransitionFunction(transitions);
		
		
		return AutomatonFactory.createDFA(states,alphabet,function,startState,accStates);
	}
	
	public static DFA responseDFA(Set<Symbol> alphabetSet, Symbol a, Symbol b) {
		if(alphabetSet == null || a == null || b == null)
			throw new IllegalArgumentException(EMSG1);
		if(!alphabetSet.contains(a) || !alphabetSet.contains(b))
			throw new IllegalArgumentException(EMSG2);
		
		Alphabet alphabet = AutomatonFactory.createAlphabet(alphabetSet);
		
		State state0 = AutomatonFactory.createState("0");
		State state1 = AutomatonFactory.createState("1");
		
		Set<State> states = new HashSet<>(Arrays.asList(state0,state1));
		Set<State> accStates = new HashSet<>(Arrays.asList(state0));
		State startState = state0;
		
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> transitions = new HashSet<>();
		transitions.add(MathFactory.createTransition(state0, a, state1));
		transitions.add(MathFactory.createTransition(state1, b, state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a),state0,state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(b),state1,state1));
		
		TransitionFunction function = MathFactory.createTransitionFunction(transitions);
		
		
		return AutomatonFactory.createDFA(states,alphabet,function,startState,accStates);
	}
	
	public static DFA successionDFA(Set<Symbol> alphabetSet, Symbol a, Symbol b) {
		if(alphabetSet == null || a == null || b == null)
			throw new IllegalArgumentException(EMSG1);
		if(!alphabetSet.contains(a) || !alphabetSet.contains(b))
			throw new IllegalArgumentException(EMSG2);
		
		Alphabet alphabet = AutomatonFactory.createAlphabet(alphabetSet);
		
		State state0 = AutomatonFactory.createState("0");
		State state1 = AutomatonFactory.createState("1");
		State state2 = AutomatonFactory.createState("2");
		State state3 = AutomatonFactory.createState("3");
		
		Set<State> states = new HashSet<>(Arrays.asList(state0,state1,state2,state3));
		Set<State> accStates = new HashSet<>(Arrays.asList(state0,state1));
		State startState = state0;
		
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> transitions = new HashSet<>();
		transitions.add(MathFactory.createTransition(state0, a, state3));
		transitions.add(MathFactory.createTransition(state0, b, state2));
		transitions.add(MathFactory.createTransition(state3, b, state1));
		transitions.add(MathFactory.createTransition(state1, a, state3));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a,b),state0,state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a),state1,state1));
		transitions.addAll(createOtherwiseTransitions(alphabet,Collections.emptyList(),state2,state2));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(b),state3,state3));
		
		TransitionFunction function = MathFactory.createTransitionFunction(transitions);
		
		
		return AutomatonFactory.createDFA(states,alphabet,function,startState,accStates);
	}
	
	public static DFA alternatePrecedenceDFA(Set<Symbol> alphabetSet, Symbol a, Symbol b) {
		if(alphabetSet == null || a == null || b == null)
			throw new IllegalArgumentException(EMSG1);
		if(!alphabetSet.contains(a) || !alphabetSet.contains(b))
			throw new IllegalArgumentException(EMSG2);
		
		Alphabet alphabet = AutomatonFactory.createAlphabet(alphabetSet);
		
		State state0 = AutomatonFactory.createState("0");
		State state1 = AutomatonFactory.createState("1");
		State state2 = AutomatonFactory.createState("2");
		
		Set<State> states = new HashSet<>(Arrays.asList(state0,state1,state2));
		Set<State> accStates = new HashSet<>(Arrays.asList(state0,state1));
		State startState = state0;
		
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> transitions = new HashSet<>();
		transitions.add(MathFactory.createTransition(state0, a, state1));
		transitions.add(MathFactory.createTransition(state0, b, state2));
		transitions.add(MathFactory.createTransition(state1, b, state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a,b),state0,state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(b),state1,state1));
		transitions.addAll(createOtherwiseTransitions(alphabet,Collections.emptyList(),state2,state2));
		
		TransitionFunction function = MathFactory.createTransitionFunction(transitions);		
		
		return AutomatonFactory.createDFA(states,alphabet,function,startState,accStates);
	}
	
	public static DFA alternateResponseDFA(Set<Symbol> alphabetSet, Symbol a, Symbol b) {
		if(alphabetSet == null || a == null || b == null)
			throw new IllegalArgumentException(EMSG1);
		if(!alphabetSet.contains(a) || !alphabetSet.contains(b))
			throw new IllegalArgumentException(EMSG2);
		
		Alphabet alphabet = AutomatonFactory.createAlphabet(alphabetSet);
		
		State state0 = AutomatonFactory.createState("0");
		State state1 = AutomatonFactory.createState("1");
		State state2 = AutomatonFactory.createState("2");
		
		Set<State> states = new HashSet<>(Arrays.asList(state0,state1,state2));
		Set<State> accStates = new HashSet<>(Arrays.asList(state0));
		State startState = state0;
		
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> transitions = new HashSet<>();
		transitions.add(MathFactory.createTransition(state0, a, state2));
		transitions.add(MathFactory.createTransition(state2, a, state1));
		transitions.add(MathFactory.createTransition(state2, b, state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a),state0,state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a,b),state2,state2));
		transitions.addAll(createOtherwiseTransitions(alphabet,Collections.emptyList(),state1,state1));
		
		TransitionFunction function = MathFactory.createTransitionFunction(transitions);		
		
		return AutomatonFactory.createDFA(states,alphabet,function,startState,accStates);
	}
	
	public static DFA alternateSuccessionDFA(Set<Symbol> alphabetSet, Symbol a, Symbol b) {
		if(alphabetSet == null || a == null || b == null)
			throw new IllegalArgumentException(EMSG1);
		if(!alphabetSet.contains(a) || !alphabetSet.contains(b))
			throw new IllegalArgumentException(EMSG2);
		
		Alphabet alphabet = AutomatonFactory.createAlphabet(alphabetSet);
		
		State state0 = AutomatonFactory.createState("0");
		State state1 = AutomatonFactory.createState("1");
		State state2 = AutomatonFactory.createState("2");
		
		Set<State> states = new HashSet<>(Arrays.asList(state0,state1,state2));
		Set<State> accStates = new HashSet<>(Arrays.asList(state0));
		State startState = state0;
		
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> transitions = new HashSet<>();
		transitions.add(MathFactory.createTransition(state0, a, state2));
		transitions.add(MathFactory.createTransition(state0, b, state1));
		transitions.add(MathFactory.createTransition(state2, a, state1));
		transitions.add(MathFactory.createTransition(state2, b, state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a,b),state0,state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a,b),state2,state2));
		transitions.addAll(createOtherwiseTransitions(alphabet,Collections.emptyList(),state1,state1));
		
		TransitionFunction function = MathFactory.createTransitionFunction(transitions);		
		
		return AutomatonFactory.createDFA(states,alphabet,function,startState,accStates);
	}
	
	public static DFA chainPrecedenceDFA(Set<Symbol> alphabetSet, Symbol a, Symbol b) {
		if(alphabetSet == null || a == null || b == null)
			throw new IllegalArgumentException(EMSG1);
		if(!alphabetSet.contains(a) || !alphabetSet.contains(b))
			throw new IllegalArgumentException(EMSG2);
		
		Alphabet alphabet = AutomatonFactory.createAlphabet(alphabetSet);
		
		State state0 = AutomatonFactory.createState("0");
		State state1 = AutomatonFactory.createState("1");
		State state2 = AutomatonFactory.createState("2");
		
		Set<State> states = new HashSet<>(Arrays.asList(state0,state1,state2));
		Set<State> accStates = new HashSet<>(Arrays.asList(state0,state1));
		State startState = state0;
		
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> transitions = new HashSet<>();
		transitions.add(MathFactory.createTransition(state0, a, state1));
		transitions.add(MathFactory.createTransition(state0, b, state2));
		transitions.add(MathFactory.createTransition(state1, a, state1));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a,b),state0,state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a),state1,state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Collections.emptyList(),state2,state2));
		
		TransitionFunction function = MathFactory.createTransitionFunction(transitions);		
		
		return AutomatonFactory.createDFA(states,alphabet,function,startState,accStates);
	}
	
	public static DFA chainResponseDFA(Set<Symbol> alphabetSet, Symbol a, Symbol b) {
		if(alphabetSet == null || a == null || b == null)
			throw new IllegalArgumentException(EMSG1);
		if(!alphabetSet.contains(a) || !alphabetSet.contains(b))
			throw new IllegalArgumentException(EMSG2);
		
		Alphabet alphabet = AutomatonFactory.createAlphabet(alphabetSet);
		
		State state0 = AutomatonFactory.createState("0");
		State state1 = AutomatonFactory.createState("1");
		State state2 = AutomatonFactory.createState("2");
		
		Set<State> states = new HashSet<>(Arrays.asList(state0,state1,state2));
		Set<State> accStates = new HashSet<>(Arrays.asList(state0));
		State startState = state0;
		
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> transitions = new HashSet<>();
		transitions.add(MathFactory.createTransition(state0, a, state2));
		transitions.add(MathFactory.createTransition(state2, b, state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a),state0,state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(b),state2,state1));
		transitions.addAll(createOtherwiseTransitions(alphabet,Collections.emptyList(),state1,state1));
		
		TransitionFunction function = MathFactory.createTransitionFunction(transitions);		
		
		return AutomatonFactory.createDFA(states,alphabet,function,startState,accStates);
	}
	
	public static DFA chainSuccessionDFA(Set<Symbol> alphabetSet, Symbol a, Symbol b) {
		if(alphabetSet == null || a == null || b == null)
			throw new IllegalArgumentException(EMSG1);
		if(!alphabetSet.contains(a) || !alphabetSet.contains(b))
			throw new IllegalArgumentException(EMSG2);
		
		Alphabet alphabet = AutomatonFactory.createAlphabet(alphabetSet);
		
		State state0 = AutomatonFactory.createState("0");
		State state1 = AutomatonFactory.createState("1");
		State state2 = AutomatonFactory.createState("2");
		
		Set<State> states = new HashSet<>(Arrays.asList(state0,state1,state2));
		Set<State> accStates = new HashSet<>(Arrays.asList(state0));
		State startState = state0;
		
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> transitions = new HashSet<>();
		transitions.add(MathFactory.createTransition(state0, a, state2));
		transitions.add(MathFactory.createTransition(state0, b, state1));
		transitions.add(MathFactory.createTransition(state2, b, state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a,b),state0,state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(b),state2,state1));
		transitions.addAll(createOtherwiseTransitions(alphabet,Collections.emptyList(),state1,state1));
		
		TransitionFunction function = MathFactory.createTransitionFunction(transitions);		
		
		return AutomatonFactory.createDFA(states,alphabet,function,startState,accStates);
	}
	
	public static DFA respondedExistenceDFA(Set<Symbol> alphabetSet, Symbol a, Symbol b) {
		if(alphabetSet == null || a == null || b == null)
			throw new IllegalArgumentException(EMSG1);
		if(!alphabetSet.contains(a) || !alphabetSet.contains(b))
			throw new IllegalArgumentException(EMSG2);
		
		Alphabet alphabet = AutomatonFactory.createAlphabet(alphabetSet);
		
		State state0 = AutomatonFactory.createState("0");
		State state1 = AutomatonFactory.createState("1");
		State state2 = AutomatonFactory.createState("2");
		
		Set<State> states = new HashSet<>(Arrays.asList(state0,state1,state2));
		Set<State> accStates = new HashSet<>(Arrays.asList(state0,state1));
		State startState = state0;
		
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> transitions = new HashSet<>();
		transitions.add(MathFactory.createTransition(state0, a, state2));
		transitions.add(MathFactory.createTransition(state0, b, state1));
		transitions.add(MathFactory.createTransition(state2, b, state1));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a,b),state0,state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(b),state2,state2));
		transitions.addAll(createOtherwiseTransitions(alphabet,Collections.emptyList(),state1,state1));
		
		TransitionFunction function = MathFactory.createTransitionFunction(transitions);		
		
		return AutomatonFactory.createDFA(states,alphabet,function,startState,accStates);
	}
	
	public static DFA coExistenceDFA(Set<Symbol> alphabetSet, Symbol a, Symbol b) {
		if(alphabetSet == null || a == null || b == null)
			throw new IllegalArgumentException(EMSG1);
		if(!alphabetSet.contains(a) || !alphabetSet.contains(b))
			throw new IllegalArgumentException(EMSG2);
		
		Alphabet alphabet = AutomatonFactory.createAlphabet(alphabetSet);
		
		State state0 = AutomatonFactory.createState("0");
		State state1 = AutomatonFactory.createState("1");
		State state2 = AutomatonFactory.createState("2");
		State state3 = AutomatonFactory.createState("3");
		
		Set<State> states = new HashSet<>(Arrays.asList(state0,state1,state2,state3));
		Set<State> accStates = new HashSet<>(Arrays.asList(state0,state1));
		State startState = state0;
		
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> transitions = new HashSet<>();
		transitions.add(MathFactory.createTransition(state0, a, state3));
		transitions.add(MathFactory.createTransition(state0, b, state2));
		transitions.add(MathFactory.createTransition(state2, a, state1));
		transitions.add(MathFactory.createTransition(state3, b, state1));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a,b),state0,state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a),state2,state2));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(b),state3,state3));
		transitions.addAll(createOtherwiseTransitions(alphabet,Collections.emptyList(),state1,state1));
		
		TransitionFunction function = MathFactory.createTransitionFunction(transitions);		
		
		return AutomatonFactory.createDFA(states,alphabet,function,startState,accStates);
	}
	
	public static DFA choice1Of2DFA(Set<Symbol> alphabetSet, Symbol a, Symbol b) {
		if(alphabetSet == null || a == null || b == null)
			throw new IllegalArgumentException(EMSG1);
		if(!alphabetSet.contains(a) || !alphabetSet.contains(b))
			throw new IllegalArgumentException(EMSG2);
		
		Alphabet alphabet = AutomatonFactory.createAlphabet(alphabetSet);
		
		State state0 = AutomatonFactory.createState("0");
		State state1 = AutomatonFactory.createState("1");
		
		Set<State> states = new HashSet<>(Arrays.asList(state0,state1));
		Set<State> accStates = new HashSet<>(Arrays.asList(state1));
		State startState = state0;
		
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> transitions = new HashSet<>();
		transitions.add(MathFactory.createTransition(state0, a, state1));
		transitions.add(MathFactory.createTransition(state0, b, state1));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a,b),state0,state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Collections.emptyList(),state1,state1));
		
		TransitionFunction function = MathFactory.createTransitionFunction(transitions);		
		
		return AutomatonFactory.createDFA(states,alphabet,function,startState,accStates);
	}
	
	public static DFA exclusiveChoice1Of2DFA(Set<Symbol> alphabetSet, Symbol a, Symbol b) {
		if(alphabetSet == null || a == null || b == null)
			throw new IllegalArgumentException(EMSG1);
		if(!alphabetSet.contains(a) || !alphabetSet.contains(b))
			throw new IllegalArgumentException(EMSG2);
		
		Alphabet alphabet = AutomatonFactory.createAlphabet(alphabetSet);
		
		State state0 = AutomatonFactory.createState("0");
		State state1 = AutomatonFactory.createState("1");
		State state2 = AutomatonFactory.createState("2");
		State state3 = AutomatonFactory.createState("3");
		
		Set<State> states = new HashSet<>(Arrays.asList(state0,state1,state2,state3));
		Set<State> accStates = new HashSet<>(Arrays.asList(state2,state3));
		State startState = state0;
		
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> transitions = new HashSet<>();
		transitions.add(MathFactory.createTransition(state0, a, state2));
		transitions.add(MathFactory.createTransition(state0, b, state3));
		transitions.add(MathFactory.createTransition(state3, a, state1));
		transitions.add(MathFactory.createTransition(state2, b, state1));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a,b),state0,state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Collections.emptyList(),state1,state1));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(b),state2,state2));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a),state3,state3));
		
		TransitionFunction function = MathFactory.createTransitionFunction(transitions);		
		
		return AutomatonFactory.createDFA(states,alphabet,function,startState,accStates);
	}
	
	public static DFA notCoExistenceDFA(Set<Symbol> alphabetSet, Symbol a, Symbol b) {
		if(alphabetSet == null || a == null || b == null)
			throw new IllegalArgumentException(EMSG1);
		if(!alphabetSet.contains(a) || !alphabetSet.contains(b))
			throw new IllegalArgumentException(EMSG2);
		
		Alphabet alphabet = AutomatonFactory.createAlphabet(alphabetSet);
		
		State state0 = AutomatonFactory.createState("0");
		State state1 = AutomatonFactory.createState("1");
		State state2 = AutomatonFactory.createState("2");
		State state3 = AutomatonFactory.createState("3");
		
		Set<State> states = new HashSet<>(Arrays.asList(state0,state1,state2,state3));
		Set<State> accStates = new HashSet<>(Arrays.asList(state0,state1,state3));
		State startState = state0;
		
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> transitions = new HashSet<>();
		transitions.add(MathFactory.createTransition(state0, a, state3));
		transitions.add(MathFactory.createTransition(state0, b, state1));
		transitions.add(MathFactory.createTransition(state3, b, state2));
		transitions.add(MathFactory.createTransition(state1, a, state2));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a,b),state0,state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Collections.emptyList(),state2,state2));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(b),state3,state3));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a),state1,state1));
		
		TransitionFunction function = MathFactory.createTransitionFunction(transitions);		
		
		return AutomatonFactory.createDFA(states,alphabet,function,startState,accStates);
	}
	
	public static DFA notSuccessionDFA(Set<Symbol> alphabetSet, Symbol a, Symbol b) {
		if(alphabetSet == null || a == null || b == null)
			throw new IllegalArgumentException(EMSG1);
		if(!alphabetSet.contains(a) || !alphabetSet.contains(b))
			throw new IllegalArgumentException(EMSG2);
		
		Alphabet alphabet = AutomatonFactory.createAlphabet(alphabetSet);
		
		State state0 = AutomatonFactory.createState("0");
		State state1 = AutomatonFactory.createState("1");
		State state2 = AutomatonFactory.createState("2");
		
		Set<State> states = new HashSet<>(Arrays.asList(state0,state1,state2));
		Set<State> accStates = new HashSet<>(Arrays.asList(state0,state2));
		State startState = state0;
		
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> transitions = new HashSet<>();
		transitions.add(MathFactory.createTransition(state0, a, state2));
		transitions.add(MathFactory.createTransition(state2, b, state1));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a),state0,state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Collections.emptyList(),state1,state1));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(b),state2,state2));
		
		TransitionFunction function = MathFactory.createTransitionFunction(transitions);		
		
		return AutomatonFactory.createDFA(states,alphabet,function,startState,accStates);
	}
	
	public static DFA notChainSuccessionDFA(Set<Symbol> alphabetSet, Symbol a, Symbol b) {
		if(alphabetSet == null || a == null || b == null)
			throw new IllegalArgumentException(EMSG1);
		if(!alphabetSet.contains(a) || !alphabetSet.contains(b))
			throw new IllegalArgumentException(EMSG2);
		
		Alphabet alphabet = AutomatonFactory.createAlphabet(alphabetSet);
		
		State state0 = AutomatonFactory.createState("0");
		State state1 = AutomatonFactory.createState("1");
		State state2 = AutomatonFactory.createState("2");
		
		Set<State> states = new HashSet<>(Arrays.asList(state0,state1,state2));
		Set<State> accStates = new HashSet<>(Arrays.asList(state0,state2));
		State startState = state0;
		
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> transitions = new HashSet<>();
		transitions.add(MathFactory.createTransition(state0, a, state2));
		transitions.add(MathFactory.createTransition(state2, a, state2));
		transitions.add(MathFactory.createTransition(state2, b, state1));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a),state0,state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Collections.emptyList(),state1,state1));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a,b),state2,state0));
		
		TransitionFunction function = MathFactory.createTransitionFunction(transitions);		
		
		return AutomatonFactory.createDFA(states,alphabet,function,startState,accStates);
	}
	
	public static DFA notRespondedExistenceDFA(Set<Symbol> alphabetSet, Symbol a, Symbol b) {
		if(alphabetSet == null || a == null || b == null)
			throw new IllegalArgumentException(EMSG1);
		if(!alphabetSet.contains(a) || !alphabetSet.contains(b))
			throw new IllegalArgumentException(EMSG2);
		
		Alphabet alphabet = AutomatonFactory.createAlphabet(alphabetSet);
		
		State state0 = AutomatonFactory.createState("0");
		State state1 = AutomatonFactory.createState("1");
		State state2 = AutomatonFactory.createState("2");
		State state3 = AutomatonFactory.createState("3");
		
		Set<State> states = new HashSet<>(Arrays.asList(state0,state1,state2,state3));
		Set<State> accStates = new HashSet<>(Arrays.asList(state0,state1,state3));
		State startState = state0;
		
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> transitions = new HashSet<>();
		transitions.add(MathFactory.createTransition(state0, a, state1));
		transitions.add(MathFactory.createTransition(state0, b, state3));
		transitions.add(MathFactory.createTransition(state1, b, state2));
		transitions.add(MathFactory.createTransition(state3, a, state2));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a,b),state0,state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(b),state1,state1));
		transitions.addAll(createOtherwiseTransitions(alphabet,Collections.emptyList(),state2,state2));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a),state3,state3));
		
		TransitionFunction function = MathFactory.createTransitionFunction(transitions);		
		
		return AutomatonFactory.createDFA(states,alphabet,function,startState,accStates);
	}
	
	public static DFA notResponseDFA(Set<Symbol> alphabetSet, Symbol a, Symbol b) {
		if(alphabetSet == null || a == null || b == null)
			throw new IllegalArgumentException(EMSG1);
		if(!alphabetSet.contains(a) || !alphabetSet.contains(b))
			throw new IllegalArgumentException(EMSG2);
		
		Alphabet alphabet = AutomatonFactory.createAlphabet(alphabetSet);
		
		State state0 = AutomatonFactory.createState("0");
		State state1 = AutomatonFactory.createState("1");
		State state2 = AutomatonFactory.createState("2");
		
		Set<State> states = new HashSet<>(Arrays.asList(state0,state1,state2));
		Set<State> accStates = new HashSet<>(Arrays.asList(state0,state1));
		State startState = state0;
		
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> transitions = new HashSet<>();
		transitions.add(MathFactory.createTransition(state0, a, state1));
		transitions.add(MathFactory.createTransition(state1, b, state2));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(a),state0,state0));
		transitions.addAll(createOtherwiseTransitions(alphabet,Arrays.asList(b),state1,state1));
		transitions.addAll(createOtherwiseTransitions(alphabet,Collections.emptyList(),state2,state2));
		
		TransitionFunction function = MathFactory.createTransitionFunction(transitions);		
		
		return AutomatonFactory.createDFA(states,alphabet,function,startState,accStates);
	}
	
	private static Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> createOtherwiseTransitions(Alphabet alphabet, List<Symbol> symbols, State origin, State dest){
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> transitions = new HashSet<>();
		
		for(Symbol other: SetOperations.complement(alphabet.getSymbols(), new HashSet<>(symbols))) 
			transitions.add(MathFactory.createTransition(origin, other, dest));
		
		return transitions;
	}
}
