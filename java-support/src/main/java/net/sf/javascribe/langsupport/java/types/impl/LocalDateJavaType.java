package net.sf.javascribe.langsupport.java.types.impl;

import net.sf.javascribe.langsupport.java.types.JavaVariableTypeBase;

public class LocalDateJavaType extends JavaVariableTypeBase {

	public LocalDateJavaType() {
		super("LocalDate","java.time.LocalDate",null);
	}

	@Override
	public String getName() {
		return "date";
	}

}
