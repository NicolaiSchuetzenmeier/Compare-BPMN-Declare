package io.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import data.automaton.definitions.DFA;
import data.automaton.definitions.State;
import data.automaton.definitions.Symbol;
import data.math.definitions.BinaryTuple;
import data.math.definitions.TransitionFunction;

/**
 * This class allows to store DFAs as a json files.
 * Example for a valid output json Object:
 * {
 *	"States": ["0", "1", "2", "3", "4"],
 *	"Alphabet": ["a", "b", "c", "d", "e"],
 *	"TransitionFunction": [["0", "a", "1"], ["0", "b", "2"], ["1", "a", "3"], ["2", "a", "3"], ["4", "d", "4"]],
 *	"StartState": "0",
 *	"AcceptingStates": ["3"]
 *	}
 *
 */
public class JsonDFAWriter {
	
	private JsonDFAWriter() {}

	public static void createJSONFileForDFA(DFA dfa, File file) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		try {	    
			writeDFA(writer, dfa);
		} catch (IOException e) {
			writer.close();
			throw e;
		}
		writer.close();
	    
	}

	private static void writeDFA(BufferedWriter writer,DFA dfa) throws IOException{
		writer.write("{" + System.lineSeparator());
		
		writer.write("\"" + JsonDFAElement.STATES.getDeclaration() + "\": ");
		writeStateArray(writer,dfa.getStates());
		writer.write("," + System.lineSeparator());
		
		writer.write("\"" + JsonDFAElement.ALPHABET.getDeclaration() + "\": ");
		writeSymbolArray(writer,dfa.getAlphabet().getSymbols());
		writer.write("," + System.lineSeparator());
	
		writer.write("\"" + JsonDFAElement.TRANSFUNCTION.getDeclaration() + "\": ");
		writeTransitionFunction(writer,dfa.getTransitionFunction());
		writer.write("," + System.lineSeparator());
	
		writer.write("\"" + JsonDFAElement.STARTSTATE.getDeclaration() + "\": ");
		writer.write("\"" + dfa.getStartState().getName() + "\"");
		writer.write("," + System.lineSeparator());
	
		writer.write("\"" + JsonDFAElement.ACCEPTINGSTATES.getDeclaration() + "\": ");
		writeStateArray(writer,dfa.getAcceptingStates());
		writer.write(System.lineSeparator());
		
		writer.write("}");
	}
	
	private static void writeStateArray(BufferedWriter writer, Set<State> states) throws IOException{
		writer.write("[");
		
		int i = 0;
		for(State state: states) {
			writer.write("\"" + state.getName() + "\"");
			if(i < states.size() - 1)
				writer.write(", ");				
			i++;
		}
		writer.write("]");
	}

	private static void writeTransitionFunction(BufferedWriter writer, TransitionFunction function) throws IOException{
		writer.write("[");
		
		int i = 0;
		for(BinaryTuple<BinaryTuple<State,Symbol>,State> tuple: function.getTuples()) {
			
			writer.write("[\"" + tuple.getFirst().getFirst().getName() 
					+ "\", \"" + tuple.getFirst().getSecond().getChar()
					+ "\", \"" + tuple.getSecond().getName() + "\"]");
			
			if(i < function.getTuples().size() - 1)
				writer.write(", ");				
			i++;
		}
		writer.write("]");				
	}
	
	private static void writeSymbolArray(BufferedWriter writer, Set<Symbol> symbols) throws IOException{
		writer.write("[");
		
		int i = 0;
		for(Symbol symbol: symbols) {
			writer.write("\"" + symbol.getChar() + "\"");
			if(i < symbols.size() - 1)
				writer.write(", ");				
			i++;
		}
		writer.write("]");		
	}

}
