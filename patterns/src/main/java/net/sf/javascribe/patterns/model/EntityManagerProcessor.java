package net.sf.javascribe.patterns.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.java.JavaBeanType;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.jsom.JavascribeVariableTypeResolver;
import net.sf.javascribe.langsupport.java.jsom.JsomJavaBeanType;
import net.sf.javascribe.langsupport.java.jsom.JsomUtils;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.java5.Java5Annotation;
import net.sf.jsom.java5.Java5ClassConstructor;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5DataObjectSourceFile;
import net.sf.jsom.java5.NVPAnnotationArgument;

@Scannable
@Processor
public class EntityManagerProcessor {

	private static final Logger log = Logger.getLogger(EntityManagerProcessor.class);
	
	@ProcessorMethod(componentClass=EntityManagerComponent.class)
	public void process(EntityManagerComponent comp,ProcessorContext ctx) throws JavascribeException {
		List<DatabaseTable> tables = null;
		PersistenceUnitConfig puConfig = null;

		try {
			log.info("Processing Entity Manager '"+comp.getName()+"'");
			
			ctx.setLanguageSupport("Java");

			puConfig = createPersistenceUnit(comp,ctx);
			tables = EntityManagerUtils.readTables(comp, ctx);
			for(DatabaseTable table : tables) {
				String entityName = EntityManagerUtils.getEntityName(table.getName(), ctx, comp);
				String lowerCamel = JavascribeUtils.getLowerCamelName(entityName);
				try {
					addEntityRowType(puConfig,ctx,table,comp);
				} catch(Exception ex) {
					throw new CodeGenerationException("Couldn't process database table "+table.getName(),ex);
				}
				addEntityRowClass(table,ctx,comp);
				ctx.addAttribute(lowerCamel, entityName);
				ctx.addAttribute(lowerCamel+"List", "list/"+entityName);
			}
			addEntityManagerType(tables,comp,ctx);
		} catch(CodeGenerationException e) {
			throw new JavascribeException("JSOM exception while processing component",e);
		}
	}

	private void addEntityRowClass(DatabaseTable table,ProcessorContext ctx,EntityManagerComponent comp) throws CodeGenerationException,JavascribeException {
		JavascribeVariableTypeResolver types = new JavascribeVariableTypeResolver(ctx);
		Java5DataObjectSourceFile src = new Java5DataObjectSourceFile(types);
		String entityName = EntityManagerUtils.getEntityName(table.getName(), ctx, comp);
		Java5ClassConstructor con = new Java5ClassConstructor(types, entityName);

		src.getPublicClass().setClassName(entityName);
		src.setPackageName(JavaUtils.findPackageName(ctx, ctx.getRequiredProperty(EntityManagerComponent.ENTITY_PACKAGE)));
		JsomUtils.addJavaFile(src, ctx);

		src.getPublicClass().addMethod(con);
		src.getPublicClass().addAnnotation(new Java5Annotation("javax.persistence.Entity"));

		con = new Java5ClassConstructor(types, entityName);
		Java5CodeSnippet code = new Java5CodeSnippet();
		con.setMethodBody(code);
		src.getPublicClass().addMethod(con);

		for(DatabaseTableColumn col : table.getColumns()) {
			if (col.getJavaType()==null) {
				throw new JavascribeException("The database column "+col.getName()+" has an unsupported type "+col.getType());
			}
			String prop = EntityManagerUtils.getAttributeName(col.getName(), ctx, comp);
			src.addJavaBeanProperty(prop, col.getJavaType());
			con.addArg(col.getJavaType(), prop);
			code.append("this."+prop+" = "+prop+";\n");
			if (table.getPrimaryKeyColumn()!=null) {
				if (col.getName().equals(table.getPrimaryKeyColumn())) {
					Java5Annotation an = new Java5Annotation("javax.persistence.Id");
					src.addPropertyAnnotation(prop, an);
					an = new Java5Annotation("javax.persistence.GeneratedValue");
					an.addArgument(new NVPAnnotationArgument("strategy","GenerationType.AUTO"));
					src.addImport("javax.persistence.GenerationType");
					src.addPropertyAnnotation(prop, an);
				}
			}
			src.addPropertyAnnotation(prop, new Java5Annotation("javax.persistence.Column"));

			if (ctx.getAttributeType(prop)!=null) {
				if (!ctx.getAttributeType(prop).equals(col.getJavaType())) {
					throw new CodeGenerationException("Found inconsistent type for attribute '"+prop+"'");
				}
			}
		}
	}

