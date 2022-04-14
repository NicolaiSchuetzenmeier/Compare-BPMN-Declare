package data.automaton.implementations;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import data.automaton.definitions.Alphabet;
import data.automaton.definitions.Symbol;

class SimpleAlphabet implements Alphabet{

	private Set<Symbol> symbols;
	

	SimpleAlphabet(Set<Symbol> symbols) {
		if(symbols == null)
			throw new IllegalArgumentException("The set of symbols for this alphabet is null!");
		for(Symbol symbol: symbols)
			if(symbol == null)
				throw new IllegalArgumentException("The set of symbols for this alphabet includes null!");
		this.symbols = new HashSet<>(symbols);
	}

	@Override
	public Set<Symbol> getSymbols() {
		return Collections.unmodifiableSet(symbols);
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(!(o instanceof Alphabet)) return false;
		return getSymbols().equals(((Alphabet) o).getSymbols());
	}
	
	@Override
	public int hashCode() {
		return symbols.hashCode();
	}
	
	@Override
	public String toString() {
		return symbols.toString();
	}

	@Override
	public boolean contains(Symbol symbol) {
		return getSymbols().contains(symbol);
	}

	@Override
	public boolean containsAll(Collection<Symbol> symbols) {
		return getSymbols().containsAll(symbols);
	}

	@Override
	public int size() {
		return getSymbols().size();
	}

	@Override
	public boolean isEmpty() {
		return getSymbols().isEmpty();
	}

	@Override
	public Iterator<Symbol> iterator() {
		return getSymbols().iterator();
	}

	@Override
	public boolean isInKleeneStar(String word) {
		for(char c: word.toCharArray())
			if(!contains(AutomatonFactory.createSymbol(c)))
				return false;
		return true;
	}
}
