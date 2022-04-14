package main;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import constraints.definitions.ModelDefinition;
import constraints.generation.ConstraintDFAGenerator;
import data.algorithms.DFAAlgorithms;
import data.algorithms.util.PDFALanguageType;
import data.automaton.definitions.*;
import data.automaton.implementations.*;
import data.exceptions.DFASEqualException;
import data.math.definitions.*;
import data.math.implementations.*;
import data.regex.definitions.Regex;
import io.exceptions.JSONParserException;
import io.exceptions.ModelParserException;
import io.parser.JsonDFAParser;
import io.parser.JsonDFAWriter;
import io.parser.ModelParser;

public class TestMain {

	public static void main(String[] args) throws IOException, JsonIOException, JsonSyntaxException, JSONParserException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ModelParserException {
	
		//mainOne();
		//mainTwo();
		//mainThree(); 
		//mainFour();
		//testMinimizedProductDFA();
		//printAllTestAutomatons();
		//mainFive();
		//testRegexDFA();
		//testConstraintDFAS();
		testModels();
	}
	
	public static void mainOne() throws IOException, JsonIOException, JsonSyntaxException, JSONParserException {
		Set<State> states = new HashSet<>();
		for(int i = 0; i < 5; i++)
			states.add(AutomatonFactory.createState("" + i));
		
		Set<Symbol> symbols = new HashSet<>();
		for(int i = 0; i < 5; i++)
			symbols.add(AutomatonFactory.createSymbol((char) ('a' + i)));
		Alphabet alphabet = AutomatonFactory.createAlphabet(symbols);
		
		State startState = AutomatonFactory.createState("" + 0);
		
		Set<State> acceptingStates = new HashSet<>();
		acceptingStates.add(AutomatonFactory.createState("" + 3));
		
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> tupels = new HashSet<>();
		tupels.add(MathFactory.createBinaryTuple(MathFactory.createBinaryTuple(AutomatonFactory.createState("0"), AutomatonFactory.createSymbol('a')), AutomatonFactory.createState("1")));
		tupels.add(MathFactory.createBinaryTuple(MathFactory.createBinaryTuple(AutomatonFactory.createState("0"), AutomatonFactory.createSymbol('b')), AutomatonFactory.createState("2")));
		tupels.add(MathFactory.createBinaryTuple(MathFactory.createBinaryTuple(AutomatonFactory.createState("2"), AutomatonFactory.createSymbol('a')), AutomatonFactory.createState("3")));
		tupels.add(MathFactory.createBinaryTuple(MathFactory.createBinaryTuple(AutomatonFactory.createState("1"), AutomatonFactory.createSymbol('a')), AutomatonFactory.createState("3")));
		tupels.add(MathFactory.createBinaryTuple(MathFactory.createBinaryTuple(AutomatonFactory.createState("4"), AutomatonFactory.createSymbol('d')), AutomatonFactory.createState("4")));
		TransitionFunction function = MathFactory.createTransitionFunction(tupels); 
		DFA dfa = AutomatonFactory.createDFA(states,alphabet,function,startState,acceptingStates);
		
		System.out.println("Input DFA:");
		System.out.println(dfa.toString());
		System.out.println();
		
		File file = new File("Test.json");
		JsonDFAWriter.createJSONFileForDFA(dfa, file);

		dfa = JsonDFAParser.parseSingleDFA(file);

		System.out.println("Output DFA:");
		System.out.println(dfa.toString());
		System.out.println();
		

		DFA minimized = DFAAlgorithms.minimizeDFA(dfa);
		System.out.println("Minimized DFA:");
		System.out.println(minimized.toString());
		System.out.println();
		

		DFA product = DFAAlgorithms.productDFAIntersect(dfa, minimized);
		System.out.println("Product of minimized and original DFA:");
		System.out.println(product.toString());
		System.out.println();

		DFA productMinimized = DFAAlgorithms.minimizeDFA(product);
		System.out.println("Minimized Product of minimized and original DFA:");
		System.out.println(productMinimized.toString());
		System.out.println();

		File file2 = new File("TestMiniProd.json");
		JsonDFAWriter.createJSONFileForDFA(product, file2);

		
		System.out.println("Minimized Product of the original 15 times with it self (BIG):");		
		List<DFA> dfas = new ArrayList<>(15);
		for(int i = 0; i < 15; i++)
			dfas.add(dfa);
		
		DFA big = DFAAlgorithms.minimizedProductDFA(dfas);
		File bigF = new File("Big.json");
		System.out.println(big.toString());
		JsonDFAWriter.createJSONFileForDFA(big, bigF);

		System.out.println("Is BIG equivalent to the original?");		
		System.out.println(DFAAlgorithms.areEquivalent(dfa, big));
	}
	
