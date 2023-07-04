package net.sf.javascribe.langsupport.java.types.impl;

import net.sf.javascribe.langsupport.java.types.JavaVariableTypeBase;

public class LocalDateTimeJavaType extends JavaVariableTypeBase {

	public LocalDateTimeJavaType() {
		super("LocalDateTime","java.time.LocalDateTime",null);
	}

	@Override
	public String getName() {
		return "datetime";
	}

}
