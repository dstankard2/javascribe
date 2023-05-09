package net.sf.javascribe.langsupport.java.types.impl;

import net.sf.jaspercode.api.CodeExecutionContext;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.types.JavaVariableTypeBase;

public class LongJavaType extends JavaVariableTypeBase {

	public LongJavaType() {
		super("longint",null,null);
	}

	@Override
	public String getName() {
		return "longint";
	}

	@Override
	public String getImport() {
		return null;
	}

	@Override
	public String getClassName() {
		return "Long";
	}

	@Override
	public JavaCode declare(String name, CodeExecutionContext execCtx) {
		return new JavaCode("Long "+name+" = null;\n");
	}

}