	public static void mainTwo() throws JsonIOException, JsonSyntaxException, JSONParserException, IOException {
		DFA dfa1 = JsonDFAParser.parseSingleDFA(new File("in/dfa1.json"));
		DFA dfa2 = JsonDFAParser.parseSingleDFA(new File("in/dfa2.json"));

		System.out.println("DFA1:");	
		System.out.println(dfa1.toString());		
		System.out.println();	
		

		System.out.println("DFA2:");	
		System.out.println(dfa2.toString());		
		System.out.println();	
		
		DFA product = DFAAlgorithms.productDFAIntersect(dfa1, dfa2);
		DFA minimizedProduct = DFAAlgorithms.minimizeDFA(product);
		System.out.println("Product DFA of DFA1 and DFA2");	
		System.out.println(product.toString());		
		System.out.println();	
		
		File productF = new File("out/product.json");
		JsonDFAWriter.createJSONFileForDFA(product, productF);
		
		System.out.println("Minimized Product DFA of DFA1 and DFA2");	
		System.out.println(minimizedProduct.toString());		
		System.out.println();	

		System.out.println("Does DFA1 accept \"abc\"?");	
		System.out.println(dfa1.accepts("abc"));	
		System.out.println("Does DFA2 accept \"abc\"?");	
		System.out.println(dfa2.accepts("abc"));	

		System.out.println("Does DFA1 accept \"aaaa\"?");	
		System.out.println(dfa1.accepts("aaaa"));	
		System.out.println("Does DFA2 accept \"aaaa\"?");	
		System.out.println(dfa2.accepts("aaaa"));	
		
		System.out.println("Does DFA1 accept \"c\"?");	
		System.out.println(dfa1.accepts("c"));	
		System.out.println("Does DFA2 accept \"c\"?");	
		System.out.println(dfa2.accepts("c"));	
		
		System.out.println("Are the two DFAs equivalent?");			
		System.out.println(DFAAlgorithms.areEquivalent(dfa1,dfa2));	

		System.out.println("A word not accepted by DFA2, but by DFA1 is:");			
		try {
			System.out.println(DFAAlgorithms.getWordAcceptedByOneRejectedByTwo(dfa1,dfa2));
		} catch (DFASEqualException e) {}	
		
		System.out.println(DFAAlgorithms.productDFAIntersect(DFAAlgorithms.productDFAIntersect(dfa1,dfa2), DFAAlgorithms.productDFAIntersect(dfa1,dfa2)).toString());
	}
	
	public static void mainThree() throws JsonIOException, JsonSyntaxException, JSONParserException, IOException {
		DFA dfa1 = JsonDFAParser.parseSingleDFA(new File("in/myDFA1.json"));
		DFA dfa2 = JsonDFAParser.parseSingleDFA(new File("in/myDFA2.json"));
		
		DFA product = DFAAlgorithms.productDFA(dfa1, dfa2,PDFALanguageType.SYMMETRICDIFFERENCE);
		
		File productF = new File("out/myProduct.json");
		JsonDFAWriter.createJSONFileForDFA(product, productF);
		
		System.out.println("Product DFA of myDFA1 and myDFA2");	
		System.out.println(product.toString());		
		System.out.println();	
		
		DFA minimizedProduct = DFAAlgorithms.minimizeDFA(product);
		File mProductF = new File("out/myMinimizedProduct.json");
		JsonDFAWriter.createJSONFileForDFA(minimizedProduct, mProductF);

		System.out.println("Minimized Product:");	
		System.out.println(minimizedProduct.toString());		
		System.out.println();	
		
	}
	
	public static void mainFour() throws JsonIOException, JsonSyntaxException, FileNotFoundException, JSONParserException {
		List<DFA> dfas = new LinkedList<>();
		String path = "tests/in/";
		
		dfas.add(JsonDFAParser.parseSingleDFA(new File(path + "testDFA1.json")));
		dfas.add(JsonDFAParser.parseSingleDFA(new File(path + "testDFA2.json")));
		dfas.add(JsonDFAParser.parseSingleDFA(new File(path + "testDFA3.json")));
		dfas.add(JsonDFAParser.parseSingleDFA(new File(path + "testDFA4.json")));
		dfas.add(JsonDFAParser.parseSingleDFA(new File(path + "testDFA5.json")));
		dfas.add(JsonDFAParser.parseSingleDFA(new File(path + "testDFA6.json")));
		dfas.add(JsonDFAParser.parseSingleDFA(new File(path + "testSTH.json")));
		
		for(DFA dfa: dfas) {
			System.out.println("Original:");
			System.out.println(dfa.toString());
			System.out.println("Complete:");
			System.out.println(DFAAlgorithms.getCompleteDFA(dfa).toString());
			System.out.println("Minimized:");
			System.out.println(DFAAlgorithms.minimizeDFA(dfa).toString());
			System.out.println("Minimized Complete:");
			System.out.println(DFAAlgorithms.minimizeDFA(DFAAlgorithms.getCompleteDFA(dfa)).toString());
		}
	}
	
