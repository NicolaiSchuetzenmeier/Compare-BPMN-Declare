package data.math.definitions;

import java.util.Set;

/**
 * An interface for a binary Relation R subset of X x Y.
 *
 * @param <X> the type of the domain.
 * @param <Y> the type of the codomain.
 */
public interface Relation <X,Y> {
	
	/**
	 * @return the domain of this relation.
	 */
	public Set<X> getDomain();

	/**
	 * @return the codomain of this relation.
	 */
	public Set<Y> getCodomain();
	
	/**
	 * @return Returns all values x for which (x,value) in R. Returns an empty set if value is not in the codomain.
	 */
	public Set<X> getPreimage(Y value);

	/**
	 * @return Returns all values y for which (value,y) in R. Returns an empty set if value is not in the domain.
	 */
	public Set<Y> getImage(X value);
	
	/**
	 * @return returns a tuple set, that contains all tuples in this relation.
	 */
	public Set<BinaryTuple<X,Y>> getTuples();
	
	/**
	 * @return whether or not this relation defines a function.
	 */
	public boolean isFunction();

	/**
	 * Returns this relation as a function.
	 * Should only be called after calling isFunction().
	 * Should throw an UnsupportedOperationException otherwise.
	 * @return A function object for this relation.
	 */
	public Function<X,Y> getFunction();
	
	public boolean isRightUnique();
	
	public boolean isLeftUnique();

}