	private void addEntityManagerType(List<DatabaseTable> tables,EntityManagerComponent comp,ProcessorContext ctx) throws JavascribeException {
		List<String> entityNames = new ArrayList<String>();
		
		for(DatabaseTable tab : tables) {
			String n = EntityManagerUtils.getEntityName(tab.getName(), ctx, comp);
			entityNames.add(n);
		}
		EntityManagerType type = new EntityManagerType(entityNames,comp.getName());
		ctx.getTypes().addType(type);
		ctx.addAttribute(comp.getRef(), comp.getName());
	}

	private PersistenceUnitConfig createPersistenceUnit(EntityManagerComponent comp,ProcessorContext ctx) throws JavascribeException {
		String cfgPath = ctx.getBuildRoot()+File.separatorChar+"persistence.xml";
		PersistenceUnitConfig ret = null;

		PersistenceConfig cfg = (PersistenceConfig)ctx.getSourceFile(cfgPath);
		if (cfg==null) {
			cfg = new PersistenceConfig();
			cfg.setPath(cfgPath);
			ctx.addSourceFile(cfg);
		}
		ret = cfg.createPersistenceUnit(comp.getName());

		String showSql = ctx.getProperty(EntityManagerComponent.SHOW_SQL);
		if ((showSql!=null) && (showSql.equalsIgnoreCase("true")))
			ret.addProperty("hibernate.show_sql", "true");

		ret.addProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
		ret.setProvider("org.hibernate.ejb.HibernatePersistence");
		ret.setTransactionType("local");
		ret.addProperty("hibernate.connection.driver_class", ctx.getRequiredProperty(EntityManagerComponent.RUNTIME_JPA_DRIVER));
		ret.addProperty("hibernate.connection.url", ctx.getRequiredProperty(EntityManagerComponent.RUNTIME_JPA_URL));
		ret.addProperty("hibernate.connection.username", ctx.getRequiredProperty(EntityManagerComponent.RUNTIME_JPA_USERNAME));
		ret.addProperty("hibernate.connection.password", ctx.getRequiredProperty(EntityManagerComponent.RUNTIME_JPA_PASSWORD));
		
		return ret;
	}

	private String getEntityPackage(ProcessorContext ctx) throws JavascribeException {
		return JavaUtils.findPackageName(ctx, ctx.getRequiredProperty(EntityManagerComponent.ENTITY_PACKAGE));
	}

	private void addEntityRowType(PersistenceUnitConfig puConfig,ProcessorContext ctx,DatabaseTable table,EntityManagerComponent comp) throws JavascribeException {
		String pkg = getEntityPackage(ctx);
		String entity = null;
		
		entity = EntityManagerUtils.getEntityName(table.getName(), ctx, comp);
		log.debug("Processing DB table "+table.getName()+" as attribute "+entity);
		JavaBeanType type = new JsomJavaBeanType(entity,pkg,entity);
		for(DatabaseTableColumn col : table.getColumns()) {
			String prop = EntityManagerUtils.getAttributeName(col.getName(), ctx, comp);
			log.debug("Processing DB column "+col.getName()+" as attribute "+prop);
			ctx.addAttribute(prop, col.getJavaType());
			type.addAttribute(prop, col.getJavaType());
		}

		ctx.getTypes().addType(type);
		puConfig.addClass(pkg+'.'+entity);
	}

}

