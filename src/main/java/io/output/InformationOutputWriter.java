package io.output;

import java.io.File;
import java.io.IOException;

import comparison.Comparison;
import io.parser.JsonDFAWriter;

public class InformationOutputWriter{

	private InformationOutputWriter() {}
	
	public static void handleOutput(Comparison output, File dir) throws IOException {
		File file1 = new File(dir.getPath() + "/DFA1");
		File file2 = new File(dir.getPath() + "/DFA2");
		
		JsonDFAWriter.createJSONFileForDFA(output.getFirstDFA(), file1);
		JsonDFAWriter.createJSONFileForDFA(output.getFirstDFA(), file2);		
	}
}
