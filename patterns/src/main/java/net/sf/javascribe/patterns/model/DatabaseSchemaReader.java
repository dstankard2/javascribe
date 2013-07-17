package net.sf.javascribe.patterns.model;

import java.util.List;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeException;

public interface DatabaseSchemaReader {

	public String databaseType();
	
	public List<DatabaseTable> readSchema(String url,String username,String password,String catalog,ProcessorContext ctx) throws JavascribeException;

}

