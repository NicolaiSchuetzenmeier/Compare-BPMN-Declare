package data.math.definitions;

import data.math.exceptions.OutOfDomainException;

/**
 * An interface for a unary function f: X -> Y.
 *
 * @param <X> Type of elements in the domain
 * @param <Y> Type of elements in the codomain
 */
public interface Function<X,Y> extends Relation<X,Y>{
	
	/**
	 * Applies the function on the given parameter.
	 * @param value the value the function has to be applied on.
	 * @return the value of f(value)
	 * @throws OutOfDomainException if value is not in the domain of the function.
	 */
	public Y apply(X value) throws OutOfDomainException;

	/**
	 * Returns a binary relation representation of this function.
	 * @return the relation that belongs to this function.
	 */
	public Relation<X,Y> getRelation();
}
