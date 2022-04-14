package constraints.implementations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import constraints.definitions.Constraint;
import constraints.definitions.ModelDefinition;
import data.automaton.definitions.Symbol;

public class SimpleModelDefinition implements ModelDefinition {
	
	private Set<Symbol> activities;
	private Collection<Constraint> constraints;
	
	SimpleModelDefinition(Set<Symbol> activities, Collection<Constraint> constraints){
		if(activities == null || constraints == null)
			throw new IllegalArgumentException("The input parameters of a model definition must not be null!");
		
		for(Constraint constraint: constraints)
			if(!activities.containsAll(constraint.getSymbolParameters()))
				throw new IllegalArgumentException("The set of all activities in a model definition must include all used activities!");
		
		this.activities = new HashSet<>(activities);
		this.constraints = new ArrayList<>(constraints);
	}
	
	@Override
	public Set<Symbol> getActivities() {
		return Collections.unmodifiableSet(activities);
	}

	@Override
	public Collection<Constraint> getConstraints() {
		return Collections.unmodifiableCollection(constraints);
	}

}
