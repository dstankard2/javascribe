package net.sf.javascribe.api;


public interface VariableType {

	public String getName();
	public Code instantiate(String name,String value,CodeExecutionContext execCtx) throws JavascribeException;
	public Code declare(String name,CodeExecutionContext execCtx) throws JavascribeException;
	
}

