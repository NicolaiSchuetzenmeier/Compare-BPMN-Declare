package constraints.definitions;

import java.util.ArrayList;
import java.util.List;

import constraints.exceptions.ConstraintGenerationException;
import data.automaton.definitions.Symbol;

public enum ConstraintType {
	INIT,
	LAST,
	EXISTENCE,
	ABSENCE,
	EXACTLY,
	PRECEDENCE,
	RESPONSE,
	SUCCESSION,
	ALTERNATE_PRECEDENCE,
	ALTERNATE_RESPONSE,
	ALTERNATE_SUCCESSION,
	CHAIN_PRECEDENCE,
	CHAIN_RESPONSE,
	CHAIN_SUCCESSION,
	RESPONDED_EXISTENCE,
	CO_EXISTENCE,
	CHOICE1OF2,
	EXCLUSIVE_CHOICE1OF2,
	NOT_CO_EXISTENCE,
	NOT_SUCCESSION,
	NOT_CHAIN_SUCCESSION,
	NOT_RESPONDED_EXISTENCE,
	NOT_RESPONSE;
	
	public boolean isUnary() {
		if(this == INIT || this == LAST)
			return true;
		return false;
	}
	
	public boolean isBinary() {
		return !isUnary() && !isCount();
	}
	
	public boolean isCount() {
		if(this == EXISTENCE || this == ABSENCE || this == EXACTLY)
			return true;
		return false;
	}
	
	public boolean areValidParameters(List<Symbol> symbolParameters, int intParameter) {
		if(isUnary() && symbolParameters.size() == 1)
			return true;
		if(isBinary() && symbolParameters.size() == 2)
			return true;
		if(isCount() && symbolParameters.size() == 1 && intParameter > 0)
			return true;
		return false;
	}
	
	@Override
	public String toString() {
		return name().toLowerCase();
	}
	
	public static ConstraintType getConstraintType(String name) throws ConstraintGenerationException {
		for(ConstraintType type: ConstraintType.values())
			if(type.toString().equals(name))
				return type;
		throw new ConstraintGenerationException("No such constraint type exists!");
	}
	
	public static List<String> getConstraintCalls(){
		List<String> list = new ArrayList<>(ConstraintType.values().length);
		for(ConstraintType constraint: ConstraintType.values()) {
			if(constraint.isUnary())
				list.add(constraint.toString() + "(a)");
			else if(constraint.isBinary())
				list.add(constraint.toString() + "(a,b)");
			else if(constraint.isCount())
				list.add(constraint.toString() + "(a,n)");
		}
		return list;
	}
}
