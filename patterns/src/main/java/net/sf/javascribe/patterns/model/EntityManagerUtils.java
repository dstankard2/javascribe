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
				ctx.addObject(EntityManagerComponent.NAME_RESOLVERS, ret);
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
			throw new JavascribeException("Couldn't find database object name resolver '"+databaseObjectNameResolver+"'");
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
				ctx.addObject(EntityManagerComponent.SCHEMA_READERS, schemaReaders);
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
			throw new JavascribeException("No schema reader found for database type '"+databaseType+"'");
		}
		ret = reader.readSchema(url, username, password, catalog, ctx);

		return ret;
	}

	/*
	public static List<Entity> readSchema(GeneratorContext ctx) throws JavascribeException {
		List<Entity> ret = new ArrayList<Entity>();

		String driverClass = ctx.getRequiredProperty(EntityManagerProcessor.JPA_DRIVER);
		String jdbcUrl = ctx.getRequiredProperty(EntityManagerProcessor.JPA_URL);
		String jdbcUsername = ctx.getRequiredProperty(EntityManagerProcessor.JPA_USERNAME);
		String jdbcPassword = ctx.getRequiredProperty(EntityManagerProcessor.JPA_PASSWORD);
		String jdbcCatalog = ctx.getRequiredProperty(EntityManagerProcessor.CONNECT_JPA_CATALOG);
		String versionField = ctx.getRequiredProperty(EntityManagerProcessor.JPA_VERSION_FIELD);

		Connection conn = null;

		try {
			Class.forName(driverClass);
			conn = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);

			List<String> tableNames = EntityManagerUtils.findTableNames(conn,jdbcCatalog);
			for(String s : tableNames) {
				Entity entity = EntityManagerUtils.readTable(conn,jdbcCatalog,s,versionField);
				ret.add(entity);
			}
		} catch(Exception e) {
			throw new JavascribeException("Error adding types",e);
		} finally {
			if (conn!=null) {
				try { conn.close(); } catch(Exception e) { }
			}
		}
		return ret;
	}
	*/

	/*
	public static List<String> findTableNames(Connection conn,String catalog) throws Exception {
		List<String> ret = new ArrayList<String>();
		Statement stmt = null;
		ResultSet res = null;

		try {
			stmt = conn.createStatement();
			res = stmt.executeQuery("show tables in "+catalog);
			while(res.next()) {
				ret.add(res.getString(1));
			}
		} finally {
			try { res.close(); } catch(Exception e) { }
			try { stmt.close(); } catch(Exception e) { }
		}

		return ret;
	}
	*/

	/*
	public static Entity readTable(Connection conn,String schema,String tableName,String versionField) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet res = null;
		Entity entity = new Entity();

		try {
			entity.name = tableName;
			entity.lowerCamelName = Character.toLowerCase(tableName.charAt(0))+tableName.substring(1);
			pstmt = conn.prepareStatement("show columns in "+schema+"."+tableName);
			res = pstmt.executeQuery();
			while(res.next()) {
				EntityAttrib att = new EntityAttrib();
				if ((versionField!=null) && (res.getString(1).equals(versionField))) {
					entity.versionField = res.getString(1);
					continue;
				}
				att.name = res.getString(1);
				entity.attributes.put(att.name, att);
				String s = res.getString(3);
				if (s.equals("NO")) att.isNull = true;
				s = res.getString(2);
				if (s.indexOf("bigint")>=0) {
					att.type = "longint";
				} else if (s.indexOf("int")>=0) {
					att.type = "integer";
				} else if (s.indexOf("varchar")==0) {
					att.type = "string";
					int end = s.indexOf(')');
					att.size = s.substring(8,end);
				} else if (s.indexOf("datetime")==0) {
					att.type = "timestamp";
				} else if (s.indexOf("date")==0) {
					att.type = "date";
				} else if (s.indexOf("text")==0) {
					att.type = "string";
				} else {
					System.out.println("Found no type for "+s);
				}
			}
		} finally {
			if (res!=null) {
				try { res.close(); } catch(Exception e) { }
			}
			if (pstmt!=null) {
				try { pstmt.close(); } catch(Exception e) { }
			}
		}
		return entity;
	}
	*/

}

