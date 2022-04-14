package data.regex.implementations.subclasses;

import java.util.HashSet;
import java.util.Set;

import data.automaton.definitions.Symbol;
import data.automaton.implementations.AutomatonFactory;
import data.regex.definitions.Regex;

public class SymbolRegex extends Regex {
	
	private Symbol symbol;
	
	public SymbolRegex(Symbol symbol){
		if(OPERATORSYMBOLS.contains(symbol.getChar()))
			throw new IllegalArgumentException("The symbol bannot be one of " + OPERATORSYMBOLS.toString() + " !");
		
		Set<Symbol> symbols = new HashSet<>();
		symbols.add(symbol);
		alphabet = AutomatonFactory.createAlphabet(symbols);
		this.symbol = symbol;
	}
	
	public Symbol getSymbol() {
		return symbol;
	}

	@Override
	public String getRegexString() {
		return "" + symbol.getChar();
	}
	
}
