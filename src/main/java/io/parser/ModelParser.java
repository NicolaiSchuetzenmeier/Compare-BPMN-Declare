package io.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

import constraints.definitions.Constraint;
import constraints.definitions.ConstraintType;
import constraints.definitions.ModelDefinition;
import constraints.exceptions.ConstraintGenerationException;
import constraints.implementations.ConstraintFactory;
import data.automaton.definitions.Symbol;
import data.automaton.implementations.AutomatonFactory;
import io.exceptions.ModelParserException;

public class ModelParser {

	public static final String INVALID_ALPHABET = "Invalid Alphabet!";
	public static final String INVALID_CONSTRAINT = "Invalid Constraint!";
	public static final String INVALID_SYMBOL = "Constraint uses symbol not defined in the alphabet!";
	public static final String NO_CONSTRAINTS = "No Constraints!";
	
	private static final Pattern alphabetPattern = Pattern.compile("\\w(,\\w)*");
	private static final String PL = "\\(\\w(,\\w)*(,[1-9][0-9]*)?\\)"; 
	private static final Pattern constraintPattern = Pattern.compile("\\w*" + PL);
	
	private ModelParser() {}
	
	public static ModelDefinition parseModelDefinition(File file) throws FileNotFoundException, ModelParserException {
		
		List<String> items = getAllItems(getInputWithoutWhitespaces(file));
		
		Set<Symbol> alphabet = getAlphabet(items.remove(0));		
		Collection<Constraint> constraints = getConstraints(items);
		checkConstraintValidity(alphabet,constraints);
		return ConstraintFactory.createModelDefinition(alphabet, constraints);
	}
	
	private static void checkConstraintValidity(Set<Symbol> alphabet, Collection<Constraint> constraints) throws ModelParserException {
		if(constraints.isEmpty())
			throw new ModelParserException(NO_CONSTRAINTS);
		for(Constraint constraint: constraints)
			if(!alphabet.containsAll(constraint.getSymbolParameters()))
				throw new ModelParserException(INVALID_SYMBOL);				
	}
	
	private static String getInputWithoutWhitespaces(File file) throws FileNotFoundException{
		Scanner scanner = new Scanner(file);
		scanner.useDelimiter("\\s+");
		StringBuilder input = new StringBuilder();
		while(scanner.hasNext())
			input.append(scanner.next());
		scanner.close();
		return input.toString();
	}
	
	private static List<String> getAllItems(String input) {
		Scanner scanner = new Scanner(input);
		scanner.useDelimiter(";");
		
		List<String> items = new LinkedList<>();

		while(scanner.hasNext())
			items.add(scanner.next());		
		
		scanner.close();
		return items;
	}
	
	private static Set<Symbol> getAlphabet(String input) throws ModelParserException{
		if(!alphabetPattern.matcher(input).matches())
			throw new ModelParserException(INVALID_ALPHABET);
		
		Scanner scanner = new Scanner(input);
		scanner.useDelimiter(",");

		Set<Symbol> symbols = new HashSet<>();
		while(scanner.hasNext())
			symbols.add(AutomatonFactory.createSymbol(scanner.next().charAt(0)));
			
		scanner.close();
		return symbols;
	}
	
	private static Collection<Constraint> getConstraints(List<String> items) throws ModelParserException{
		Collection<Constraint> constraints = new LinkedList<>();
		for(String item: items) {
			constraints.add(getConstraint(item));
		}
		return constraints;
	}
	
	private static Constraint getConstraint(String item) throws ModelParserException{
		if(!constraintPattern.matcher(item).matches())
			throw new ModelParserException(INVALID_CONSTRAINT);
		
		Scanner scanner = new Scanner(item);
		scanner.useDelimiter("\\(|\\)|,");
		List<String> substrings = new LinkedList<>();
		
		while(scanner.hasNext())
			substrings.add(scanner.next());

		scanner.close();
		try {
			ConstraintType type = ConstraintType.getConstraintType(substrings.remove(0));
			List<Symbol> parameters = new LinkedList<>();

			if(type.isUnary()) {
				if(substrings.size() != 1)
					throw new ModelParserException(INVALID_CONSTRAINT);
				else parameters.add(AutomatonFactory.createSymbol(substrings.get(0).charAt(0)));
			}else if(type.isBinary()) {
				if(substrings.size() != 2)
					throw new ModelParserException(INVALID_CONSTRAINT);
				else {
					parameters.add(AutomatonFactory.createSymbol(substrings.get(0).charAt(0)));
					if(substrings.get(1).length() != 1)
						throw new ModelParserException(INVALID_CONSTRAINT);
					parameters.add(AutomatonFactory.createSymbol(substrings.get(1).charAt(0)));
				}
			}else if(type.isCount()) {
				if(substrings.size() != 2)
					throw new ModelParserException(INVALID_CONSTRAINT);
				else {
					parameters.add(AutomatonFactory.createSymbol(substrings.get(0).charAt(0)));
					return ConstraintFactory.createCountConstraint(type, parameters, Integer.parseInt(substrings.get(1)));
				}
			}
					
			return ConstraintFactory.createConstraint(type, parameters);
		} catch (ConstraintGenerationException | NumberFormatException e) {
			throw new ModelParserException(INVALID_CONSTRAINT);
		}
	}
}
