package net.sf.javascribe.langsupport.javascript;

import java.util.Arrays;
import java.util.List;

import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.types.ListType;

public class ListVariableType extends JavascriptBaseObjectType implements ListType {

	@Override
	public String getCodeToRetrieveAttribute(String varName, String attribName,
			String targetType, CodeExecutionContext execCtx)
			throws IllegalArgumentException, JavascribeException {

		if (attribName.equals("length")) {
			return varName+".length";
		}
		return null;
	}

	@Override
	public String getCodeToSetAttribute(String varName, String attribName,
			String evaluatedValue, CodeExecutionContext execCtx)
			throws JavascribeException {
		throw new JavascribeException("You cannot manipulate attributes of a Javascript list type");
	}

	@Override
	public String getAttributeType(String attrib) throws JavascribeException {
		if (attrib.equals("length")) return "integer";
		return null;
	}

	@Override
	public List<String> getAttributeNames() throws JavascribeException {
		return Arrays.asList(new String[] { "length" });
	}

	@Override
	public String getName() {
		return "list";
	}

	@Override
	public Code instantiate(String name, String value,
			CodeExecutionContext execCtx) throws JavascribeException {
		JavascriptCode ret = new JavascriptCode(true);
		ret.append(name+" = [ ];\n");
		return ret;
	}

	@Override
	public Code declare(String name, CodeExecutionContext execCtx)
			throws JavascribeException {
		JavascriptCode ret = new JavascriptCode(true);
		ret.append("var "+name+";\n");
		return ret;
	}

	@Override
	public Code declare(String varName, String elementType,
			CodeExecutionContext execCtx) throws JavascribeException {
		return declare(varName,execCtx);
	}

	@Override
	public Code appendToList(String listVarName, String value,
			CodeExecutionContext execCtx) throws JavascribeException {
		JavascriptCode ret = new JavascriptCode(true);
		ret.append(listVarName+".push("+value+");\n");
		return ret;
	}

}
