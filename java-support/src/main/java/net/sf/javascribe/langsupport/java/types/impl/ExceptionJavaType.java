package net.sf.javascribe.langsupport.java.types.impl;

import net.sf.javascribe.api.BuildContext;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.exception.JasperException;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.types.JavaVariableType;

public class ExceptionJavaType implements JavaVariableType {
	private BuildContext buildCtx = null;
	private String className = null;
	private String pkg = null;

	public ExceptionJavaType(String name,String pkg,BuildContext buildCtx) {
		this.className = name;
		this.pkg = pkg;
		this.buildCtx = buildCtx;
	}

	@Override
	public String getName() {
		return className;
	}

	@Override
	public BuildContext getBuildContext() {
		return buildCtx;
	}

	@Override
	public String getImport() {
		return pkg+'.'+className;
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public JavaCode declare(String name, CodeExecutionContext execCtx) throws JasperException {
		JavaCode code = new JavaCode(className+" = "+name+" = null;\n");
		if (getImport()!=null) code.addImport(getImport());
		return code;
	}

}

