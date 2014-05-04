package net.sf.javascribe.patterns.model;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.Attribute;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.java.JavaServiceObjectType;
import net.sf.javascribe.langsupport.java.jsom.JavascribeVariableTypeResolver;
import net.sf.javascribe.langsupport.java.jsom.JsomUtils;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5DeclaredMethod;
import net.sf.jsom.java5.Java5SourceFile;

import org.apache.log4j.Logger;

@Scannable
@Processor
public class JpaEntityIndexProcessor {

	private static final Logger log = Logger.getLogger(JpaEntityIndexProcessor.class);
	
	public static final String DAO_PACKAGE_PROPERTY = "net.sf.javascribe.patterns.model.JpaDaoFactory.pkg";

	@ProcessorMethod(componentClass=JpaEntityIndex.class)
	public void process(JpaEntityIndex comp,ProcessorContext ctx) throws JavascribeException {
		Java5SourceFile daoFile = null;
		JavaServiceObjectType daoType = null;
		EntityManagerType emType = null;
		
		ctx.setLanguageSupport("Java");
		
		String entityName = null;
		entityName = comp.getEntity();

		if (comp.getName().trim().length()==0) {
			throw new JavascribeException("Found entity index with no name");
		}
		if (comp.getEntityManager().trim().length()==0) {
			throw new JavascribeException("Found entity index '"+comp.getName()+"' with no entityManager");
		}
		if (entityName.trim().length()==0) {
			throw new JavascribeException("Found entity index '"+comp.getName()+"' with no name");
		}
		
		log.info("Processing JPA index "+entityName+"."+comp.getName());
		emType = (EntityManagerType)ctx.getTypes().getType(comp.getEntityManager());
		if (emType==null) {
			throw new JavascribeException("Could not find Entity Manager '"+comp.getEntityManager()+"'");
		}
		if (!emType.getEntityNames().contains(entityName)) {
			throw new JavascribeException("Entity Manager '"+comp.getEntityManager()+"' does not contain entity '"+entityName+"'");
		}
		
		daoType = (JavaServiceObjectType)ctx.getTypes().getType(entityName+"Dao");
		daoFile = JsomUtils.getJavaFile(daoType.getImport(), ctx);

		String selectMethodName = "select"+comp.getName();
//		String deleteMethodName = "delete"+index.getName();

		// Add select method to both DAO file and type
		boolean multiple = false;
		if (comp.getMultiple().equalsIgnoreCase("true")) {
			multiple = true;
		} else if (comp.getMultiple().equalsIgnoreCase("false")) {
			multiple = false;
		} else {
			throw new JavascribeException("An entity index must have multiple as 'true' or 'false'");
		}
		
		
		Java5DeclaredMethod method = new Java5DeclaredMethod(new JavascribeVariableTypeResolver(ctx));

		// Set method name and return type
		method.setName(selectMethodName);
		if (multiple) {
			method.setType("list/"+entityName);
		} else if (!multiple) {
			method.setType(entityName);
		}

		// Read parameters, add them to select method (code and type operation).
		List<Attribute> params = new ArrayList<Attribute>();
		if ((comp.getParams()!=null) && (comp.getParams().length()>0)) {
			params = JavascribeUtils.readAttributes(ctx, comp.getParams());
		}
		for(Attribute a : params) {
			try {
				method.addArg(a.getType(), a.getName());
			} catch(CodeGenerationException e) {
				throw new JavascribeException("JSOM exception while processing component",e);
			}
		}
		
		// Generate code for select
		Java5CodeSnippet code = new Java5CodeSnippet();
		code.addImport("javax.persistence.Query");
		String queryString = "select _entity from "+entityName+" as _entity";
		if ((comp.getIndexString()!=null) && (comp.getIndexString().length()>0)) {
			queryString = queryString + " where " + comp.getIndexString();
		}

		code.append("Query _query = entityManager.createQuery(\""+queryString+"\");\n");

		for(Attribute a : params) {
			code.append("_query.setParameter(\""+a.getName()+"\","+a.getName()+");\n");
		}
		
		if (multiple) {
			code.append("List<"+entityName+"> _ret = _query.getResultList();\n");
		} else {
			code.append(entityName+" _ret = null;\n");
			code.append("try {\n");
			code.append("_ret = ("+entityName+")_query.getSingleResult();\n");
			code.append("} catch(javax.persistence.NoResultException e) { }\n");
		}
		code.append("return _ret;");
		method.setMethodBody(code);

		// Add select to source file and object type.
		daoFile.getPublicClass().addMethod(method);
		daoType.addMethod(JsomUtils.createJavaOperation(method));
	}

}

