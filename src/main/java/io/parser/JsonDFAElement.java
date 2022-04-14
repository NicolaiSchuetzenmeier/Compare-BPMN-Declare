package io.parser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

enum JsonDFAElement {

	STATES,
	ACCEPTINGSTATES,
	STARTSTATE,
	ALPHABET,
	TRANSFUNCTION;
	
	private static JsonDFAElement[] elements = {STATES,ALPHABET,TRANSFUNCTION,STARTSTATE,ACCEPTINGSTATES};
	
	String getDeclaration() {
		switch(this) {
		case ACCEPTINGSTATES:
			return "AcceptingStates";
		case ALPHABET:
			return "Alphabet";
		case STARTSTATE:
			return "StartState";
		case STATES:
			return "States";
		case TRANSFUNCTION:
			return "TransitionFunction";
		default:
			throw new UnsupportedOperationException();
		}
	}
	
	static List<JsonDFAElement> getAllElements(){
		return Collections.unmodifiableList(Arrays.asList(elements));
	}
}
