package net.sf.javascribe.langsupport.javascript.types;

import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.types.ListType;
import net.sf.javascribe.api.types.VariableType;
import net.sf.javascribe.langsupport.javascript.JavascriptCode;

public class ArrayType extends JavascriptType implements ListType {

	private VariableType elementType = null;
	
	@Override
	public Code instantiate(String varName) {
		return new JavascriptCode(varName+" = [];\n");
	}

	@Override
	public ListType getListTypeWithElementTypName(VariableType elementType) {
		ArrayType ret = new ArrayType();
		ret.elementType = elementType;
		return ret;
	}

	public ArrayType() {
		super("list");
	}

	@Override
	public String getName() {
		if (elementType!=null) {
			return "list/"+elementType.getName();
		} else {
			return "list";
		}
	}

	@Override
	public Code declare(String varName, String elementType, CodeExecutionContext execCtx) throws JavascribeException {
		return declare(varName,execCtx);
	}

	@Override
	public Code instantiate(String varName, String elementType, CodeExecutionContext execCtx) throws JavascribeException {
		return instantiate(varName);
	}

	@Override
	public Code appendToList(String listVarName, String value, CodeExecutionContext execCtx) throws JavascribeException {
		return new JavascriptCode(listVarName+".push("+value+");\n");
	}

	@Override
	public VariableType getElementType() {
		return elementType;
	}

}

