package data.math.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import data.math.definitions.Tuple;

/**
 * A simple implementation for a n tuple as defined in the interface tuple
 *
 * @param <T> type of all elements of this tuple
 */
class SimpleTuple <T> implements Tuple<T>{

	private List<T> elements;
	
	/**
	 * Creates a simple tuple with the given elements.
	 * @param elements elements of this tuple in order
	 * @throws IllegalArgumentException if the lsit of elements is null, contains null or contains less than 2 elements
	 */
	SimpleTuple(List<T> elements){
		if(elements == null)
			throw new IllegalArgumentException("The list of elements of a tuple must not be null!");
		if(elements.contains(null))
			throw new IllegalArgumentException("The list of elements of a tuple must not contain null!");
		if(elements.isEmpty() || elements.size() == 1)
			throw new IllegalArgumentException("The list of elements of a tuple should have at least 2 elements!");
		
		this.elements = new ArrayList<>(elements);
	}
	
	@Override
	public List<T> getElements() {
		return Collections.unmodifiableList(elements);
	}

}
