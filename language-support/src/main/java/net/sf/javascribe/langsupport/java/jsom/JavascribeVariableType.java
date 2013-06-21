package net.sf.javascribe.langsupport.java.jsom;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaVariableType;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.CodeSnippet;
import net.sf.jsom.java5.Java5Type;

public class JavascribeVariableType implements Java5Type {
	JavaVariableType type = null;
	
	public JavascribeVariableType(JavaVariableType type) {
		this.type = type;
	}
	
	@Override
	public String getName() {
		return type.getName();
	}

	@Override
	public CodeSnippet instantiate(String varName, String value)
			throws CodeGenerationException {
		try {
			return new JavascribeJavaCodeSnippet((JsomJavaCode)type.instantiate(varName, value, null));
		} catch(JavascribeException e) {
			throw new CodeGenerationException("Couldn't instantiate",e);
		}
	}

	@Override
	public CodeSnippet declare(String varName) throws CodeGenerationException {
		try {
			return new JavascribeJavaCodeSnippet((JsomJavaCode)type.declare(varName, null));
		} catch(JavascribeException e) {
			throw new CodeGenerationException("Couldn't declare",e);
		}
	}

	@Override
	public String getImport() {
		return type.getImport();
	}

	@Override
	public String getClassName() {
		return type.getClassName();
	}

}
