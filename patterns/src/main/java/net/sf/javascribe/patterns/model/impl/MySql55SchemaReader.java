package net.sf.javascribe.patterns.model.impl;

import java.util.List;

import net.sf.javascribe.api.GeneratorContext;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.model.DatabaseSchemaReader;
import net.sf.javascribe.patterns.model.DatabaseTable;

@Scannable
public class MySql55SchemaReader implements DatabaseSchemaReader {

	@Override
	public String databaseType() {
		return "MySQL 5.5";
	}

	@Override
	public List<DatabaseTable> readSchema(String url, String username,
			String password,String catalog, GeneratorContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

}
