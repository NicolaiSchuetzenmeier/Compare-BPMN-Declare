package constraints.definitions;

import java.util.Collection;
import java.util.Set;

import data.automaton.definitions.Symbol;


public interface ModelDefinition {

	public Set<Symbol> getActivities();
	
	public Collection<Constraint> getConstraints();
}
