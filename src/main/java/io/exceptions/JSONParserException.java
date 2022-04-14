package io.exceptions;

public class JSONParserException extends Exception {

	/**
	 * This is a utility Exception thrown whenever exceptions have to be caught during parsing of JSON files
	 */
	private static final long serialVersionUID = 1L;
	
	public JSONParserException(String message) {
		super(message);
	}

}
