package data.math.definitions;

import java.util.List;

/**
 * Interface for a n tuple of the form (x1, ... ,xn)
 *
 * @param <T> type of all elements of the tuple
 */
public interface Tuple <T>{
	/**
	 * Gives an ordered list of all tuple elements
	 * @return list of tuple elements
	 */
	public List<T> getElements();
}
