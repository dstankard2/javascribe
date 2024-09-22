package net.sf.javascribe.patterns.model;

import org.dom4j.Element;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaClassSourceFile;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.types.JavaVariableType;
import net.sf.javascribe.langsupport.java.types.impl.JavaDataObjectType;
import net.sf.javascribe.langsupport.java.types.impl.JavaEnumType;
import net.sf.javascribe.patterns.XmlFile;
import net.sf.javascribe.patterns.model.types.EntityManagerType;
import net.sf.javascribe.patterns.xml.model.PersistenceUnit;

@Plugin
public class PersistenceUnitProcessor implements ComponentProcessor<PersistenceUnit> {

	@Override
	public void process(PersistenceUnit persistenceUnit, ProcessorContext ctx) throws JavascribeException {
		String tableSetId = persistenceUnit.getTableSetId();
		XmlFile persistenceXml = null;
		Element root = null;
		Element pu = null;
		String txRef = persistenceUnit.getTxRef();
		String puName = persistenceUnit.getName();
		ctx.setLanguageSupport("Java8");

		TableSet tableSet = ModelUtils.getTableSet(tableSetId, ctx);

		if (tableSet==null) {
			throw new JavascribeException("Could find table definitions for table set named '"+tableSetId+"'");
		}

		ctx.getLog().info("Processing persistence unit '"+persistenceUnit.getName()+"'");

		persistenceXml = ModelUtils.getPersistenceXml(ctx);
		root = persistenceXml.getDocument().getRootElement();

		pu = root.addElement("persistence-unit");
		pu.addAttribute("transaction-type", "RESOURCE_LOCAL");
		pu.addAttribute("name", puName);

		pu.addElement("provider").setText("org.hibernate.jpa.HibernatePersistenceProvider");

		if (persistenceUnit.getJndiDataSource()!=null) {
			String name = persistenceUnit.getJndiDataSource();
			pu.addElement("non-jta-data-source").setText(name);
		} else {
			throw new JavascribeException("A persistence unit requires a JNDI data source (with property 'jpa.jndiDataSource'), or the database connection will not be available.");
		}
		
		for(TableInfo ti : tableSet.getTableInfos()) {
			addEntity(persistenceUnit,ti,pu,ctx);
		}
		
		Element props = pu.addElement("properties");
		Element dialect = props.addElement("property");
		dialect.addAttribute("name", "hibernate.dialect");
		if (tableSet.getDatabase()==DatabaseType.MYSQL) {
			// Add MySQL dialect and dependencies
			dialect.addAttribute("value","org.hibernate.dialect.MySQLDialect");
			ctx.getBuildContext().addDependency("mysql-connector");
			if (persistenceUnit.getShowSql()!=null) {
				Element showSql = props.addElement("property");
				showSql.addAttribute("value", "true");
				showSql.addAttribute("name", "hibernate.show_sql");
			}
		}

		ctx.getBuildContext().addDependency("jpa");
		ctx.getBuildContext().addDependency("hibernate");
		ctx.getBuildContext().addDependency("jaxb-impl");

		ModelUtils.enableJpa(ctx);
		EntityManagerType emType = new EntityManagerType(puName,tableSet,ctx.getBuildContext());
		ctx.addSystemAttribute(txRef, emType.getName());
		ctx.addVariableType(emType);
	}

	protected void addEntity(PersistenceUnit persistenceUnit,TableInfo tableInfo,Element puElt,ProcessorContext ctx) throws JavascribeException {
		String typeName = tableInfo.getEntityName();
		JavaClassSourceFile entityFile = new JavaClassSourceFile(ctx);
		String pkg = JavaUtils.getJavaPackage(persistenceUnit, ctx);
		JavaDataObjectType objType = new JavaDataObjectType(typeName,pkg+'.'+typeName,ctx.getBuildContext());

		entityFile.getSrc().setPackage(pkg);
		entityFile.getSrc().setName(typeName);
		entityFile.getSrc().addAnnotation("javax.persistence.Entity");
		entityFile.getSrc().addAnnotation("javax.persistence.Table")
				.setLiteralValue("name", "\""+tableInfo.getTableName()+"\"")
				.setLiteralValue("catalog", "\""+tableInfo.getSchema()+"\"");
		entityFile.getSrc().addMethod().setConstructor(true).setPublic().setBody("");
		MethodSource<JavaClassSource> constructor = entityFile.getSrc().addMethod().setConstructor(true).setPublic();
		StringBuilder constructorCode = new StringBuilder();
		for(ColumnInfo col : tableInfo.getColumns()) {
			String enumType = null;
			String name = col.getAttributeName();
			String attrType = col.getAttributeType();
			String colName = col.getName();
			String colType = col.getType();
			ctx.addSystemAttribute(name, attrType);
			String colClass = null;

			objType.addProperty(name, colType);
			if (attrType.equals("string")) colClass = "String";
			else if (attrType.equals("integer")) colClass = "Integer";
			else if (attrType.equals("datetime")) colClass = "java.time.LocalDateTime";
			else if (attrType.equals("date")) colClass = "java.time.LocalDate";
			else if (attrType.equals("longint")) colClass = "Long";
			else if (attrType.equals("boolean")) colClass = "Boolean";
			else if (attrType.equals("double")) colClass = "Double";
			else {
				// This may be an enumeration
				JavaVariableType t = JavascribeUtils.getType(JavaVariableType.class, attrType, ctx);
				if (t instanceof JavaEnumType) {
					colClass = t.getImport();
					if (colType.indexOf("int")>=0) {
						enumType = "javax.persistence.EnumType.ORDINAL";
					} else {
						enumType = "javax.persistence.EnumType.STRING";
					}
					//isEnum = true;
				}
				else {
					throw new JavascribeException("Couldn't find entity property type for attribute type '"+name+"' - column type was '"+colType+"'");
				}
			}
			boolean nullible = col.isNullable();
			entityFile.getSrc().addProperty(colClass, name);

			constructor.addParameter(colClass, name);
			constructorCode.append("this."+name+" = "+name+";\n");
			FieldSource<JavaClassSource> field = entityFile.getSrc().getField(name);
			AnnotationSource<JavaClassSource> an = field.addAnnotation();
			an.setName("javax.persistence.Column")
					.setLiteralValue("name", "\""+colName+"\"");
			if (!nullible) {
				an.setLiteralValue("nullable","false");
			}
			if (enumType!=null) {
				AnnotationSource<JavaClassSource> a2 = field.addAnnotation().setName("javax.persistence.Enumerated");
				a2.setLiteralValue(enumType);
			}
			if (col.isPrimaryKey()) {
				field.addAnnotation("javax.persistence.Id");
			}
			if (col.isAutoGenerate()) {
				field.addAnnotation("javax.persistence.GeneratedValue").setLiteralValue("strategy", "javax.persistence.GenerationType.IDENTITY");
			}
		}
		constructor.setBody(constructorCode.toString());
		ctx.addVariableType(objType);
		ctx.addSourceFile(entityFile);

		String attribName = JavascribeUtils.getLowerCamelName(typeName);
		String multiple = JavascribeUtils.getMultiple(attribName);

		ctx.addSystemAttribute(attribName, typeName);
		ctx.addSystemAttribute(multiple, "list/"+typeName);

		Element entityClass = puElt.addElement("class");
		entityClass.addText(pkg+'.'+typeName);
	}
}

