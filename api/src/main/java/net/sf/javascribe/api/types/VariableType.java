package net.sf.javascribe.api.types;

import net.sf.javascribe.api.BuildContext;
import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.exception.JavascribeException;

public interface VariableType {

	public String getName();
	public Code declare(String name,CodeExecutionContext execCtx) throws JavascribeException;
	public BuildContext getBuildContext();

}
