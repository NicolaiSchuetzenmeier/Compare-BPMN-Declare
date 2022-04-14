package data.algorithms.util;

import java.util.HashSet;
import java.util.Set;

import data.automaton.definitions.DFA;
import data.automaton.definitions.State;
import data.automaton.definitions.Symbol;
import data.math.exceptions.OutOfDomainException;
/**
 * Provides utility methods for DFA calculations.
 *
 */
public class DFAUtil {

	private static final String NULLINPUTMSG = "This method does not allow null parameters!";
	private static final String NOTELEMENTOFSTATESMSG = "This input state is not element of the state set!";
	private static final String NOTELEMENTSOFSTATESMSG = "These input states are not (all) elements of the state set!";
	private static final String NOTELEMENTOFALPHABETMSG = "This symbol is not element of the alphabet!";

	private DFAUtil() {}
	
	/**
	 * This method returns all states that are reachable from the origin state in this DEA's graph.
	 * @param dfa the dfa to work on
	 * @param origin the state from which reachable states have to be reached.
	 * @throws IllegalArgumentException if origin is not element of the dfas state set
	 * @return all states that can be reached from origin
	 */
	public static Set<State> getAllReachableStates(DFA dfa, State origin) {
		if(dfa == null || origin == null)
			throw new IllegalArgumentException(NULLINPUTMSG);		
		if(!dfa.getStates().contains(origin))
			throw new IllegalArgumentException(NOTELEMENTOFSTATESMSG);		
		
		Set<State> reachable = new HashSet<>(dfa.getStates());
		reachable.removeAll(getAllUnReachableStates(dfa,origin));
		
		return reachable;
	}

	/**
	 * This method returns all states for which a path to the destination exists in this DEA's graph.
	 * @param dfa the dfa to work on
	 * @param destination state that has to be reached
	 * @throws IllegalArgumentException if destination is not element of the dfas state set
	 * @return all states from which destination can be reached.
	 */
	public static Set<State> getAllReachingStates(DFA dfa, State destination) {
		if(dfa == null || destination == null)
			throw new IllegalArgumentException(NULLINPUTMSG);		
		if(!dfa.getStates().contains(destination))
			throw new IllegalArgumentException(NOTELEMENTOFSTATESMSG);
		
		Set<State> reaching = new HashSet<>(dfa.getStates());
		reaching.removeAll(getAllNonReachingStates(dfa,destination));
		
		return reaching;
	}

	/**
	 * This method returns all states that are not reachable from the origin state in this DEA's graph.
	 * @param dfa the dfa to work on
	 * @param origin the state from which reachable states have to be reached.
	 * @throws IllegalArgumentException if origin is not element of the dfas state set
	 * @return all states that can not be reached from origin
	 */
	public static Set<State> getAllUnReachableStates(DFA dfa, State origin) {
		if(dfa == null || origin == null)
			throw new IllegalArgumentException(NULLINPUTMSG);		
		if(!dfa.getStates().contains(origin))
			throw new IllegalArgumentException(NOTELEMENTOFSTATESMSG);
		
		Set<State> unvisited = new HashSet<>(dfa.getStates());
		
		visit(dfa,unvisited,origin);
		
		return unvisited;
	}

	/**
	 * This method returns all states for which no path to the destination exists in this DEA's graph.
	 * @param dfa the dfa to work on
	 * @param destination state that would have to be reached
	 * @throws IllegalArgumentException if destination is not element of the dfas state set
	 * @return all states from which destination can not be reached.
	 */
	public static Set<State> getAllNonReachingStates(DFA dfa, State destination) {
		if(dfa == null || destination == null)
			throw new IllegalArgumentException(NULLINPUTMSG);		
		if(!dfa.getStates().contains(destination))
			throw new IllegalArgumentException(NOTELEMENTOFSTATESMSG);
		
		Set<State> unvisited = new HashSet<>(dfa.getStates());
		
		visitReverse(dfa,unvisited,destination);
		
		return unvisited;
	}

	/**
	 * This method returns all states that are reachable from the origin state in this DEA's graph by reading the given symbol.
	 * @param dfa the dfa to work on
	 * @param origin the state from which reachable states have to be reached.
	 * @param symbol the symbol that has to be read
	 * @throws IllegalArgumentException if origin is not element of the dfas state set
	 * @return all states that can be reached from origin by reading symbol
	 */
	public static Set<State> getAllDirectlyReachableStates(DFA dfa, State origin, Symbol symbol) {
		if(dfa == null || origin == null || symbol == null)
			throw new IllegalArgumentException(NULLINPUTMSG);		
		if(!dfa.getStates().contains(origin))
			throw new IllegalArgumentException(NOTELEMENTOFSTATESMSG);		
		if(!dfa.getAlphabet().contains(symbol))
			throw new IllegalArgumentException(NOTELEMENTOFALPHABETMSG);		
		
		Set<State> reachable = new HashSet<>();
		
		try {
			reachable.add(dfa.getTransitionFunction().apply(origin,symbol));
		} catch (OutOfDomainException e) {}
		
		return reachable;
	}

