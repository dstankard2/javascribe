package net.sf.javascribe.langsupport.java;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.langsupport.java.jsom.JsomJavaCode;
import net.sf.javascribe.langsupport.java.jsom.JsomUtils;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5Type;

public class Java5FloatType extends JavaVariableTypeBase implements Java5Type {

	public Java5FloatType() {
		super("float",null,"Float");
	}

	@Override
	public Java5CodeSnippet instantiate(String varName, String value) {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		JsomUtils.merge(ret, instantiate(varName,value,null));
		return ret;
	}

	@Override
	public Java5CodeSnippet declare(String varName) {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		
		ret.append("Float "+varName+" = null;\n");
		return ret;
	}

	@Override
	public JavaCode instantiate(String name, String value,CodeExecutionContext execCtx) {
		return new JsomJavaCode(instantiate(name, value));
	}

	@Override
	public JavaCode declare(String name, CodeExecutionContext execCtx) {
		return new JsomJavaCode(declare(name));
	}

}

