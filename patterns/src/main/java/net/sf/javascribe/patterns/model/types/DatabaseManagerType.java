package net.sf.javascribe.patterns.model.types;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.types.JavaVariableType;

public interface DatabaseManagerType extends JavaVariableType {

	public JavaCode rollback(String dbVarRef,CodeExecutionContext execCtx);

	public JavaCode commit(String dbVarRef,CodeExecutionContext execCtx);

	public JavaCode close(String dbVarRef,CodeExecutionContext execCtx);

	public JavaCode setReadOnly(String dbVarRef,CodeExecutionContext execCtx);

}
