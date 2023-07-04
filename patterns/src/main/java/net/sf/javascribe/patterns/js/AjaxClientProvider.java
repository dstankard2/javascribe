package net.sf.javascribe.patterns.js;

import net.sf.javascribe.langsupport.javascript.JavascriptCode;

public interface AjaxClientProvider {

	String getName();
	
	/**
	 * An Ajax Client provider is a code generator that produces code to invoke a web service via AJAX.  There is no 
	 * code execution context but two variables are provided: optionsVar and resultVar.  The resultVar is a variable 
	 * which must be set by the code generator, and it should be a promise that will be returned to the caller of the 
	 * generated code.  optionsVar is an object which contains the following:
	 * @param optionsVar 
	 * @param resultVar Variable that should be populated with a promise to return.  It is already declared in the code execution context.
	 * @return Code to perform Ajax call.
	 */
	JavascriptCode getAjaxCode(String optionsVar, String resultVar);

}
