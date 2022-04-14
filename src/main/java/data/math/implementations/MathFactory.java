package data.math.implementations;

import java.util.List;
import java.util.Set;

import data.automaton.definitions.State;
import data.automaton.definitions.Symbol;
import data.math.definitions.Function;
import data.math.definitions.Relation;
import data.math.definitions.TransitionFunction;
import data.math.definitions.Tuple;
import data.math.definitions.BinaryTuple;
/**
 * A factory able to create multiple mathematical objects.
 *
 */
public class MathFactory {
	
	private MathFactory() {}

	/**
	 * Creates a relation.
	 * @param <X> type of the relations domain.
	 * @param <Y> type of the relations codomain.
	 * @param tupels the set of tuples, that are supposed to define the relation.
	 * @return a relation object based on the tuple set.
	 */
	public static <X,Y> Relation<X,Y> createRelation(Set<BinaryTuple<X,Y>> tupels){
		return new SimpleRelation<>(tupels);
	}
	
	/**
	 * Creates a function.
	 * @param <X> type of the functions domain.
	 * @param <Y> type of the functions codomain.
	 * @param tupels set of tupels that define the function.
	 * @return a new function object.
	 */
	public static <X,Y> Function<X,Y> createFunction(Set<BinaryTuple<X,Y>> tupels){
		return new SimpleFunction<>(tupels);
	}
	
	/**
	 * Creates a TransitionFunction.
	 * @param tupels set of tupels that define the function.
	 * @return new transition function object.
	 */
	public static TransitionFunction createTransitionFunction(Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> tupels){
		return new SimpleTransitionFunction(tupels);
	}
	
	/**
	 * Creates a new BinaryTuple.
	 * @param <X> type of the first value.
	 * @param <Y> type of the second value.
	 * @param first first value of the tuple.
	 * @param second second value of the tuple.
	 * @return a new tuple (first,second).
	 */
	public static <X,Y> BinaryTuple<X,Y> createBinaryTuple(X first, Y second){
		return new SimpleBinaryTuple<>(first,second);
	}
	
	/**
	 * Creates a triple as a binary tuple containing a binary tuple and a value. 
	 * @param <X> type of the first value
	 * @param <Y> type of the second value
	 * @param <Z> type of the third value
	 * @param first first value of the triple
	 * @param second second value of the triple
	 * @param third third value of the triple
	 * @return a new pseudo triple ((first,second),third)
	 */
	public static <X,Y,Z> BinaryTuple<BinaryTuple<X,Y>,Z> createPseudoTriple(X first, Y second, Z third){
		return createBinaryTuple(createBinaryTuple(first,second),third);
	}

	/**
	 * Creates a new n-Tuple.
	 * @param <X> type of all elements of this tuple
	 * @param elements list of elements of this tuple
	 * @return a new n-tuple object
	 */
	public static <X> Tuple<X> createTuple(List<X> elements){
		return new SimpleTuple<>(elements);
	}

	/**
	 * Creates a Transition.
	 * @param origin the origin state of this transition
	 * @param symbol the symbol of this transition
	 * @param destination the destination state of this transition
	 * @return new transition.
	 */
	public static BinaryTuple<BinaryTuple<State,Symbol>,State> createTransition(State origin, Symbol symbol, State destination){
		return createBinaryTuple(createBinaryTuple(origin,symbol),destination);
	}
}
