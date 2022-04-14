package data.regex.implementations;

import data.automaton.definitions.DFA;
import data.regex.definitions.RegexDFA;

public class RegexDFAFactory {
	
	private RegexDFAFactory() {}
	
	public static RegexDFA createRegexDFA(DFA dfa) {
		return new FastRegexDFA(dfa);
	}

}
