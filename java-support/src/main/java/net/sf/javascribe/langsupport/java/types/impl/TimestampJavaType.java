package net.sf.javascribe.langsupport.java.types.impl;

import net.sf.javascribe.langsupport.java.types.JavaVariableTypeBase;

public class TimestampJavaType extends JavaVariableTypeBase {

	public TimestampJavaType() {
		super("Timestamp","java.sql.Timestamp",null);
	}

	@Override
	public String getName() {
		return "timestamp";
	}

}
