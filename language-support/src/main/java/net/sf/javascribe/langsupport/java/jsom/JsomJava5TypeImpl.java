package net.sf.javascribe.langsupport.java.jsom;

import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaVariableType;
import net.sf.jsom.java5.Java5CodeSnippet;

public class JsomJava5TypeImpl implements JavaVariableType {
	String im = null;
	String className = null;
	String name = null;
	
	public JsomJava5TypeImpl(String name,String _import,String className) {
		this.name = name;
		this.im = _import;
		this.className = className;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Code instantiate(String name, String value,
			CodeExecutionContext execCtx) throws JavascribeException {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		ret.addImport(im);
		ret.append(name+" = new "+className+"();\n");
		return new JsomJavaCode(ret);
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

