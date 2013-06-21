package net.sf.javascribe.langsupport.java;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;

public interface Injectable extends JavaVariableType {

	public JavaCode getInstance(String instanceName,CodeExecutionContext execCtx) throws JavascribeException;

}
