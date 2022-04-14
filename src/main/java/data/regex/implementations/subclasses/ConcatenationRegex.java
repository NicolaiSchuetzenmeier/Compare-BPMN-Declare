package data.regex.implementations.subclasses;

import data.algorithms.util.SetOperations;
import data.regex.definitions.Regex;

public class ConcatenationRegex extends Regex {
	
	private Regex regex1;
	private Regex regex2;
	
	public ConcatenationRegex(Regex regex1, Regex regex2){
		alphabet = SetOperations.unionAlphabet(regex1.getAlphabet(), regex2.getAlphabet());
		this.regex1 = regex1;
		this.regex2 = regex2;
	}

	@Override
	public String getRegexString() {
		String sregex1 = regex1.getRegexString();
		String sregex2 = regex2.getRegexString();
		
		if(regex1 instanceof AlternationRegex)
			sregex1 = "(" + sregex1 + ")";
		if(regex2 instanceof AlternationRegex)
			sregex2 = "(" + sregex2 + ")";
		
		return sregex1 + sregex2;
	}
	
}
