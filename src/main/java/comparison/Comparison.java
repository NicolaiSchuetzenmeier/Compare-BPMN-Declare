package comparison;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import constraints.definitions.ModelDefinition;
import constraints.generation.ConstraintDFAGenerator;
import data.algorithms.DFAAlgorithms;
import data.automaton.definitions.DFA;
import data.exceptions.DFASEqualException;
import data.regex.definitions.Regex;

public class Comparison {

	private DFA firstDFA;
	private DFA secondDFA;
	
	private boolean areEquivalent;
	
	private Optional<Optional<String>> subset1Of2TestResult;
	private Optional<Optional<String>> subset2Of1TestResult;
	private Optional<Regex> regex1;
	private Optional<Regex> regex2;
	private Optional<List<String>> wordsOf1;
	private Optional<List<String>> wordsOf2;
	private Optional<List<String>> wordsOf1NotIn2;
	private Optional<List<String>> wordsOf2NotIn1;
	
	private int maxWordSize;
	
	private Comparison(ModelDefinition model1, ModelDefinition model2, int maxWordSize) {
		this.maxWordSize = maxWordSize;
		
		firstDFA = ConstraintDFAGenerator.generateDFA(model1);
		secondDFA = ConstraintDFAGenerator.generateDFA(model2);
		
		areEquivalent = DFAAlgorithms.areEquivalent(firstDFA, secondDFA);
		subset1Of2TestResult = Optional.empty();
		subset2Of1TestResult = Optional.empty();
		regex1 = Optional.empty();
		regex2 = Optional.empty();
		wordsOf1 = Optional.empty();
		wordsOf2 = Optional.empty();
		wordsOf1NotIn2 = Optional.empty();
		wordsOf2NotIn1 = Optional.empty();		
	}
	
	public static Comparison compareModels(ModelDefinition model1, ModelDefinition model2, int maxWordSize) {
		if(model1 == null || model2 == null)
			throw new IllegalArgumentException("Input parameters must not be null!");
		return new Comparison(model1,model2,maxWordSize);
	}
	
	public DFA getFirstDFA() {
		return firstDFA;
	}
	
	public DFA getSecondDFA() {
		return secondDFA;
	}
	
	public boolean areEquivalent() {
		return areEquivalent;
	}
	
	public Optional<String> subset1Of2TestResult(){
		if(subset1Of2TestResult.isPresent())
			return subset1Of2TestResult.get();
		try {
			subset1Of2TestResult = Optional.of(Optional.of(DFAAlgorithms.getWordAcceptedByOneRejectedByTwo(firstDFA, secondDFA)));
		} catch (DFASEqualException e) {
			subset1Of2TestResult = Optional.of(Optional.empty());
		}
		return subset1Of2TestResult.get();
	}
	
	public Optional<String> subset2Of1TestResult(){
		if(subset2Of1TestResult.isPresent())
			return subset2Of1TestResult.get();
		try {
			subset2Of1TestResult = Optional.of(Optional.of(DFAAlgorithms.getWordAcceptedByOneRejectedByTwo(secondDFA, firstDFA)));
		} catch (DFASEqualException e) {
			subset2Of1TestResult = Optional.of(Optional.empty());
		}
		return subset2Of1TestResult.get();
	}
	
	public Regex getFirstRegex() {
		if(regex1.isEmpty())
			regex1 = Optional.of(DFAAlgorithms.getEquivalentRegex(firstDFA));
		return regex1.get();
	}
	
	public Regex getSecondRegex() {
		if(regex2.isEmpty())
			regex2 = Optional.of(DFAAlgorithms.getEquivalentRegex(secondDFA));
		return regex2.get();
	}
	
	public List<String> getWordsOf1(){
		if(wordsOf1.isEmpty()) {
			List<String> wordsFirst = new ArrayList<>(firstDFA.acceptsUntilLength(maxWordSize));
			Collections.sort(wordsFirst);
			wordsOf1 = Optional.of(wordsFirst);
		}
		return Collections.unmodifiableList(wordsOf1.get());
	}
	
	public List<String> getWordsOf2(){
		if(wordsOf2.isEmpty()) {
			List<String> wordsSecond = new ArrayList<>(secondDFA.acceptsUntilLength(maxWordSize));
			Collections.sort(wordsSecond);
			wordsOf2 = Optional.of(wordsSecond);
		}
		return Collections.unmodifiableList(wordsOf2.get());
	}
	
	public List<String> getWordsOf1NotIn2(){
		if(wordsOf1NotIn2.isEmpty()) {
			List<String> wordsDiff12 = new ArrayList<>(DFAAlgorithms.productDFADifference(firstDFA, secondDFA).acceptsUntilLength(maxWordSize));
			Collections.sort(wordsDiff12);
			wordsOf1NotIn2 = Optional.of(wordsDiff12);
		}
		return Collections.unmodifiableList(wordsOf1NotIn2.get());
	}

	public List<String> getWordsOf2NotIn1(){
		if(wordsOf2NotIn1.isEmpty()) {
			List<String> wordsDiff21 = new ArrayList<>(DFAAlgorithms.productDFADifference(secondDFA, firstDFA).acceptsUntilLength(maxWordSize));
			Collections.sort(wordsDiff21);
			wordsOf2NotIn1 = Optional.of(wordsDiff21);
		}
		return Collections.unmodifiableList(wordsOf2NotIn1.get());
	}
	
	public int getMaxWordSize() {
		return maxWordSize;
	}
}
