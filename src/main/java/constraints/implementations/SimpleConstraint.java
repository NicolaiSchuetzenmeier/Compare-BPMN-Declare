package constraints.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import constraints.definitions.Constraint;
import constraints.definitions.ConstraintType;
import constraints.exceptions.ConstraintGenerationException;
import data.automaton.definitions.Symbol;

public class SimpleConstraint implements Constraint {

	private ConstraintType type;
	private List<Symbol> symbolParameters;
	private int intParameter;
	
	SimpleConstraint(ConstraintType type, List<Symbol> parameters) throws ConstraintGenerationException{
		if(type == null || parameters == null)
			throw new IllegalArgumentException("The input parameters of a constraint must not be null!");
		if(!type.areValidParameters(parameters, -1))
			throw new ConstraintGenerationException("The parameters must fit the constraint type!");
		
		this.symbolParameters = new ArrayList<>(parameters);
		this.type = type;
		intParameter = -1;
	}
	
	SimpleConstraint(ConstraintType type, List<Symbol> parameters, int intParameter) throws ConstraintGenerationException{
		if(type == null || parameters == null)
			throw new IllegalArgumentException("The input parameters of a constraint must not be null!");
		if(!type.areValidParameters(parameters, intParameter))
			throw new ConstraintGenerationException("The parameters must fit the constraint type!");
		
		this.symbolParameters = new ArrayList<>(parameters);
		this.type = type;
		this.intParameter = intParameter;
	}

	@Override
	public ConstraintType getConstraintType() {
		return type;
	}

	@Override
	public List<Symbol> getSymbolParameters() {
		return Collections.unmodifiableList(symbolParameters);
	}

	@Override
	public int getIntegerParameter() {
		return intParameter;
	}
}
