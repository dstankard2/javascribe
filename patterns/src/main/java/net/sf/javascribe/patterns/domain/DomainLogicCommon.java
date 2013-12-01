package net.sf.javascribe.patterns.domain;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.Attribute;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.LocatedJavaServiceObjectType;
import net.sf.javascribe.langsupport.java.jsom.JavascribeVariableTypeResolver;
import net.sf.javascribe.langsupport.java.jsom.JsomUtils;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.java5.Java5ClassConstructor;
import net.sf.jsom.java5.Java5ClassDefinition;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5CompatibleCodeSnippet;
import net.sf.jsom.java5.Java5DeclaredMethod;
import net.sf.jsom.java5.Java5MethodSignature;
import net.sf.jsom.java5.Java5SourceFile;


public class DomainLogicCommon {

	public static final String DOMAIN_LOGIC_PKG = "net.sf.javascribe.patterns.domain.RetrieveDataRule.pkg";
	public static final String DOMAIN_LOGIC_SERVICE_OBJ = "net.sf.javascribe.patterns.domain.RetrieveDataRule.serviceObj";
	public static final String DOMAIN_LOGIC_LOCATOR_CLASS = "net.sf.javascribe.patterns.domain.RetrieveDataRule.locatorClass";

	public static String getServiceLocatorName(DomainLogicComponent comp,ProcessorContext ctx) throws JavascribeException {
		// Read service locator name
		if (comp.getServiceLocator().trim().length()>0) {
			return comp.getServiceLocator();
		} else if (ctx.getProperty(DOMAIN_LOGIC_LOCATOR_CLASS)!=null) {
			return ctx.getProperty(DOMAIN_LOGIC_LOCATOR_CLASS);
		} else {
			throw new JavascribeException("Service Locator Name must be specified in the component or in property '"+DomainLogicCommon.DOMAIN_LOGIC_LOCATOR_CLASS+"'");
		}
	}

	public static List<Attribute> getParams(DomainLogicComponent comp,ProcessorContext ctx) throws JavascribeException {
		List<Attribute> ret = null;

		// Read rule parameters
		String paramString = comp.getParams();
		if (paramString.trim().length()>0) {
			ret = JavascribeUtils.readAttributes(ctx, paramString);
		} else {
			ret = new ArrayList<Attribute>();
		}

		return ret;
	}

	public static String getRule(DomainLogicComponent comp,ProcessorContext ctx) throws JavascribeException {
		String ret = null;
		ret = comp.getRule();
		if (ret.trim().length()==0) {
			throw new JavascribeException("Attribute 'ruleName' is required on component Retrieve Data Rule");
		}

		return ret;
	}

	public static String getServiceObj(DomainLogicComponent comp,ProcessorContext ctx) throws JavascribeException {
		String ret = null;
		if (comp.getServiceObj().trim().length()>0) {
			ret = comp.getServiceObj();
		} else if (ctx.getProperty(DOMAIN_LOGIC_SERVICE_OBJ)!=null) {
			ret = ctx.getProperty(DOMAIN_LOGIC_SERVICE_OBJ);
		} else {
			throw new JavascribeException("Service Object Name must be specified in the component or in property '"+DomainLogicCommon.DOMAIN_LOGIC_SERVICE_OBJ+"'");
		}

		return ret;
	}

	public static Java5SourceFile getServiceFile(String serviceObj,Java5SourceFile locatorFile,DomainServiceLocatorType locatorType,ProcessorContext ctx) throws JavascribeException {
		Java5SourceFile ret = null;
		String pkg = JavaUtils.findPackageName(ctx, ctx.getRequiredProperty(DomainLogicCommon.DOMAIN_LOGIC_PKG));

		ensureService(serviceObj,locatorFile,locatorType,ctx);
		ret = JsomUtils.getJavaFile(pkg+'.'+serviceObj, ctx);

		return ret;
	}

	public static LocatedJavaServiceObjectType getServiceType(String serviceObj,Java5SourceFile locatorFile,DomainServiceLocatorType locatorType,ProcessorContext ctx) throws JavascribeException {
		LocatedJavaServiceObjectType ret = null;

		ensureService(serviceObj,locatorFile,locatorType,ctx);
		ret = (LocatedJavaServiceObjectType)ctx.getType(serviceObj);

		return ret;
	}

