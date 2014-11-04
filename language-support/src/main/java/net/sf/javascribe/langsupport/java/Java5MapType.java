package net.sf.javascribe.langsupport.java;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.types.MapType;
import net.sf.javascribe.langsupport.java.jsom.JsomJavaCode;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.CodeSnippet;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5Type;

/**
 * Basic map support.  Currently does not support any of the operations that generate real code, 
 * but this type can be used to declare and pass around maps.  The map's key type is always 
 * "string".
 * @author Dave
 *
 */
public class Java5MapType implements JavaVariableType,Java5Type,MapType {

	@Override
	public String getName() {
		return "map";
	}

	@Override
	public CodeSnippet instantiate(String varName, String value) {
		return null;
	}

	@Override
	public CodeSnippet declare(String varName) throws CodeGenerationException {
		return null;
	}

	@Override
	public JavaCode declare(String varName, String elementType,CodeExecutionContext execCtx) {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		JavaVariableType eltType = (JavaVariableType)execCtx.getTypes().getType(elementType);

		ret.addImport(eltType.getImport());
		ret.addImport("java.util.HashMap");
		ret.append("HashMap<String,"+eltType.getClassName()+"> "+varName+" = null;\n");

		return new JsomJavaCode(ret);
	}

	@Override
	public JavaCode instantiate(String varName, String elementType,
			CodeExecutionContext execCtx) {
		Java5CodeSnippet code = new Java5CodeSnippet();
		JavaVariableType eltType = (JavaVariableType)execCtx.getTypes().getType(elementType);
		
		code.addImport(eltType.getImport());
		code.addImport("java.util.HashMap");
		code.append(varName+" = new HashMap<String,"+eltType.getClassName()+">();\n");

		return new JsomJavaCode(code);
	}

	@Override
	public String getClassName() {
		return "Map";
	}

	@Override
	public String getImport() {
		return "java.util.Map";
	}

	@Override
	public JavaCode declare(String name, CodeExecutionContext execCtx)
			throws JavascribeException {
		// TODO Auto-generated method stub
		return null;
	}

}

