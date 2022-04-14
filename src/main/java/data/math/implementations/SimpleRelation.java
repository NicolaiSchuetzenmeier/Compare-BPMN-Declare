package data.math.implementations;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import data.math.definitions.Function;
import data.math.definitions.Relation;
import data.math.definitions.BinaryTuple;

class SimpleRelation<X,Y> implements Relation<X, Y> {

	private Set<X> domain;
	private Set<Y> codomain;
	private Set<BinaryTuple<X,Y>> tupels;
	private boolean isFunction;
		
	SimpleRelation(Set<BinaryTuple<X,Y>> tupels) {
		if(tupels == null)
			throw new IllegalArgumentException("The Arguments of a relation must not be null!");
		
		if(tupels.contains(null))
			throw new IllegalArgumentException("The tupel set of a relation must not include null!");
		
		this.tupels = new HashSet<>(tupels);
		domain = new HashSet<>();
		codomain = new HashSet<>();
		for(BinaryTuple<X,Y> tupel: tupels)	{
			domain.add(tupel.getFirst());
			codomain.add(tupel.getSecond());
		}
		
		isFunction = isRightUnique();
	}
	
	@Override
	public Set<X> getDomain() {
		return Collections.unmodifiableSet(domain);
	}

	@Override
	public Set<Y> getCodomain() {
		return Collections.unmodifiableSet(codomain);
	}

	@Override
	public Set<BinaryTuple<X, Y>> getTuples() {
		return Collections.unmodifiableSet(tupels);
	}

	@Override
	public Set<X> getPreimage(Y value) {
		if(value == null)
			throw new IllegalArgumentException("The value must not be null!");
		
		Set<X> sourceSet = new HashSet<>();
		for(BinaryTuple<X,Y> tupel: tupels)
			if(tupel.getSecond().equals(value))
				sourceSet.add(tupel.getFirst());
		
		return Collections.unmodifiableSet(sourceSet);
	}

	@Override
	public Set<Y> getImage(X value) {
		if(value == null)
			throw new IllegalArgumentException("The value must not be null!");
		
		Set<Y> targetSet = new HashSet<>();
		for(BinaryTuple<X,Y> tupel: tupels)
			if(tupel.getFirst().equals(value))
				targetSet.add(tupel.getSecond());
		
		return Collections.unmodifiableSet(targetSet);
	}

	@Override
	public boolean isFunction() {
		return isFunction;
	}

	@Override
	public Function<X,Y> getFunction() {
		if(!isFunction)
			throw new UnsupportedOperationException("This relation is not a function!");
		
		return MathFactory.createFunction(tupels);
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(!(o instanceof Relation)) return false;
		
		Relation<?,?> r = (Relation<?,?>) o;
		return r.getTuples().equals(getTuples());
	}

	@Override
	public int hashCode() {
		int i = 0;
		for(BinaryTuple<X,Y> tupel: tupels)
			i += tupel.hashCode();
		return i;
	}

	@Override
	public String toString() {
		return tupels.toString();
	}

	@Override
	public boolean isRightUnique() {
		for(BinaryTuple<X,Y> tupel: getTuples())
			if(getImage(tupel.getFirst()).size() > 1)
				return false;
		return true;
	}

	@Override
	public boolean isLeftUnique() {
		for(BinaryTuple<X,Y> tupel: getTuples())
			if(getPreimage(tupel.getSecond()).size() > 1)
				return false;
		return true;
	}
}
