package net.sf.javascribe.patterns.domain;

import java.util.List;

import net.sf.javascribe.api.Attribute;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;

import org.apache.log4j.Logger;

//@Scannable
@Processor
public class UpdateEntityRuleProcessor {

	private static final Logger log = Logger.getLogger(UpdateEntityRuleProcessor.class);

	@ProcessorMethod(componentClass=UpdateEntityRule.class)
	public void processUpdateEntityRule(UpdateEntityRule comp,ProcessorContext ctx) throws JavascribeException {

		// Set language to Java
		ctx.setLanguageSupport("Java");

		// Read service locator name
		String serviceLocatorName = DomainLogicCommon.getServiceLocatorName(ctx);

		// Read business object name
		String serviceObjName = DomainLogicCommon.getServiceObj(comp, ctx);

		// Read rule name
		String ruleName = comp.getRule();

		log.info("Processing update entity rule '"+serviceObjName+"."+ruleName+"'");

		// Read rule parameters
		List<Attribute> params = DomainLogicCommon.getParams(comp, ctx);

		// Read entity
		if (JavascribeUtils.isEmpty(comp.getEntity())) {
			throw new JavascribeException("Attribute 'entity' is required");
		}
		String entity = comp.getEntity();

		/*
		// Read selectBy
		if (JavascribeUtils.isEmpty(comp.getSelectBy())) {
			throw new JavascribeException("Attribute 'entity' is required");
		}
		String selectBy = comp.getEntity();

		// Read daoFactoryRef
		if (JavascribeUtils.isEmpty(comp.getDaoFactoryRef())) {
			throw new JavascribeException("Attribute 'daoFactoryRef' is required");
		}
		String daoFactoryRef = comp.getEntity();

		Java5SourceFile locatorFile = DomainLogicCommon.getServiceLocatorFile(serviceLocatorName, ctx);
		DomainServiceLocatorType locatorType = DomainLogicCommon.getServiceLocatorType(serviceLocatorName, ctx);
		Java5SourceFile serviceFile = DomainLogicCommon.getServiceFile(serviceObjName, locatorFile, locatorType, ctx);
		LocatedJavaServiceObjectType serviceType = DomainLogicCommon.getServiceType(serviceObjName, locatorFile, locatorType, ctx);

		// Ensure that the service file has a constructor
		Java5ClassDefinition serviceClass = serviceFile.getPublicClass();
		Java5ClassConstructor con = DomainLogicCommon.getDefaultConstructor(serviceClass);

		// Get type for the DAO Factory
		String factoryTypeName = ctx.getAttributeType(daoFactoryRef);
		if (factoryTypeName==null) {
			throw new JavascribeException("DAO Factory Ref '"+daoFactoryRef+"' is not a valid reference");
		}
		VariableType type = ctx.getType(factoryTypeName);
		if ((type==null) || (!(type instanceof JpaDaoFactoryType))) 
			throw new JavascribeException("Could not find DAO Factory type '"+factoryTypeName+"'");
		JpaDaoFactoryType factoryType = (JpaDaoFactoryType)type;

		// Ensure that the DAO Factory Ref is a dependency of the service
		try {
			if (!serviceType.getDependancyNames().contains(daoFactoryRef)) {
				serviceType.addDependancy(daoFactoryRef);
				serviceClass.addMemberVariable(daoFactoryRef, factoryTypeName, null);
				Java5CompatibleCodeSnippet code = con.getMethodBody();
				JsomUtils.merge(code, factoryType.getInstance(daoFactoryRef, null));
			}
			
			// Create the rule method, add it to the class
			Java5DeclaredMethod method = new Java5DeclaredMethod(serviceClass.getTypes());
			Java5CodeSnippet code = new Java5CodeSnippet();
			method.setMethodBody(code);
			method.setAccessLevel("public");
			method.setMethodName(comp.getRule());
			
		} catch(CodeGenerationException e) {
			throw new JavascribeException("JSOM exception while building business object.",e);
		}
		
		*/
	}

}

