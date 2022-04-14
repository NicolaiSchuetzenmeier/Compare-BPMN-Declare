package constraints.definitions;

import java.util.List;

import data.automaton.definitions.Symbol;


public interface Constraint {
	
	public ConstraintType getConstraintType();
	
	public List<Symbol> getSymbolParameters();
	
	public int getIntegerParameter();
}
