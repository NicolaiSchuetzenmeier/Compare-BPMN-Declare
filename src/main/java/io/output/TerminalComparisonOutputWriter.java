package io.output;

import comparison.Comparison;
import io.exceptions.ModelParserException;
import io.interfaces.ComparisonOutputWriter;

public class TerminalComparisonOutputWriter implements ComparisonOutputWriter {

	@Override
	public void handleOutput(Comparison output) {
		System.out.println("Two deterministic finite automata have been created for the given models!");
		System.out.println();
		isEquivalent(output);
	}
	
	private void isEquivalent(Comparison output) {
		System.out.println("The test for equivalence of the two DFAs returned: " +  output.areEquivalent());
		System.out.println();
		if(!output.areEquivalent())
			testSubset(output);
	}

	private void testSubset(Comparison output) {
		System.out.print("Is L(DFA1) subset of L(DFA2): ");		
		if(output.subset1Of2TestResult().isEmpty()) {
			System.out.println("Yes!");
			return;
		}
		System.out.println("No! --> " + output.subset1Of2TestResult().get() + " is in L(DFA1) and not in L(DFA2)");
		
		System.out.print("Is L(DFA2) subset of L(DFA1): ");		
		if(output.subset2Of1TestResult().isEmpty()) {
			System.out.println("Yes!");
			return;
		}
		System.out.println("No! --> " + output.subset2Of1TestResult().get() + " is in L(DFA2) and not in L(DFA1)");
		System.out.println();
		getRegexes(output);
	}
	
	private void getRegexes(Comparison output) {
		System.out.println("An Equivalent regular expression for DFA1 is: " + output.getFirstRegex());
		System.out.println("An Equivalent regular expression for DFA2 is: " + output.getSecondRegex());
		System.out.println();
		getWords(output);
	}
	
	private void getWords(Comparison output) {
		System.out.println("All words in L(DFA1) of maximum length " + output.getMaxWordSize() + " are:");
		System.out.println(output.getWordsOf1());
		System.out.println("All words in L(DFA2) of maximum length " + output.getMaxWordSize() + " are:");
		System.out.println(output.getWordsOf2());
		System.out.println();
		System.out.println("All words in L(DFA1) - L(DFA2) of maximum length " + output.getMaxWordSize() + " are:");
		System.out.println(output.getWordsOf1NotIn2());
		System.out.println("All words in L(DFA2) - L(DFA1) of maximum length " + output.getMaxWordSize() + " are:");
		System.out.println(output.getWordsOf2NotIn1());
		System.out.println();
	}

	@Override
	public void handleCreationException(ModelParserException exception) {
		//TODO
		System.out.println("Error during construction of the DFAs! --> " + exception.getMessage());
	}

	@Override
	public void handleFileNotFoundException() {
		// TODO Auto-generated method stub
		System.out.println("Files not found!");		
	}

	@Override
	public void handleDFAOutput(Comparison output) {
		System.out.println("The first generated DFA:");
		System.out.println(output.getFirstDFA().toString());
		System.out.println();

		System.out.println("The second generated DFA:");
		System.out.println(output.getSecondDFA().toString());
		System.out.println();		
	}
}
