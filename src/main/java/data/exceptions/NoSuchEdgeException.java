package data.exceptions;

public class NoSuchEdgeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6626573085408947551L;

	public NoSuchEdgeException(String msg) {
		super(msg);
	}
	
	public NoSuchEdgeException() {
		super();
	}
}
