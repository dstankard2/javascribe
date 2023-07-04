package net.sf.javascribe.patterns.model;

import java.util.List;

import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.types.ServiceOperation;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaClassSourceFile;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.types.impl.LocatedServiceType;
import net.sf.javascribe.langsupport.java.types.impl.ServiceLocatorImpl;
import net.sf.javascribe.patterns.model.types.EntityManagerType;
import net.sf.javascribe.patterns.xml.model.JpaDaoFactory;

@Plugin
public class JpaDaoFactoryProcessor implements ComponentProcessor<JpaDaoFactory> {

	public void process(JpaDaoFactory comp, ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Java8");
		boolean selectOnIndexes = false;
		boolean deleteOnIndexes = false;

		String s = comp.getSelectByIndex();
		if ((s.equalsIgnoreCase("t")) || (s.equalsIgnoreCase("true"))) {
			selectOnIndexes = true;
		}
		s = comp.getDeleteByIndex();
		if ((s.equalsIgnoreCase("t")) || (s.equalsIgnoreCase("true"))) {
			deleteOnIndexes = true;
		}
		
		String locatorName = comp.getEntityManagerLocator();

		EntityManagerLocator emLocator = JavascribeUtils.getType(EntityManagerLocator.class, locatorName, ctx);
		
		String ref = comp.getRef();
		String pkg = comp.getFullPackage();
		String name = comp.getName();
		EntityManagerType em = emLocator.getEntityManagerType();
		TableSet tableSet = null;
		ServiceLocatorImpl factoryType = new ServiceLocatorImpl(name,name,pkg,ctx.getBuildContext());
		EntityManagerLocator locatorType = JavascribeUtils.getType(EntityManagerLocator.class, locatorName, ctx);

		tableSet = em.getTableSet();
		
		JavaClassSourceFile factory = new JavaClassSourceFile(ctx);
		JavaClassSource factoryClass = factory.getSrc();

		factoryClass.setPackage(pkg);
		factoryClass.setName(name);

		MethodSource<JavaClassSource> constructor = factoryClass.addMethod().setConstructor(true).setPublic();
		StringBuilder constructorCode = new StringBuilder();
		
		MethodSource<JavaClassSource> getEm = factoryClass.addMethod().setPublic().setName("getEntityManager")
				.setReturnType("javax.persistence.EntityManager");
		//getEm.setBody("return null;");
		getEm.setBody("return "+locatorType.getEntityManager(null, null).getCodeText()+";");

		for(TableInfo table : tableSet.getTableInfos()) {
			String daoName = table.getEntityName()+"Dao";
			String daoRef = JavascribeUtils.getLowerCamelName(daoName);
			JavaClassSourceFile daoFile = new JavaClassSourceFile(ctx);
			JavaClassSource daoClass = daoFile.getSrc();
			LocatedServiceType daoType = null;
			String entityName = table.getEntityName();
			String pkField = null;
			String pkFieldType = null;
			CodeExecutionContext execCtx = new CodeExecutionContext(ctx);
			
			for(ColumnInfo col : table.getColumns()) {
				if (col.isPrimaryKey()) {
					pkField = col.getAttributeName();
					pkFieldType = col.getAttributeType();
					break;
				}
			}
			if (pkField==null) {
				throw new JavascribeException("JpaDaoFactory requires all entities to have one primary key field - table '"+table.getTableName()+"' has none");
			}

			daoClass.addImport(locatorType.getImport());
			daoClass.setPackage(pkg);
			daoClass.setName(daoName);
			daoType = new LocatedServiceType(pkg,daoName,ctx.getBuildContext(),pkg+'.'+name,name);
			factoryType.addService(daoName);

			// Get entity manager
			JavaCode getEmAnon = locatorType.getEntityManager(null, execCtx);
			ServiceOperation op = new ServiceOperation("getEntityManager");
			op.returnType(em.getName());
			JavaCode body = new JavaCode("return "+getEmAnon.getCodeText()+";");
			JavaUtils.addServiceOperation(op,body,daoClass, ctx);

			// Select single row
			op = new ServiceOperation("get"+entityName).returnType(entityName).addParam(pkField, pkFieldType);
			body = new JavaCode("return "+getEmAnon.getCodeText()+".find("+entityName+".class, "+pkField+");\n");
			JavaUtils.addServiceOperation(op, body, daoClass, ctx);
			daoType.addOperation(op);

			// Persist a row
			op = new ServiceOperation("persist").addParam("row", entityName);
			body = new JavaCode(getEmAnon.getCodeText()+".persist(row);\n");
			JavaUtils.addServiceOperation(op, body, daoClass, ctx);
			daoType.addOperation(op);

			// Merge a row
			op = new ServiceOperation("merge").addParam("row", entityName);
			body = new JavaCode(getEmAnon.getCodeText()+".merge(row);\n");
			JavaUtils.addServiceOperation(op, body, daoClass, ctx);
			daoType.addOperation(op);
			
			// Delete a row
			op = new ServiceOperation("delete").addParam("row", entityName);
			body = new JavaCode(getEmAnon.getCodeText()+".remove(row);\n");
			JavaUtils.addServiceOperation(op, body, daoClass, ctx);
			daoType.addOperation(op);
			
			List<IndexInfo> indices = table.getIndices();
			for(IndexInfo ind : indices) {
				String indName = JavascribeUtils.getUpperCamelName(ind.getName());
				if (selectOnIndexes) {
					StringBuilder queryString = new StringBuilder();
					op = new ServiceOperation("findBy"+indName);
					body = new JavaCode();
					if (ind.isUnique()) {
						body.appendCodeText("try {");
					}
					body.appendCodeText("return "+getEmAnon.getCodeText()+".createQuery(\"_REPLACE_\","+entityName+".class)");
					queryString.append("from "+entityName);
					boolean first = true;
					for(String col : ind.getColumns()) {
						String type = ctx.getSystemAttribute(col);
						op.addParam(col, type);
						body.appendCodeText(".setParameter(\""+col+"\","+col+")\n");
						if (first) {
							first = false;
							queryString.append(" where "+col+" = :"+col);
						} else {
							queryString.append(" and "+col+" = :"+col);
						}
					}
					if (ind.isUnique()) {
						op.returnType(entityName);
						body.appendCodeText(".getSingleResult();\n");
						body.appendCodeText("} catch(javax.persistence.NoResultException _e) {return null;}");
					} else {
						body.appendCodeText(".getResultList();\n");
						op.returnType("list/"+entityName);
					}
					JavaCode b = new JavaCode(body.getCodeText().replace("_REPLACE_", queryString.toString()));
					JavaUtils.addServiceOperation(op, b, daoClass, ctx);
					daoType.addOperation(op);
				}
				if (deleteOnIndexes) {
					StringBuilder queryString = new StringBuilder();
					op = new ServiceOperation("deleteBy"+indName);
					body = new JavaCode();

					body.appendCodeText(getEmAnon.getCodeText()+".createQuery(\"_REPLACE_\")");
					
					queryString.append("delete from "+entityName);
					boolean first = true;
					for(String col : ind.getColumns()) {
						String type = ctx.getSystemAttribute(col);
						op.addParam(col, type);
						body.appendCodeText(".setParameter(\""+col+"\","+col+")\n");
						if (first) {
							first = false;
							queryString.append(" where "+col+" = :"+col);
						} else {
							queryString.append(" and "+col+" = :"+col);
						}
					}
					body.appendCodeText(".executeUpdate();");
					JavaCode b = new JavaCode(body.getCodeText().replace("_REPLACE_", queryString.toString()));
					JavaUtils.addServiceOperation(op, b, daoClass, ctx);
					daoType.addOperation(op);
				}
			}

			ctx.addSourceFile(daoFile);
			ctx.addVariableType(daoType);
			ctx.addSystemAttribute(daoRef, daoName);
			factoryClass.addField().setName(daoRef).setType(daoType.getClassName()).setPrivate().setLiteralInitializer("null");
			constructorCode.append(daoRef + " = new "+daoName+"();\n");
			factoryClass.addMethod().setName("get"+daoName).setReturnType(daoType.getClassName()).setBody("return "+daoRef+";\n").setPublic();
		}
		
		constructor.setBody(constructorCode.toString());

		ctx.addSourceFile(factory);
		ctx.addVariableType(factoryType);
		ctx.addSystemAttribute(ref, name);
	}
	
}
