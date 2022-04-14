package data.math.implementations;

import data.math.definitions.BinaryTuple;

/**
 * A simple implementation of the tuple interface.
 * Represents a tuple (a,b).
 * 
 * @param <X> Type of the first element a.
 * @param <Y> Type of the second element b.
 */
class SimpleBinaryTuple <X,Y> implements BinaryTuple<X, Y> {
	
	
	private X first;
	
	private Y second;
	
	/**
	 * @param first the first value of the tuple
	 * @param second the second value of the tuple
	 * @throws IllegalArgumentException if one of the arguments is null
	 */
	SimpleBinaryTuple(X first, Y second){
		
		if(first == null || second == null) 
			throw new IllegalArgumentException("The arguments of a tupel must not be null!");
		
		this.first = first;
		this.second = second;
	}

	/** 
	 * @return the first value of this tuple
	 */
	@Override
	public X getFirst() {
		return first;
	}

	/** 
	 * @return the second value of this tuple
	 */
	@Override
	public Y getSecond() {
		return second;
	}

	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(!(o instanceof BinaryTuple)) return false;
		
		BinaryTuple<?,?> t = (BinaryTuple<?,?>) o;
		return t.getFirst().equals(getFirst()) && t.getSecond().equals(getSecond());
	}
	
	@Override
	public int hashCode() {
		return getFirst().hashCode() + getSecond().hashCode();
	}

	/** 
	 * @return A string representation of this tuple (a,b) as "(a, b)"
	 */
	@Override
	public String toString() {
		return "(" + first.toString() + ", " + second.toString() + ")";
	}

}
