package net.sf.javascribe.patterns.model;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.types.JavaVariableType;
import net.sf.javascribe.patterns.model.types.EntityManagerType;

public interface EntityManagerLocator extends JavaVariableType {

	public JavaCode getEntityManager(String var,CodeExecutionContext execCtx);

	public JavaCode releaseEntityManager(String var,CodeExecutionContext execCtx);

	public EntityManagerType getEntityManagerType();
	
}