	public static void testMinimizedProductDFA() throws JsonIOException, JsonSyntaxException, FileNotFoundException, JSONParserException {
		List<DFA> dfas = new LinkedList<>();
		String path = "tests/in/";
		
		dfas.add(JsonDFAParser.parseSingleDFA(new File(path + "testDFA1.json")));
		dfas.add(JsonDFAParser.parseSingleDFA(new File(path + "testDFA2.json")));
		dfas.add(JsonDFAParser.parseSingleDFA(new File(path + "testDFA3.json")));
		dfas.add(JsonDFAParser.parseSingleDFA(new File(path + "testDFA4.json")));
		dfas.add(JsonDFAParser.parseSingleDFA(new File(path + "testDFA5.json")));
		dfas.add(JsonDFAParser.parseSingleDFA(new File(path + "testDFA6.json")));
		
		for(PDFALanguageType type: PDFALanguageType.values()) {
			System.out.println(type.name() + ":");
			System.out.println(DFAAlgorithms.minimizedProductDFA(dfas,type).toString());
		}
	}
	
	public static void printAllTestAutomatons() throws JsonIOException, JsonSyntaxException, FileNotFoundException, JSONParserException {
		String[] dirs = new String[] {"tests/in/.","tests/in/complement/","tests/in/complete/","tests/in/dead/","tests/in/equivalence/","tests/in/product/","tests/in/unreachable/"};
		
		FileFilter fileFilter = file -> !file.isDirectory() && file.getName().endsWith(".json");
		
		List<DFA> dfas = new LinkedList<>();
		
		for(String dir: dirs) {
			File file = new File(dir);
			File [] files = file.listFiles(fileFilter);
			for(File dfaFile: files)
				dfas.add(JsonDFAParser.parseSingleDFA(dfaFile));
		}
		
		for(DFA dfa: dfas)
			System.out.println(dfa.toString());
		
		System.out.println(dfas.size() + " DFAs were found!");
	}
	
	public static void mainFive() throws JsonIOException, JsonSyntaxException, FileNotFoundException, JSONParserException {
		DFA dfa = JsonDFAParser.parseSingleDFA(new File("tests/in/testDFA1.json"));
		
		System.out.println(dfa.toString());
		
		System.out.println("Accepts of length == 20:");
		dfa.acceptsOfLength(20).forEach(n->System.out.println(n));
		
	}
	
	public static void testRegexDFA() throws JsonIOException, JsonSyntaxException, FileNotFoundException, JSONParserException {
		List<DFA> dfas = new LinkedList<>();
		String path = "tests/in/";
		
		dfas.add(JsonDFAParser.parseSingleDFA(new File(path + "testDFA1.json")));
		dfas.add(JsonDFAParser.parseSingleDFA(new File(path + "testDFA2.json")));
		dfas.add(JsonDFAParser.parseSingleDFA(new File(path + "testDFA3.json")));
		dfas.add(JsonDFAParser.parseSingleDFA(new File(path + "testDFA4.json")));
		dfas.add(JsonDFAParser.parseSingleDFA(new File(path + "testDFA5.json")));
		dfas.add(JsonDFAParser.parseSingleDFA(new File(path + "testDFA6.json")));
		
		for(DFA dfa: dfas) {
			System.out.println("DFA:");
			System.out.println(dfa.toString());
			System.out.println("RA:");
			System.out.println(Regex.convertToRegex(DFAAlgorithms.minimizeDFA(dfa)).getRegexString());
		}
	}
	
	public static void testConstraintDFAS() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Symbol a = AutomatonFactory.createSymbol('a');
		Symbol b = AutomatonFactory.createSymbol('b');
		Symbol c = AutomatonFactory.createSymbol('c');
		Set<Symbol> alphabet = new HashSet<>(Arrays.asList(a,b,c));
		
		for(Method method: ConstraintDFAGenerator.class.getMethods()) {
			if(!isObjectMethod(method) && !method.getName().equals("generateDFA")) {
				System.out.println(method.getName());
				if(method.getParameterCount() == 2) {
					System.out.println(method.invoke(null,alphabet,a));
				} else if(method.getParameterCount() == 3) {
					if(method.getParameters()[2].getType().isPrimitive())
						System.out.println(method.invoke(null,alphabet, a, 6));
					else
						System.out.println(method.invoke(null,alphabet,a,b));
				}
				System.out.println();
			}
		}
	}
	
	private static boolean isObjectMethod(Method method) {
		for(Method methodO: Object.class.getMethods())
			if(methodO.getName().equals(method.getName()))
				return true;
		return false;
	}
	
	public static void testModels() throws FileNotFoundException, ModelParserException {
		ModelDefinition model1 = ModelParser.parseModelDefinition(new File("examples/models/test1.txt"));
		//ModelDefinition model2 = ModelParser.parseModelDefinition(new File("examples/models/test2.txt"));
		ModelDefinition model3 = ModelParser.parseModelDefinition(new File("examples/models/test3.txt"));
		//ModelDefinition model4 = ModelParser.parseModelDefinition(new File("examples/models/test4.txt"));
		
		System.out.println(ConstraintDFAGenerator.generateDFA(model1).accepts("aeecbf"));
		System.out.println(ConstraintDFAGenerator.generateDFA(model3).accepts("aeecbf"));
	}
}