	/**
	 * This method returns all states that are reachable from any state in the origin set by reading the given symbol.
	 * @param dfa the dfa to work on
	 * @param origins the set of states from which reachable states have to be reached.
	 * @param symbol the symbol that has to be read
	 * @throws IllegalArgumentException if origins are not all elements of the dfas state set
	 * @return all states that can be reached from any origin state by reading symbol
	 */
	public static Set<State> getAllDirectlyReachableStatesFromSet(DFA dfa, Set<State> origins, Symbol symbol) {
		if(dfa == null || origins == null || symbol == null)
			throw new IllegalArgumentException(NULLINPUTMSG);		
		if(!dfa.getStates().containsAll(origins))
			throw new IllegalArgumentException(NOTELEMENTSOFSTATESMSG);		
		if(!dfa.getAlphabet().contains(symbol))
			throw new IllegalArgumentException(NOTELEMENTOFALPHABETMSG);	
		
		Set<State> reachable = new HashSet<>();
		
		for(State origin: origins)
			reachable.addAll(getAllDirectlyReachableStates(dfa,origin, symbol));
		
		return reachable;
	}

	/**
	 * This method returns all states from which the destination state can be reached by reading the given symbol.
	 * @param dfa the dfa to work on
	 * @param destination the state to reach
	 * @param symbol the symbol that has to be read.
	 * @throws IllegalArgumentException if destination is not element of the dfas state set
	 * @return all states that can reach destination.
	 */
	public static Set<State> getAllDirectlyReachingStates(DFA dfa, State destination, Symbol symbol) {
		if(dfa == null || destination == null || symbol == null)
			throw new IllegalArgumentException(NULLINPUTMSG);		
		if(!dfa.getStates().contains(destination))
			throw new IllegalArgumentException(NOTELEMENTOFSTATESMSG);		
		if(!dfa.getAlphabet().contains(symbol))
			throw new IllegalArgumentException(NOTELEMENTOFALPHABETMSG);	
		
		Set<State> reaching = new HashSet<>();
		
		for(State state: dfa.getTransitionFunction().getPreimageStates(symbol,destination))
			reaching.add(state);
		
		return reaching;
	}
	
	/**
	 * This method returns all states from which any state in the destination set can be reached by reading the given symbol.
	 * @param dfa the dfa to work on
	 * @param destinations the set of states to reach.
	 * @param symbol the symbol that has to be read.
	 * @throws IllegalArgumentException if destinations are not all elements of the dfas state set
	 * @return all states that can reach any state in the destinations set.
	 */
	public static Set<State> getAllDirectlyReachingStates(DFA dfa, Set<State> destinations, Symbol symbol) {
		if(dfa == null || destinations == null || symbol == null)
			throw new IllegalArgumentException(NULLINPUTMSG);		
		if(!dfa.getStates().containsAll(destinations))
			throw new IllegalArgumentException(NOTELEMENTSOFSTATESMSG);		
		if(!dfa.getAlphabet().contains(symbol))
			throw new IllegalArgumentException(NOTELEMENTOFALPHABETMSG);	
		
		Set<State> reaching = new HashSet<>();
		
		for(State destination: destinations)
			for(State origin: dfa.getTransitionFunction().getPreimageStates(symbol,destination))
				reaching.add(origin);
		
		return reaching;
	}

	/**
	 * Visits every unvisited but visitable state from the current state in the DFA's graph and marks them.
	 * @param dfa the dfa to work on
	 * @param unvisited the set of states that have not yet been visited
	 * @param current the current state.
	 */
	private static void visit(DFA dfa, Set<State> unvisited, State current) {
		if(!unvisited.contains(current))
			return;
		unvisited.remove(current);
		for(Symbol symbol: dfa.getAlphabet())
			try {
				visit(dfa,unvisited,dfa.getTransitionFunction().apply(current, symbol));
			} catch (OutOfDomainException e) {}
	}
	

	/**
	 * Visits every unvisited but visitable state from the current state in an graph with inverted edges and marks them.
	 * @param dfa the dfa to work on
	 * @param unvisited the set of states that have not yet been visited
	 * @param current the current state.
	 */
	private static void visitReverse(DFA dfa, Set<State> unvisited, State current) {
		if(!unvisited.contains(current))
			return;

		unvisited.remove(current);
		
		for(Symbol symbol: dfa.getAlphabet())
				for(State state: dfa.getTransitionFunction().getPreimageStates(symbol,current))
					visitReverse(dfa,unvisited,state);
	}
}
