package data.regex.implementations.subclasses;

import data.regex.definitions.Regex;

public class KleeneStarRegex extends Regex {

	private Regex operand;
	
	public KleeneStarRegex(Regex operand){
		alphabet = operand.getAlphabet();
		this.operand = operand;
	}

	public Regex getOperand() {
		return operand;
	}
	
	@Override
	public String getRegexString() {
		return "(" + operand.getRegexString() + ")*";
	}
	
}
