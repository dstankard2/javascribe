package net.sf.javascribe.patterns.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.javascribe.api.GeneratorContext;
import net.sf.javascribe.api.JavascribeException;

public class EntityManagerUtils {

	public static String getDatabaseType(EntityManagerComponent comp,GeneratorContext ctx) throws JavascribeException {
		if (comp.getDatabaseType().trim().length()>0) {
			return comp.getDatabaseType();
		} else if (ctx.getProperty(EntityManagerComponent.DATABASE_TYPE)!=null) {
			return ctx.getProperty(EntityManagerComponent.DATABASE_TYPE);
		}
		throw new JavascribeException("Could not find databaseType in EntityManager component or in property '"+EntityManagerComponent.DATABASE_TYPE+"'");
	}
	
	public static String getCatalog(EntityManagerComponent comp,GeneratorContext ctx) throws JavascribeException {
		if (comp.getCatalog().trim().length()>0) {
			return comp.getCatalog();
		} else if (ctx.getProperty(EntityManagerComponent.CATALOG)!=null) {
			return ctx.getProperty(EntityManagerComponent.CATALOG);
		}
		throw new JavascribeException("Could not find catalog in EntityManager component or in property '"+EntityManagerComponent.CATALOG+"'");
	}
	
	private static HashMap<String,DatabaseObjectNameResolver> getNameResolvers(GeneratorContext ctx) throws JavascribeException {
		HashMap<String,DatabaseObjectNameResolver> ret = null;
		
		try {
			ret = (HashMap<String,DatabaseObjectNameResolver>)ctx.getObject(EntityManagerComponent.NAME_RESOLVERS);
			if (ret==null) {
				ret = new HashMap<String,DatabaseObjectNameResolver>();
				ctx.putObject(EntityManagerComponent.NAME_RESOLVERS, ret);
				List<Class<?>> resolvers = ctx.getEngineProperties().getScannedClassesOfInterface(DatabaseObjectNameResolver.class);
				for(Class<?> cl : resolvers) {
					DatabaseObjectNameResolver inst = (DatabaseObjectNameResolver)cl.newInstance();
					ret.put(inst.name(), inst);
				}
			}
		} catch(Exception e) {
			throw new JavascribeException("Couldn't load databae schema readers",e);
		}
		
		return ret;
	}

	public static String getAttributeName(String tableFieldName,GeneratorContext ctx,EntityManagerComponent comp) throws JavascribeException {
		String databaseObectNameResolver = ctx.getRequiredProperty(EntityManagerComponent.NAME_RESOLVER);

		return getAttributeName(databaseObectNameResolver,tableFieldName,ctx);
	}

	public static String getEntityName(String tableName,GeneratorContext ctx,EntityManagerComponent comp) throws JavascribeException {
		String databaseObectNameResolver = ctx.getRequiredProperty(EntityManagerComponent.NAME_RESOLVER);

		return getEntityName(databaseObectNameResolver,tableName,ctx);
	}

	public static String getEntityName(String databaseObjectNameResolver,String tableName,GeneratorContext ctx) throws JavascribeException {
		HashMap<String,DatabaseObjectNameResolver> map = getNameResolvers(ctx);
		if (map.get(databaseObjectNameResolver)==null) {
			String nameString = "";
			boolean first = true;
			for(String s : map.keySet()) {
				if (!first) nameString = nameString + ',';
				else first = false;
				nameString = nameString + s;
			}
			throw new JavascribeException("Couldn't find database object name resolver '"+databaseObjectNameResolver+"'.  Valid values are: "+nameString);
		}
		return map.get(databaseObjectNameResolver).getEntityName(tableName);
	}

	public static String getAttributeName(String databaseObjectNameResolver,String tableFieldName,GeneratorContext ctx) throws JavascribeException {
		HashMap<String,DatabaseObjectNameResolver> map = getNameResolvers(ctx);
		if (map.get(databaseObjectNameResolver)==null) {
			throw new JavascribeException("Couldn't find database object name resolver '"+databaseObjectNameResolver+"'");
		}
		return map.get(databaseObjectNameResolver).getAttributeName(tableFieldName);
	}

	public static List<DatabaseTable> readTables(EntityManagerComponent comp,GeneratorContext ctx) throws JavascribeException {
		List<DatabaseTable> ret = null;
		String databaseType = getDatabaseType(comp, ctx);
		String catalog = getCatalog(comp, ctx);
		String url = ctx.getRequiredProperty(EntityManagerComponent.CODEGEN_JPA_URL);
		String username = ctx.getRequiredProperty(EntityManagerComponent.CODEGEN_JPA_USERNAME);
		String password = ctx.getRequiredProperty(EntityManagerComponent.CODEGEN_JPA_PASSWORD);

		ret = readTables(databaseType,ctx,url,username,password,catalog);
		return ret;
	}

	public static List<DatabaseTable> readTables(String databaseType,GeneratorContext ctx,String url,String username,String password,String catalog) throws JavascribeException {
		List<DatabaseTable> ret = new ArrayList<DatabaseTable>();
		HashMap<String,DatabaseSchemaReader> schemaReaders = null;

		try {
			schemaReaders = (HashMap<String,DatabaseSchemaReader>)ctx.getObject(EntityManagerComponent.SCHEMA_READERS);
			if (schemaReaders==null) {
				schemaReaders = new HashMap<String,DatabaseSchemaReader>();
				ctx.putObject(EntityManagerComponent.SCHEMA_READERS, schemaReaders);
				List<Class<?>> readers = ctx.getEngineProperties().getScannedClassesOfInterface(DatabaseSchemaReader.class);
				for(Class<?> cl : readers) {
					DatabaseSchemaReader inst = (DatabaseSchemaReader)cl.newInstance();
					schemaReaders.put(inst.databaseType(), inst);
				}
			}
		} catch(Exception e) {
			throw new JavascribeException("Couldn't load databae schema readers",e);
		}
		DatabaseSchemaReader reader = schemaReaders.get(databaseType);
		if (reader==null) {
			String nameString = "";
			boolean first = true;
			for(String s : schemaReaders.keySet()) {
				if (!first) nameString = nameString + ',';
				else first = false;
				nameString = nameString + s;
			}
			throw new JavascribeException("No schema reader found for database type '"+databaseType+"'.  Valid values are "+nameString);
		}
		ret = reader.readSchema(url, username, password, catalog, ctx);

		return ret;
	}

}

