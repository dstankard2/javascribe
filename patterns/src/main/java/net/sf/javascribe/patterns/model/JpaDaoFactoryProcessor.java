package net.sf.javascribe.patterns.model;

import java.util.List;

import org.apache.log4j.Logger;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.jsom.JavascribeJavaCodeSnippet;
import net.sf.javascribe.langsupport.java.jsom.JavascribeVariableTypeResolver;
import net.sf.javascribe.langsupport.java.jsom.JsomUtils;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.java5.Java5ClassConstructor;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5CompatibleCodeSnippet;
import net.sf.jsom.java5.Java5DeclaredMethod;
import net.sf.jsom.java5.Java5SourceFile;

@Scannable
@Processor
public class JpaDaoFactoryProcessor {

	private static final Logger log = Logger.getLogger(JpaDaoFactoryProcessor.class);

	public static final String DAO_PACKAGE_PROPERTY = "net.sf.javascribe.patterns.model.JpaDaoFactory.daoPackage";

	private ProcessorContext ctx = null;
	private JpaDaoFactory component = null;
	private String modelPkg = null;
	private String daoFactoryClass = null;
	private String pu = null;
	private EntityManagerLocator locatorType = null;
	EntityManagerType entityManagerType = null;
	private Java5SourceFile daoFactory = null;
	private String ref = null;

	@ProcessorMethod(componentClass=JpaDaoFactory.class)
	public void process(JpaDaoFactory comp,ProcessorContext ctx) throws JavascribeException {
		this.ctx = ctx;
		ctx.setLanguageSupport("Java");
		this.component = comp;
		modelPkg = ModelUtils.getDaoPackage(ctx);
		pu = component.getEntityManager();
		daoFactoryClass = pu+"DaoFactory";
		this.ref = component.getRef();

		entityManagerType = (EntityManagerType)ctx.getType(pu);
		locatorType = (EntityManagerLocator)ctx.getType(component.getLocator());

		log.info("Processing JPA Dao Factory '"+daoFactoryClass+"'");

		if (!(entityManagerType instanceof EntityManagerType)) {
			throw new JavascribeException("Could not find entity manager type "+component.getEntityManager());
		}
		if (!(locatorType instanceof EntityManagerLocator)) {
			throw new JavascribeException("Could not find entity locator type "+component.getLocator());
		}

		Java5CompatibleCodeSnippet code = null;
		JpaDaoFactoryType factoryType = null;
		List<String> entityNames = entityManagerType.getEntityNames();

		daoFactory = JsomUtils.createJavaSourceFile(ctx);
		daoFactory.setPackageName(modelPkg);
		daoFactory.getPublicClass().setClassName(daoFactoryClass);
		daoFactory.getPublicClass().addMemberVariable("entityManager", pu, null);

		JsomUtils.addJavaFile(daoFactory, ctx);

		// Add DAO Factory constructor
		JavaCode c = locatorType.getEntityManager("entityManager", new CodeExecutionContext(null, ctx.getTypes()));
		Java5ClassConstructor con = JsomUtils.createConstructor(daoFactory, ctx);
		code = new JavascribeJavaCodeSnippet(c);
		con.setMethodBody(code);
		daoFactory.getPublicClass().addMethod(con);

		// Add getter method for entity manager
		Java5DeclaredMethod method = new Java5DeclaredMethod(new JavascribeVariableTypeResolver(ctx.getTypes()));
		method.setName("getEntityManager");
		method.setType(pu);
		code = new Java5CodeSnippet();
		code.append("return entityManager;");
		method.setMethodBody(code);
		daoFactory.getPublicClass().addMethod(method);

		for(String s : entityNames) {
			processEntity(s,daoFactory);
		}

		factoryType = new JpaDaoFactoryType(ref,pu,modelPkg,daoFactoryClass,entityNames);
		ctx.getTypes().addType(factoryType);
		ctx.addAttribute(ref, pu+"DaoFactory");
	}


