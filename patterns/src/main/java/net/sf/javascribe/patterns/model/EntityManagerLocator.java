package net.sf.javascribe.patterns.model;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaVariableType;

public interface EntityManagerLocator extends JavaVariableType {

	public JavaCode getEntityManager(String varName,CodeExecutionContext execCtx);
	public JavaCode unallocateEntityManager(String varName);

}

