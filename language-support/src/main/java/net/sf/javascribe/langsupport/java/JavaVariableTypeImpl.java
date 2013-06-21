package net.sf.javascribe.langsupport.java;

import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.java.jsom.JsomJavaCode;
import net.sf.jsom.java5.Java5CodeSnippet;

public class JavaVariableTypeImpl implements JavaVariableType {
	String im = null;
	String className = null;
	String name = null;
	
	public JavaVariableTypeImpl(String name,String im,String className) {
		this.name = name;
		this.className = className;
		this.im = im;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Code instantiate(String name, String value,
			CodeExecutionContext execCtx) throws JavascribeException {
		JavaCodeImpl ret = new JavaCodeImpl();
		ret.addImport(im);
		ret.appendCodeText(name+" = new "+className+"();\n");
		return ret;
	}

	@Override
	public Code declare(String name, CodeExecutionContext execCtx)
			throws JavascribeException {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		ret.addImport(im);
		ret.append(className+' '+name+" = null;\n");
		return new JsomJavaCode(ret);
	}

	@Override
	public String getImport() {
		return im;
	}

	@Override
	public String getClassName() {
		return className;
	}

}

