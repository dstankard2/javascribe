package net.sf.javascribe.patterns.model.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.model.DatabaseObjectNameResolver;

@Scannable
public class DefaultDatabaseObjectNameResolver implements DatabaseObjectNameResolver {

	@Override
	public String name() { return "default"; }

	@Override
	public String getEntityName(String tableName) throws JavascribeException {
		if (!Character.isUpperCase(tableName.charAt(0))) {
			throw new JavascribeException("By default, database table names " +
					"should contain only letters and should start with an " +
					"uppercase letter - Consider implementing your own " +
					"DatabaseObjectNameResolver");
		}
		return tableName;
	}

	@Override
	public String getAttributeName(String columnName) {
		return columnName;
	}

}
