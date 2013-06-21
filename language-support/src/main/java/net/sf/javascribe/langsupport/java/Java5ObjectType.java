package net.sf.javascribe.langsupport.java;

import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.langsupport.java.jsom.JsomJavaCode;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5Type;

public class Java5ObjectType implements Java5Type,JavaVariableType {

	@Override
	public Code instantiate(String name, String value,
			CodeExecutionContext execCtx) {
		return new JsomJavaCode(instantiate(name,value));
	}

	@Override
	public Code declare(String name, CodeExecutionContext execCtx) {
		return new JsomJavaCode(declare(name));
	}

	@Override
	public String getName() {
		return "object";
	}

	@Override
	public Java5CodeSnippet instantiate(String varName, String value) {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		ret.append(varName+" = new Object();\n");
		return ret;
	}

	@Override
	public Java5CodeSnippet declare(String varName) {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		ret.append("Object "+varName+" = null;\n");
		return ret;
	}

	@Override
	public String getClassName() {
		return "Object";
	}

	@Override
	public String getImport() {
		return null;
	}

}
