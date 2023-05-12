package net.sf.javascribe.langsupport.java.types.impl;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.types.JavaVariableTypeBase;

public class ObjectJavaType extends JavaVariableTypeBase {

	public ObjectJavaType() {
		super("Object",null,null);
	}

	@Override
	public String getName() {
		return "object";
	}

	@Override
	public String getImport() {
		return null;
	}

	@Override
	public String getClassName() {
		return "Object";
	}

	@Override
	public JavaCode declare(String name, CodeExecutionContext execCtx) {
		return new JavaCode("Object "+name+" = null;\n");
	}

}
