package data.automaton.implementations;

import data.automaton.definitions.Symbol;

class SimpleSymbol implements Symbol{
	
	private char character;
	
	SimpleSymbol(char character) {
		this.character = character;
	}

	@Override
	public char getChar() {
		return character;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(!(o instanceof Symbol)) return false;
		return character == ((Symbol)o).getChar();
	}
	
	@Override
	public int hashCode() {
		return Character.hashCode(character);
	}
	
	@Override
	public String toString() {
		return "" + character;
	}
}
