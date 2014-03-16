package net.sf.javascribe.langsupport.java;

import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.types.ListType;
import net.sf.javascribe.langsupport.java.jsom.JsomJavaCode;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.CodeSnippet;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5Type;

public class Java5ListType implements ListType,Java5Type,JavaVariableType {

	@Override
	public Code appendToList(String listVarName, String value,
			CodeExecutionContext execCtx) throws JavascribeException {
		JavaCodeImpl ret = new JavaCodeImpl();
		ret.appendCodeText(listVarName+".add("+value+");\n");
		return ret;
	}

	@Override
	public String getCodeToRetrieveAttribute(String varName, String attribName,
			String targetType,CodeExecutionContext execCtx) throws IllegalArgumentException,
			JavascribeException {
		if (!attribName.equals("size")) {
			throw new JavascribeException("Java List type only supports size attribute.");
		}
		return varName+".size()";
	}

	@Override
	public String getCodeToSetAttribute(String varName, String attribName,
			String evaluatedValue,CodeExecutionContext execCtx) throws JavascribeException {
		// TODO Auto-generated method stub
		throw new JavascribeException("Java List type does not support set attribute.");
	}

	@Override
	public String getAttributeType(String attrib) {
		if (attrib.equals("size")) return "integer";
		return null;
	}

	@Override
	public String getClassName() {
		return "List";
	}

	@Override
	public String getImport() {
		return "java.util.List";
	}

	@Override
	public String getName() {
		return "list";
	}

	@Override
	public JavaCode declare(String varName,CodeExecutionContext execCtx) throws JavascribeException {
		throw new JavascribeException("Java List type must be declared with an element type.");
	}

	@Override
	public JavaCode declare(String varName, String elementType,CodeExecutionContext execCtx) throws JavascribeException {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		Java5Type elt = (Java5Type)execCtx.getType(elementType);
		
		if (elt==null) {
			throw new JavascribeException("Could not find list element type '"+elementType+"'");
		}
		
		ret.addImport(getImport());
		ret.addImport(elt.getImport());
		ret.append("List<"+elt.getClassName()+"> "+varName+" = null;\n");
		
		return new JsomJavaCode(ret);
	}

	@Override
	public JavaCode instantiate(String varName, String elementType,CodeExecutionContext execCtx) {
		JavaCodeImpl ret = new JavaCodeImpl();
		JavaVariableType elt = (JavaVariableType)execCtx.getType(elementType);

		ret.addImport(getImport());
		ret.addImport(elt.getImport());
		ret.appendCodeText(varName+" = new java.util.ArrayList<"+elt.getClassName()+">();\n");
		
		return ret;
	}

	@Override
	public CodeSnippet instantiate(String varName, String value)
			throws CodeGenerationException {
		throw new CodeGenerationException("Java List type must be instantiated with an element type.");
	}

	@Override
	public CodeSnippet declare(String varName) throws CodeGenerationException {
		throw new CodeGenerationException("Java List type must be declared with an element type.");
	}

}
