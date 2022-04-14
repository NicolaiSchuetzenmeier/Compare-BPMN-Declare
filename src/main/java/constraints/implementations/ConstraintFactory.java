package constraints.implementations;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import constraints.definitions.*;
import constraints.exceptions.ConstraintGenerationException;
import data.automaton.definitions.Symbol;

public class ConstraintFactory {

	private ConstraintFactory() {}
	
	public static Constraint createConstraint(ConstraintType type, List<Symbol> parameters) throws ConstraintGenerationException {
		return new SimpleConstraint(type,parameters);
	}
	
	public static Constraint createCountConstraint(ConstraintType type, List<Symbol> parameters, int intParameter) throws ConstraintGenerationException {
		return new SimpleConstraint(type,parameters,intParameter);
	}
	
	public static ModelDefinition createModelDefinition(Set<Symbol> activities, Collection<Constraint> constraints) {
		return new SimpleModelDefinition(activities,constraints);
	}
}
