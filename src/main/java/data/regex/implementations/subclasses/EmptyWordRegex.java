package data.regex.implementations.subclasses;

import java.util.Collections;

import data.automaton.implementations.AutomatonFactory;
import data.regex.definitions.Regex;

public class EmptyWordRegex extends Regex {

	public static final EmptyWordRegex EMPTYWORD = new EmptyWordRegex();
	
	private EmptyWordRegex() {
		alphabet = AutomatonFactory.createAlphabet(Collections.emptySet());
	}

	@Override
	public String getRegexString() {
		return "_";
	}
	
}
