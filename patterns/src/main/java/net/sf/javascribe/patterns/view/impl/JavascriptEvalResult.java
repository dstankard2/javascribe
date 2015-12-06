package net.sf.javascribe.patterns.view.impl;

public class JavascriptEvalResult {
	private StringBuilder result = new StringBuilder();
	private String errorMessage = null;
	private JavascriptEvalString remaining = null;
	
	private JavascriptEvalResult(String code) {
		remaining = new JavascriptEvalString(code);
	}
	
	public static JavascriptEvalResult newInstance(String code) {
		JavascriptEvalResult ret = new JavascriptEvalResult(code);
		
		return ret;
	}
	
	public JavascriptEvalResult createNew() {
		return new JavascriptEvalResult(remaining.toString());
	}

	public JavascriptEvalString getRemaining() {
		return remaining;
	}

	public StringBuilder getResult() {
		return result;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public void merge(JavascriptEvalResult other,boolean appendResult) {
		if (appendResult) {
			result.append(other.getResult().toString());
		}
		if (errorMessage==null)
			errorMessage = other.getErrorMessage();
		remaining = other.remaining.copy();
	}

}

