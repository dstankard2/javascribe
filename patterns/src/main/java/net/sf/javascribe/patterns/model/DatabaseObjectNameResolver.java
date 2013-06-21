package net.sf.javascribe.patterns.model;

import net.sf.javascribe.api.JavascribeException;

public interface DatabaseObjectNameResolver {

	public String name();
	
	public String getEntityName(String tableName) throws JavascribeException;
	
	public String getAttributeName(String columnName) throws JavascribeException;
	
}