	private void processEntity(String entityName,Java5SourceFile daoFactory) throws JavascribeException {
		Java5SourceFile daoFile = null;
		Java5DeclaredMethod method = null;
		String daoClassName = entityName+"Dao";
		Java5CodeSnippet code = null;
		DataAccessJavaServiceObjectType daoType = null;
		String lowerCamelName = JavascribeUtils.getLowerCamelName(entityName);

		try {
			// Create the DAO type
			daoType = new DataAccessJavaServiceObjectType(daoClassName,modelPkg,daoClassName);
			ctx.getTypes().addType(daoType);

			// Create the DAO file.
			daoFile = JsomUtils.createJavaSourceFile(ctx);
			daoFile.getPublicClass().setClassName(daoClassName);
			daoFile.setPackageName(modelPkg);
			JsomUtils.addJavaFile(daoFile, ctx);

			// Constructor
			Java5ClassConstructor con = JsomUtils.createConstructor(daoFile, ctx);
			con.addArg(pu, "_tx");
			code = new Java5CodeSnippet();
			code.append("entityManager = _tx;");
			con.setMethodBody(code);
			daoFile.getPublicClass().addMethod(con);

			// entityManager instance var
			daoFile.getPublicClass().addMemberVariable("entityManager", pu, null);

			// Factory method
			method = new Java5DeclaredMethod(new JavascribeVariableTypeResolver(ctx));
			method.setName("get"+daoClassName);
			method.setType(daoClassName);
			code = new Java5CodeSnippet();
			code.append("return new "+daoClassName+"(entityManager);");
			method.setMethodBody(code);
			daoFactory.getPublicClass().addMethod(method);

			// Insert method
			method = new Java5DeclaredMethod(new JavascribeVariableTypeResolver(ctx));
			method.addArg(entityName, lowerCamelName);
			method.setName("insert"+entityName);
			code = new Java5CodeSnippet();
			code.append("entityManager.persist("+lowerCamelName+");");
			method.setMethodBody(code);
			daoFile.getPublicClass().addMethod(method);
			daoType.addMethod(JsomUtils.createJavaOperation(method));

			// Select method
			method = new Java5DeclaredMethod(new JavascribeVariableTypeResolver(ctx));
			method.addArg("integer", lowerCamelName+"Id");
			method.setName("get"+entityName);
			method.setType(entityName);
			code = new Java5CodeSnippet();
			code.append("return entityManager.find("+entityName+".class,"+lowerCamelName+"Id);");
			method.setMethodBody(code);
			daoFile.getPublicClass().addMethod(method);
			daoType.addMethod(JsomUtils.createJavaOperation(method));

			// Persist method
			method = new Java5DeclaredMethod(new JavascribeVariableTypeResolver(ctx));
			method.addArg(entityName, lowerCamelName);
			method.setName("save"+entityName);
			code = new Java5CodeSnippet();
			code.append("entityManager.persist("+lowerCamelName+");");
			method.setMethodBody(code);
			daoFile.getPublicClass().addMethod(method);
			daoType.addMethod(JsomUtils.createJavaOperation(method));

			// Delete method
			method = new Java5DeclaredMethod(new JavascribeVariableTypeResolver(ctx));
			method.addArg(entityName, lowerCamelName);
			method.setName("delete"+entityName);
			code = new Java5CodeSnippet();
			code.append("entityManager.remove("+lowerCamelName+");");
			method.setMethodBody(code);
			daoFile.getPublicClass().addMethod(method);
			daoType.addMethod(JsomUtils.createJavaOperation(method));

			// Merge method
			method = new Java5DeclaredMethod(new JavascribeVariableTypeResolver(ctx));
			method.addArg(entityName, lowerCamelName);
			method.setName("merge"+entityName);
			code = new Java5CodeSnippet();
			code.append("entityManager.merge("+lowerCamelName+");");
			method.setMethodBody(code);
			daoFile.getPublicClass().addMethod(method);
			daoType.addMethod(JsomUtils.createJavaOperation(method));
		} catch(CodeGenerationException e) {
			throw new JavascribeException("JSOM exception while processing component",e);
		}
	}

}

