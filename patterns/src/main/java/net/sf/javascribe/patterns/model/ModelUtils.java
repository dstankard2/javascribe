package net.sf.javascribe.patterns.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.dom4j.Element;
import org.dom4j.Namespace;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.patterns.XmlFile;

public class ModelUtils {

	public static final String CONFIG_JDBC_URL = "model.schema.jdbcUrl";
	
	public static final String CONFIG_JDBC_USERNAME = "model.schema.jdbcUsername";
	
	public static final String CONFIG_JDBC_PASSWORD = "model.schema.jdbcPassword";
	
	public static void enableJpa(ProcessorContext ctx) throws JavascribeException {
		ctx.getBuildContext().addDependency("jpa");
		//ctx.addVariableType(new EntityManagerType(null,null));
	}
	
	public static Connection connectToDatabase(ProcessorContext ctx) throws JavascribeException {
		Connection ret = null;
		String url = ctx.getProperty(CONFIG_JDBC_URL);
		String username = ctx.getProperty(CONFIG_JDBC_USERNAME);
		String password = ctx.getProperty(CONFIG_JDBC_PASSWORD);

		try {
			ret = DriverManager.getConnection(url,username,password);
		} catch(SQLException e) {
			throw new JavascribeException("Unable to connect to database URL "+url+" - "+e.getMessage(),e);
		}

		return ret;
	}

	public static void saveSchemaInfo(TableSet schema,String schemaId,ProcessorContext ctx) throws JavascribeException {
		ctx.setObject("Schema_"+schemaId, schema);
	}

	public static TableSet getTableSet(String schemaId,ProcessorContext ctx) throws JavascribeException {
		TableSet ret = null;
		
		ret = (TableSet)ctx.getObject("Schema_"+schemaId);
		
		return ret;
	}
	
	public static XmlFile getPersistenceXml(ProcessorContext ctx) {
		String path = null;
		XmlFile ret = null;
		String root = ctx.getBuildContext().getOutputRootPath();
		
		path = root+"src/main/resources/META-INF/persistence.xml";
		ret = (XmlFile)ctx.getSourceFile(path);
		if (ret==null) {
			ret = new XmlFile(path);
			Element elt = ret.getDocument().addElement("persistence","http://xmlns.jcp.org/xml/ns/persistence");
			elt.addAttribute("version", "2.1");
			elt.addAttribute("xsi:schemaLocation", "http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd");
			elt.add(new Namespace("xsi","http://www.w3.org/2001/XMLSchema-instance"));
			ctx.addSourceFile(ret);
		}
		
		return ret;
	}

	/*
	public static void addEntitySet(ProcessorContext ctx,String txRef,TableSet tableSet) {
		ctx.setObject("TableSet_"+txRef, tableSet);
	}
	
	public static TableSet getEntitySet(ProcessorContext ctx,String txRef) {
		return (TableSet)ctx.getObject("TableSet_"+txRef);
	}
	*/

}

