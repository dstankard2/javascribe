package net.sf.javascribe.patterns.view.impl;

public class JaEvalResult {

	private JaEvalString remaining = null;
	private String errorMessage = null;
	private StringBuilder result = new StringBuilder();

	public static JaEvalResult newInstance(String code) {
		return new JaEvalResult(code,0);
	}
	protected JaEvalResult(String code,int place) {
		this.remaining = new JaEvalString(code,place);
	}
	
	public JaEvalResult createNew() {
		JaEvalResult ret = new JaEvalResult(remaining.getCode(),0);
		return ret;
	}
	
	// Other result is appended to this one, the remaining for this one is set to other's remaining
	public void merge(JaEvalResult other,boolean appendResult) {
		if (appendResult)
			result.append(other.result.toString());
		if (errorMessage==null) {
			errorMessage = other.getErrorMessage();
		}
		remaining = other.remaining.copy();
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		if (errorMessage!=null) {
			result = null;
		}
		this.errorMessage = errorMessage;
	}
	public StringBuilder getResult() {
		return result;
	}
	public void setResult(StringBuilder result) {
		this.result = result;
	}
	public JaEvalString getRemaining() {
		return remaining;
	}

}

