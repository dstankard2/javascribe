package net.sf.javascribe.langsupport.java;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.types.ListType;
import net.sf.javascribe.langsupport.java.jsom.JsomJavaCode;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.CodeSnippet;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5Type;

public class Java5ListType extends JavaVariableTypeBase implements ListType,Java5Type {

	public Java5ListType() {
		super("list","java.util","List");
	}
	
	@Override
	public Code appendToList(String listVarName, String value,
			CodeExecutionContext execCtx) throws JavascribeException {
		JavaCodeImpl ret = new JavaCodeImpl();
		ret.appendCodeText(listVarName+".add("+value+");\n");
		return ret;
	}

	public List<String> getAttributeNames() {
		ArrayList<String> ret = new ArrayList<String>();
		ret.add("size");
		
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
		throw new JavascribeException("Java List type does not support set attribute.");
	}

	@Override
	public String getAttributeType(String attrib) {
		if (attrib.equals("size")) return "integer";
		return null;
	}

	@Override
	public JavaCode declare(String varName,CodeExecutionContext execCtx) {
		throw new RuntimeException("Java List type must be declared with an element type.");
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
		if (varName!=null) {
			ret.appendCodeText(varName+" = ");
		}
		ret.appendCodeText("new java.util.ArrayList<"+elt.getClassName()+">();\n");
		
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
