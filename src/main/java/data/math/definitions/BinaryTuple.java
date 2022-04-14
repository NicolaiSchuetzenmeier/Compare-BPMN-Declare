package data.math.definitions;

/**
 * Interface for a binary tuple of the form (a,b).
*/
public interface BinaryTuple<X,Y> {

	/** 
	 * @return the first value of this tuple
	 */
	public X getFirst();

	/** 
	 * @return the second value of this tuple
	 */
	public Y getSecond();
	
	/**
	 * Returns a String "a,b" for the tuple (a,b)
	 * @param tuple a tuple (a,b)
	 * @return the string "a,b"
	 */
	public static <X,Y> String getElementsString(BinaryTuple<X,Y> tuple) {
		return tuple.getFirst().toString() + ":" + tuple.getSecond().toString();
	}
}
