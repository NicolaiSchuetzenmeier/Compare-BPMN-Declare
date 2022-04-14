package data.automaton.implementations;

import data.automaton.definitions.State;

class SimpleState implements State {
	
	private String name;
	
	SimpleState(String name) {
		if(name == null) 
			throw new IllegalArgumentException("The name of a state must not be null!");
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(!(o instanceof State)) return false;
		return name.equals(((State)o).getName());
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public String toString() {
		return name;
	}

}
