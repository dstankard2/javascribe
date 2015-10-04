package net.sf.javascribe.patterns.view.impl;

public class JaEvalResult {

	private JaEvalString remaining = null;
	private String errorMessage = null;
	private StringBuilder result = new StringBuilder();
	private boolean exprOnly = false;

	public static JaEvalResult newInstance(String code,boolean exprOnly) {
		return new JaEvalResult(code,exprOnly,0);
	}
	protected JaEvalResult(String code,boolean exprOnly,int place) {
		this.remaining = new JaEvalString(code,place);
		this.exprOnly = exprOnly;
	}
	
	public JaEvalResult createNew(boolean exprOnly) {
		JaEvalResult ret = new JaEvalResult(remaining.getCode(),exprOnly,0);
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
	public boolean getExprOnly() {
		return exprOnly;
	}
	public JaEvalString getRemaining() {
		return remaining;
	}

}

