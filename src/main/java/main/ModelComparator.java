package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import comparison.Comparison;
import constraints.definitions.ConstraintType;
import constraints.definitions.ModelDefinition;
import io.exceptions.ModelParserException;
import io.interfaces.ComparisonOutputWriter;
import io.output.TerminalComparisonOutputWriter;
import io.parser.ModelParser;

public class ModelComparator {
	
	private static final int DEFAULT_MAX_LENGTH = 3;
	private static final String DFA_OUTPUT = "--dfa-output";
	private static final String MAX_WORD_LENGTH = "--max-word-length";
	private static final String LIST_CONSTRAINTS = "--list-constraints";
	private static final String HELP = "--help";
	private static boolean dfaOutput = false;
	private static int maxLength = DEFAULT_MAX_LENGTH;
	
	public static void main(String[] args) {
		if(!checkOptions(args)) return;
		ComparisonOutputWriter writer = new TerminalComparisonOutputWriter();
		
		try {
			ModelDefinition model1 = ModelParser.parseModelDefinition(new File(args[args.length - 2]));
			ModelDefinition model2 = ModelParser.parseModelDefinition(new File(args[args.length - 1]));
			
			Comparison comparison = Comparison.compareModels(model1, model2, maxLength);
			if(dfaOutput) writer.handleDFAOutput(comparison);
			writer.handleOutput(comparison);
		} catch(FileNotFoundException e) {
			writer.handleFileNotFoundException();
			printHelpMsg();
		} catch(ModelParserException e) {
			writer.handleCreationException(e);
			printHelpMsg();
		}
	}
	
	private static boolean checkOptions(String [] args) {
		List<String> inputs = new ArrayList<>(Arrays.asList(args));

		if(inputs.contains(HELP)) {
			printHelpMsg();
			return false;
		}

		if(inputs.contains(LIST_CONSTRAINTS)) {
			printValidConstraints();
			return false; 
		}	
		
		if(inputs.size() < 2) {
			printHelpMsg();
			return false;
		}
		
		for(int i = 0; i < inputs.size(); i++) {
			if(inputs.get(i).equals(DFA_OUTPUT))
				dfaOutput = true;
			if(inputs.get(i).equals(MAX_WORD_LENGTH)) {
				try {
					maxLength = Integer.parseInt(inputs.get(i+1));
				}catch(NumberFormatException e) {
					printHelpMsg();
					return false;
				}
				if(maxLength < 1) {
					printHelpMsg();
					return false;
				}
			}	
		}
		return true;
	}
	
	public static void printValidConstraints() {
		for(String string: ConstraintType.getConstraintCalls())
			System.out.println(string);
	}

	public static void printHelpMsg() {
		String msg = "Usage: compare_models [Option] ... [Option] FILE FILE" + System.lineSeparator()
					+ "Compares the two declare models defined in the two input files" + System.lineSeparator() + System.lineSeparator()
					+ "\t "+ DFA_OUTPUT + "\t\t\t also puts out the generated dfas." + System.lineSeparator()
					+ "\t "+ MAX_WORD_LENGTH + " INTEGER \t defines the maximum word length for language outputs. Otherwise a default value will be used. The value has to be positive." + System.lineSeparator()
					+ "\t "+ LIST_CONSTRAINTS + " \t\t lists all possible constraints." + System.lineSeparator()
					+ "\t "+ HELP + " \t\t\t shows help page." + System.lineSeparator()
					+ System.lineSeparator() + System.lineSeparator()
					+ "The input files must have the following syntax:" + System.lineSeparator() + System.lineSeparator()
					+ "Alphabet; Constraint; Constraint; ... ; Constraint;" + System.lineSeparator()
					+ "The Alphabet must have the following syntax:" + System.lineSeparator()
					+ "Symbol,Symbol,...,Symbol" + System.lineSeparator()
					+ "A Constraint must have the following syntax:" + System.lineSeparator()
					+ "constraint(parameter_1,...,parameter_n)" + System.lineSeparator()
					+ System.lineSeparator()
					+ "Example:" + System.lineSeparator()
					+ "a,b,c;" + System.lineSeparator()
					+ "init(a);" + System.lineSeparator()
					+ "existence(a,12);" + System.lineSeparator()
					+ "co_existence(b,c);" + System.lineSeparator()
					+ System.lineSeparator();
		System.out.println(msg);
	}

}
