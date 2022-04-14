package data.regex.definitions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import data.automaton.definitions.Alphabet;
import data.automaton.definitions.DFA;
import data.automaton.definitions.Symbol;
import data.regex.implementations.RegexDFAFactory;
import data.regex.implementations.subclasses.AlternationRegex;
import data.regex.implementations.subclasses.ConcatenationRegex;
import data.regex.implementations.subclasses.EmptySetRegex;
import data.regex.implementations.subclasses.EmptyWordRegex;
import data.regex.implementations.subclasses.KleeneStarRegex;
import data.regex.implementations.subclasses.SymbolRegex;

/**
 * An abstract class for a regular expression that only allows alternation ("+"), concatenation and kleene star ("*") operations.
 * The empty set "{}" and the empty word "_" are also valid elementary regular expressions. It also allows brackets "(", ")".
 * Each inheriting regular Expression must return a string representation and an associated alphabet.
 * This class also provides several calculation methods.
 */
public abstract class Regex {
	
	
	private static final String EMSG = "values of a simple regex must not be null!";
	
	protected Alphabet alphabet;
	
	/**
	 * Operator symbols of regular expressions of this type.
	 */
	public static final Set<Character> OPERATORSYMBOLS = new HashSet<>(Arrays.asList('+', '_', '(', ')', '{', '}', '*'));
	

	/**
	 * @return The string representation of this regular expression.
	 */
	public abstract String getRegexString();

	/**
	 * @return The alphabet of this regular expression.
	 */
	public Alphabet getAlphabet() {
		return alphabet;
	}
	
	public boolean isEmptySetRegex() {
		return equals(EmptySetRegex.EMPTYSET);
	}

	public boolean isEmptyWordRegex() {
		return equals(EmptyWordRegex.EMPTYWORD);
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(!(o instanceof Regex)) return false;
		return getRegexString().equals(((Regex)o).getRegexString());
	}

	@Override
	public int hashCode() {
		return getRegexString().hashCode();
	}

	@Override
	public String toString() {
		return getRegexString();
	}
	
	
	public static Regex getEmptySetRegex() {
		return EmptySetRegex.EMPTYSET;
	}
	
	public static Regex getEmptyWordRegex() {
		return EmptyWordRegex.EMPTYWORD;
	}
	
	public static Regex getSingleSymbolRegex(Symbol symbol) {
		if(symbol == null)
			throw new IllegalArgumentException(EMSG);
		return new SymbolRegex(symbol);
	}
	
	public static Regex alternate(Regex regex1, Regex regex2) {
		if(regex1 == null || regex2 == null)
			throw new IllegalArgumentException(EMSG);
		
		if(regex1.equals(EmptySetRegex.EMPTYSET))
			return regex2;
		if(regex2.equals(EmptySetRegex.EMPTYSET))
			return regex1;
		
		return new AlternationRegex(regex1,regex2);
	}
	
	public static Regex concatenate(Regex regex1,Regex regex2) {
		if(regex1 == null || regex2 == null)
			throw new IllegalArgumentException(EMSG);

		if(regex1.equals(EmptySetRegex.EMPTYSET))
			return EmptySetRegex.EMPTYSET;
		if(regex2.equals(EmptySetRegex.EMPTYSET))
			return EmptySetRegex.EMPTYSET;
		if(regex1.equals(EmptyWordRegex.EMPTYWORD))
			return regex2;
		if(regex2.equals(EmptyWordRegex.EMPTYWORD))
			return regex1;
		
		return new ConcatenationRegex(regex1,regex2);
	}
	
	public static Regex kleeneStar(Regex regex) {
		if(regex == null)
			throw new IllegalArgumentException(EMSG);

		if(regex instanceof KleeneStarRegex)
			return regex;
		if(regex.equals(EmptyWordRegex.EMPTYWORD))
			return EmptyWordRegex.EMPTYWORD;
		if(regex.equals(EmptySetRegex.EMPTYSET))
			return EmptyWordRegex.EMPTYWORD;
		
		return new KleeneStarRegex(regex);		
	}
	
	public static Regex parseRegex(String regexString) {
		//TODO
		throw new UnsupportedOperationException("Not yet implemented!");
	}
	
	public static RegexDFA convertToRegexDFA(DFA dfa) {
		return RegexDFAFactory.createRegexDFA(dfa);
	}
	
	public static Regex convertToRegex(DFA dfa) {
		return RegexDFAFactory.createRegexDFA(dfa).getRegex();
	}
}
