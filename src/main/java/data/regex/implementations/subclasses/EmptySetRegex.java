package data.regex.implementations.subclasses;

import java.util.Collections;

import data.automaton.implementations.AutomatonFactory;
import data.regex.definitions.Regex;

public class EmptySetRegex extends Regex {

	public static final EmptySetRegex EMPTYSET = new EmptySetRegex();
	
	private EmptySetRegex() {
		alphabet = AutomatonFactory.createAlphabet(Collections.emptySet());
	}

	@Override
	public String getRegexString() {
		return "{}";
	}		

}
