package io.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gson.*;

import data.automaton.definitions.Alphabet;
import data.automaton.definitions.DFA;
import data.automaton.definitions.State;
import data.automaton.definitions.Symbol;
import data.automaton.implementations.AutomatonFactory;
import data.math.definitions.TransitionFunction;
import data.math.definitions.BinaryTuple;
import data.math.implementations.MathFactory;
import io.exceptions.JSONParserException;

/**
 * This class allows to read DFAs from json files.
 * Example for a valid input json Object:
 * {
 *	"States": ["0", "1", "2", "3", "4"],
 *	"Alphabet": ["a", "b", "c", "d", "e"],
 *	"TransitionFunction": [["0", "a", "1"], ["0", "b", "2"], ["1", "a", "3"], ["2", "a", "3"], ["4", "d", "4"]],
 *	"StartState": "0",
 *	"AcceptingStates": ["3"]
 *	}
 *
 */
public class JsonDFAParser {
	
	private JsonDFAParser() {}
	
	
	public static DFA parseSingleDFA(File file) throws JSONParserException, JsonIOException, JsonSyntaxException, FileNotFoundException{ 
		JsonElement element = JsonParser.parseReader(new FileReader(file));
		
		if(element.isJsonObject()) {
			return parseJsonObject(element.getAsJsonObject());
		} else
			throw new JSONParserException("This file does not define an automaton!");
	}
	
	private static DFA parseJsonObject(JsonObject object) throws JSONParserException {
		return AutomatonFactory.createDFA(
				getStates(object), 
				getAlphabet(object), 
				getTransitionFunction(object), 
				getStartState(object), 
				getAcceptingStates(object));
		
	}
	
	private static Set<State> getStates(JsonObject object) throws JSONParserException{
		Set<State> states = new HashSet<>();
		
		for(String element: getJsonArray(JsonDFAElement.STATES.getDeclaration(),object))
			states.add(AutomatonFactory.createState(element));
		
		return states;
	}
	
	private static Set<State> getAcceptingStates(JsonObject object) throws JSONParserException{
		Set<State> aStates = new HashSet<>();
		
		for(String element: getJsonArray(JsonDFAElement.ACCEPTINGSTATES.getDeclaration(),object))
			aStates.add(AutomatonFactory.createState(element));
		
		return aStates;
	}
	
	private static Alphabet getAlphabet(JsonObject object) throws JSONParserException{
		Set<Symbol> symbols = new HashSet<>();
		
		for(String element: getJsonArray(JsonDFAElement.ALPHABET.getDeclaration(),object)) {
			if(element.length() != 1)
				throw new JSONParserException("A symbol string length is greater or less than 1");
			symbols.add(AutomatonFactory.createSymbol(element.charAt(0)));
		}
		
		return AutomatonFactory.createAlphabet(symbols);
	}
	
	private static State getStartState(JsonObject object) throws JSONParserException{
		
		if(object.get(JsonDFAElement.STARTSTATE.getDeclaration()).isJsonPrimitive()) 
			return AutomatonFactory.createState(object.get(JsonDFAElement.STARTSTATE.getDeclaration()).getAsString());
		else
			throw new JSONParserException("start state is notPrimitive");
		
	}
	
	private static TransitionFunction getTransitionFunction(JsonObject object) throws JSONParserException{
		Set<BinaryTuple<BinaryTuple<State,Symbol>,State>> tuples = new HashSet<>();
		
		for(JsonArray array: getJsonArrayList(JsonDFAElement.TRANSFUNCTION.getDeclaration(),object)) {
			List<String> tmp = new LinkedList<>();
			
			for(JsonElement element: array) {
				if(element.isJsonPrimitive())
					tmp.add(element.getAsString());
				else
					throw new JSONParserException("notPrimitive");				
			}
			
			if(tmp.size() != 3)
				throw new JSONParserException("tuple element contains too many elements");
			if(tmp.get(1).length() != 1)
				throw new JSONParserException("symbol string is too long!");			
			
			State start = AutomatonFactory.createState(tmp.get(0));
			Symbol symbol = AutomatonFactory.createSymbol(tmp.get(1).charAt(0));
			State end = AutomatonFactory.createState(tmp.get(2));
			
			tuples.add(MathFactory.createTransition(start, symbol, end));			
		}
		
		return MathFactory.createTransitionFunction(tuples);
	}
	
	private static List<JsonArray> getJsonArrayList(String name, JsonObject toParse) throws JSONParserException{		
		List<JsonArray> list = new LinkedList<>();
		
		if(toParse.get(name).isJsonArray()) {
			JsonArray jArray = toParse.get(name).getAsJsonArray();
			for(JsonElement element: jArray) {
				if(element.isJsonArray())
					list.add(element.getAsJsonArray());
				else
					throw new JSONParserException("tuple is not a json Array");
			}
		} else 
			throw new JSONParserException("notArray");
		
		return list;
	}
	
	
	private static List<String> getJsonArray(String name, JsonObject toParse) throws JSONParserException{		
		List<String> list = new LinkedList<>();
		
		if(toParse.get(name).isJsonArray()) {
			JsonArray jArray = toParse.get(name).getAsJsonArray();
			for(JsonElement element: jArray) {
				if(element.isJsonPrimitive())
					list.add(element.getAsString());
				else
					throw new JSONParserException("notPrimitive");
			}
		} else 
			throw new JSONParserException("notArray");
		
		return list;
	}

}
