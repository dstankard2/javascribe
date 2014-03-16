package net.sf.javascribe.api.types;

import net.sf.javascribe.api.AttributeHolder;
import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.VariableType;

public interface ListType extends VariableType,AttributeHolder {

	public Code declare(String varName,String elementType,CodeExecutionContext execCtx) throws JavascribeException;
	public Code instantiate(String varName,String elementType,CodeExecutionContext execCtx) throws JavascribeException;
	public Code appendToList(String listVarName,String value,CodeExecutionContext execCtx) throws JavascribeException;

}