	private static void ensureService(String serviceObj,Java5SourceFile locatorFile,DomainServiceLocatorType locatorType,ProcessorContext ctx) throws JavascribeException {
		String pkg = JavaUtils.findPackageName(ctx, ctx.getRequiredProperty(DomainLogicCommon.DOMAIN_LOGIC_PKG));
		Java5SourceFile serviceFile = null;
		LocatedJavaServiceObjectType serviceType = null;
		Java5ClassDefinition serviceClass = null;
		Java5ClassDefinition locatorClass = locatorFile.getPublicClass();

		serviceFile = JsomUtils.getJavaFile(pkg+'.'+serviceObj, ctx);
		if (serviceFile!=null) return;

		// Add service file+type, create method on service locator
		serviceFile = new Java5SourceFile(new JavascribeVariableTypeResolver(ctx));
		serviceFile.setPackageName(pkg);
		serviceClass = serviceFile.getPublicClass();
		serviceClass.setClassName(serviceObj);
		JsomUtils.addJavaFile(serviceFile, ctx);
		serviceType = new LocatedJavaServiceObjectType(pkg+'.'+locatorClass.getClassName(),serviceObj,pkg,serviceObj);
		ctx.getTypes().addType(serviceType);
		serviceClass = serviceFile.getPublicClass();

		// Add locator method for service
		try {
			Java5DeclaredMethod locatorMethod = null;
			Java5CompatibleCodeSnippet locatorCode = null;
			locatorMethod = new Java5DeclaredMethod(new JavascribeVariableTypeResolver(ctx));
			locatorMethod.setStatic(true);
			locatorMethod.setMethodName("get"+serviceObj);
			locatorMethod.setReturnType(serviceObj);
			locatorClass.addMethod(locatorMethod);
			locatorCode = new Java5CodeSnippet();
			locatorMethod.setMethodBody(locatorCode);
			locatorCode.merge(JsomUtils.toJsomCode(serviceType.declare("_service")));
			locatorCode.merge(JsomUtils.toJsomCode(serviceType.instantiate("_service", null)));
			locatorCode.append("return _service;\n");
		} catch(CodeGenerationException e) {
			throw new JavascribeException("JSOM exception while building service",e);
		}

	}

	public static Java5SourceFile getServiceLocatorFile(String serviceLocator,ProcessorContext ctx) throws JavascribeException {
		String pkg = JavaUtils.findPackageName(ctx, ctx.getRequiredProperty(DOMAIN_LOGIC_PKG));

		Java5SourceFile ret = null;

		// Ensure service locator exists
		ret = JsomUtils.getJavaFile(pkg+'.'+serviceLocator, ctx);
		if (ret==null) {
			ret = new Java5SourceFile(new JavascribeVariableTypeResolver(ctx));
			ret.setPackageName(pkg);
			ret.getPublicClass().setClassName(serviceLocator);
			JsomUtils.addJavaFile(ret, ctx);
		}

		return ret;
	}

	public static DomainServiceLocatorType getServiceLocatorType(String serviceLocator,ProcessorContext ctx) throws JavascribeException {
		DomainServiceLocatorType ret = null;
		String pkg = JavaUtils.findPackageName(ctx, ctx.getRequiredProperty(DOMAIN_LOGIC_PKG));

		ret = (DomainServiceLocatorType)ctx.getType(serviceLocator);
		if (ret==null) {
			ret = new DomainServiceLocatorType(serviceLocator,pkg,serviceLocator);
			ctx.getTypes().addType(ret);
		}

		return ret;
	}
	
	public static Java5ClassConstructor getDefaultConstructor(Java5ClassDefinition cl) {
		Java5ClassConstructor ret = null;
		
		List<String> methodNames = cl.getMethodNames();
		for(String s : methodNames) {
			Java5MethodSignature method = cl.getDeclaredMethod(s);
			if (!(method instanceof Java5ClassConstructor)) continue;
			if (method.getArgNames().size()==0) {
				return (Java5ClassConstructor)method;
			}
		}
		
		if (ret==null) {
			ret = new Java5ClassConstructor(cl.getTypes(),cl.getClassName());
			cl.addMethod(ret);
		}
		
		return ret;
	}

}

