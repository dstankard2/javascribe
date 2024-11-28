package net.sf.javascribe.langsupport.java.types.impl;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.types.JavaVariableTypeBase;

public class DoubleJavaType extends JavaVariableTypeBase {

	public DoubleJavaType() {
		super("double",null,null);
	}

	@Override
	public String getName() {
		return "double";
	}

	@Override
	public String getImport() {
		return null;
	}

	@Override
	public String getClassName() {
		return "Double";
	}

	@Override
	public JavaCode declare(String name, CodeExecutionContext execCtx) {
		return new JavaCode("Double "+name+" = null;\n");
	}

}
