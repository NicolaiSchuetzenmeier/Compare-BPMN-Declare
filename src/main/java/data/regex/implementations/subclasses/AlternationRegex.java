package data.regex.implementations.subclasses;

import data.algorithms.util.SetOperations;
import data.regex.definitions.Regex;

public class AlternationRegex extends Regex {
	
	private Regex regex1;
	private Regex regex2;
	
	public AlternationRegex(Regex regex1, Regex regex2){			
		alphabet = SetOperations.unionAlphabet(regex1.getAlphabet(), regex2.getAlphabet());
		this.regex1 = regex1;
		this.regex2 = regex2;
	}

	public Regex getFirst() {
		return regex1;
	}
	
	public Regex getSecond() {
		return regex2;
	}

	@Override
	public String getRegexString() {
		return regex1.getRegexString() + " + " + regex2.getRegexString();
	}
}
