package net.sf.javascribe.langsupport.java.types.impl;

import net.sf.javascribe.langsupport.java.types.JavaVariableTypeBase;

public class DateJavaType extends JavaVariableTypeBase {

	public DateJavaType() {
		super("Date","java.sql.Date",null);
	}

	@Override
	public String getName() {
		return "date";
	}

}
