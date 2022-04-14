package data.automaton.definitions;

import java.util.Collection;
import java.util.Set;

/**
 * An interface for a Alphabet. Meaning a finite set of symbols.
 *
 */
public interface Alphabet extends Iterable<Symbol>{
	
	/**
	 *  @return the set of symbols of this alphabet.
	 */
	public Set<Symbol> getSymbols();
	
	public boolean contains(Symbol symbol);
	
	public boolean containsAll(Collection<Symbol> symbols);
	
	public int size();
	
	public boolean isEmpty();
	
	/**
	 * Checks whether a word is in the kleene star of this alphabet.
	 * @param word word to check
	 * @return true if and only if the word is in the kleene star of this alphabet.
	 */
	public boolean isInKleeneStar(String word);
}
