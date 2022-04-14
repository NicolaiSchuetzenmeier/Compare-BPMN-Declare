package data.math.definitions;

import java.util.Set;

import data.automaton.definitions.State;
import data.automaton.definitions.Symbol;
import data.math.exceptions.OutOfDomainException;

/**
 * A special case of a function which assigns states to tuples of one state and one symbol.
 * The returned tuple set of this function contains tuples of the form ((state,symbol),state).
 * Objects of this type can be used in a DFA.
 *
 */
public interface TransitionFunction extends Function<BinaryTuple<State,Symbol>, State> {
	
	public State apply(State state, Symbol symbol) throws OutOfDomainException;

	/**
	 * Returns a set of states s with (s,a) in this functions domain for some a.
	 * @return the states of the domain of this relation.
	 */
	public Set<State> getDomainStates();

	/**
	 * @return Returns all states s for which (s,a) is in the preimage of the state target for some a. Returns an empty set if target is not in the codomain.
	 */
	public Set<State> getPreimageStates(State target);
	

	/**
	 * @return Returns all states s for which (s,symbol) is in the preimage of the state target. Returns an empty set if target is not in the codomain.
	 */
	public Set<State> getPreimageStates(Symbol symbol, State target);

}
