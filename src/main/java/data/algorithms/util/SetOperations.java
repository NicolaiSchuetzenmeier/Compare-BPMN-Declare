package data.algorithms.util;

import java.util.HashSet;
import java.util.Set;

import data.automaton.definitions.Alphabet;
import data.automaton.implementations.AutomatonFactory;
import data.math.definitions.BinaryTuple;
import data.math.implementations.MathFactory;

/**
 * An Utility class for set operations.
 *
 */
public class SetOperations {
	
	private SetOperations() {}
	
	/**
	 * This method calculates the cartesian product S1 x S2 of two sets as a new set.
	 * @param <X> type of elements in the first set
	 * @param <Y> type of elements in the second set
	 * @param set1 the first set S1
	 * @param set2 the second set S2
	 * @return the cartesian product as a set of binary tuples.
	 */
	public static <X,Y> Set<BinaryTuple<X,Y>> cartesianProduct(Set<X> set1, Set<Y> set2){
		Set<BinaryTuple<X,Y>> tupels = new HashSet<>();
		for(X value1: set1)
			for(Y value2: set2)
				tupels.add(MathFactory.createBinaryTuple(value1, value2));
		return tupels;
	}
	
	/**
	 * Calculates the union of two sets as a new set.
	 * @param <X> shared type of elements of either set.
	 * @param set1 the first set
	 * @param set2 the second set
	 * @return A set containing all elements that were either in set1 or set2
	 */
	public static <X> Set<X> union(Set<X> set1, Set<X> set2){
		Set<X> union = new HashSet<>();
		union.addAll(set1);
		union.addAll(set2);
		return union;
	}
	
	/**
	 * Calculates the intersection of two sets as a new set.
	 * @param <X> shared type of elements of either set.
	 * @param set1 the first set
	 * @param set2 the second set
	 * @return A set containing all elements that were both in set1 and in set2
	 */
	public static <X> Set<X> intersection(Set<X> set1, Set<X> set2){
		Set<X> intersection = new HashSet<>();
		for(X value1: set1)
			if(set2.contains(value1))
				intersection.add(value1);
		return intersection;
	}
	
	/**
	 * Calculates the relative complement of set2 in set1 as a new set.
	 * Set2 does not have to be a subset of set1.
	 * @param <X> shared type of elements of either set.
	 * @param set1 the first set
	 * @param set2 the second set
	 * @return A set containing all elements that were in set1 but not in set2
	 */
	public static <X> Set<X> complement(Set<X> set1, Set<X> set2){
		Set<X> complement = new HashSet<>(set1);
		complement.removeAll(set2);
		return complement;
	}

	/**
	 * Calculates the union of two alphabets as a new alphabet.
	 * @param alphabet1 the first alphabet
	 * @param alphabet2 the second alphabet
	 * @return the union of both alphabets
	 */
	public static Alphabet unionAlphabet(Alphabet alphabet1, Alphabet alphabet2){
		return AutomatonFactory.createAlphabet(union(alphabet1.getSymbols(),alphabet2.getSymbols()));
	}

	/**
	 * Calculates the intersection of two alphabets as a new alphabet.
	 * @param alphabet1 the first alphabet
	 * @param alphabet2 the second alphabet
	 * @return the intersection of both alphabets
	 */
	public static Alphabet intersectionAlphabet(Alphabet alphabet1, Alphabet alphabet2){
		return AutomatonFactory.createAlphabet(intersection(alphabet1.getSymbols(),alphabet2.getSymbols()));
	}

	/**
	 * Calculates the relative complement of alphabet2 in alphabet1 as a new alphabet.
	 * Alphabet2 does not have to be a subset of alphabet1.
	 * @param alphabet1 the first alphabet
	 * @param alphabet2 the second alphabet
	 * @return relative complement of alphabet2 in alphabet1
	 */
	public static Alphabet complementAlphabet(Alphabet alphabet1, Alphabet alphabet2){
		return AutomatonFactory.createAlphabet(complement(alphabet1.getSymbols(),alphabet2.getSymbols()));
	}
}
