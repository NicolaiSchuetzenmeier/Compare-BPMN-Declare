package io.interfaces;

import comparison.Comparison;
import io.exceptions.ModelParserException;

public interface ComparisonOutputWriter {
	
	public void handleOutput(Comparison output);
	public void handleDFAOutput(Comparison output);
	
	public void handleCreationException(ModelParserException exception);
	public void handleFileNotFoundException();
}
