package data.math.implementations;

import java.util.Set;

import data.math.definitions.Function;
import data.math.definitions.Relation;
import data.math.definitions.BinaryTuple;
import data.math.exceptions.OutOfDomainException;
/**
 * A basic implementation of the function interface.
 *
 * @param <X> type of the domain.
 * @param <Y> type of the codomain.
 */
class SimpleFunction <X,Y>  extends SimpleRelation<X,Y> implements Function<X, Y>{
	
	/**
	 * @param tupels the tuple set that defines the function.
	 * @throws IllegalArgumentException if the given tuple set does not define a function (is not right-unique) or either contains null or is null.
	 */
	SimpleFunction(Set<BinaryTuple<X,Y>> tupels) {
		super(tupels);
		
		if(!isFunction())
			throw new IllegalArgumentException("The given tupel set does not define a function!");
	}
	
	@Override
	public Function<X, Y> getFunction() {
		return this;
	}

	@Override
	public Relation<X, Y> getRelation() {
		return this;
	}

	@Override
	public Y apply(X value) throws OutOfDomainException{
		if(value == null)
			throw new IllegalArgumentException("Value must not be null!");
		for(BinaryTuple<X,Y> tupel: getTuples())
			if(tupel.getFirst().equals(value))
				return tupel.getSecond();
		throw new OutOfDomainException();
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder("[");
		int i = 0;
		for(BinaryTuple<X,Y> tupel: getTuples()) {
			s.append("\"" + tupel.getFirst() + " -> " + tupel.getSecond() + "\"");
			if(i < getTuples().size() - 1)
					s.append(", ");
			i++;
		}
		s.append("]");
		return s.toString();
	}

}
