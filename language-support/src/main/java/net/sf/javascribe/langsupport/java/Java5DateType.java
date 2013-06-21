package net.sf.javascribe.langsupport.java;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.types.DateType;
import net.sf.javascribe.langsupport.java.jsom.JsomJavaCode;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5Type;

public class Java5DateType implements Java5Type,JavaVariableType,DateType {

	@Override
	public String getName() {
		return "date";
	}

	@Override
	public Java5CodeSnippet instantiate(String varName, String value) {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		
		ret.append(varName+" = new Date(System.currentTimeMillis());\n");
		
		return ret;
	}

	@Override
	public Java5CodeSnippet declare(String varName) {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		
		ret.addImport(getImport());
		ret.append("Date "+varName+" = null;\n");
		
		return ret;
	}

	@Override
	public String getClassName() {
		return "Date";
	}

	@Override
	public String getImport() {
		return "java.sql.Date";
	}

	@Override
	public JavaCode instantiate(String name, String value,
			CodeExecutionContext execCtx) {
		return new JsomJavaCode(instantiate(name,value));
	}

	@Override
	public JavaCode declare(String name, CodeExecutionContext execCtx)
			throws JavascribeException {
		return new JsomJavaCode(declare(name));
	}

}

