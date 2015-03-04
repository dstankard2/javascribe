package net.sf.javascribe.patterns.domain;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.Attribute;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.api.config.Property;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.LocatedJavaServiceObjectType;
import net.sf.javascribe.langsupport.java.jsom.JavascribeVariableTypeResolver;
import net.sf.javascribe.langsupport.java.jsom.JsomUtils;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.java5.Java5ClassDefinition;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5DeclaredMethod;

/**
 * There are several types of domain-level patterns. The pattern
 * 
 * @author DCS
 */
public class DomainLogicCommon {

	/*
	 * Constants required by domain logic patterns.
	 */
	public static final String DOMAIN_LOGIC_IMPLEMENTATION_PREFIX = "net.sf.javascribe.patterns.domain.impl.";
	public static final String DOMAIN_LOGIC_DEPENDENCIES = "net.sf.javascribe.patterns.domain.dependencies";
	public static final String DOMAIN_LOGIC_PKG = "net.sf.javascribe.patterns.domain.pkg";
	public static final String DOMAIN_LOGIC_SERVICE_OBJ = "net.sf.javascribe.patterns.domain.serviceObj";
	public static final String DOMAIN_LOGIC_LOCATOR_CLASS = "net.sf.javascribe.patterns.domain.locatorClass";

	public static final String OBJ_DOMAIN_LOGIC_OBJECT_NAMES = "net.sf.javascribe.patterns.domain.serviceNames";
	public static final String DOMAIN_LOGIC_FINALIZER_ENSURED = "net.sf.javascribe.patterns.domain.finalizerEnsured";

	public static void ensureFinalizer(ProcessorContext ctx)
			throws JavascribeException {
		String val = (String) ctx.getObject(DOMAIN_LOGIC_FINALIZER_ENSURED);
		if (val == null) {
			ComponentBase comp = new DomainLogicFinalComponent();
			comp.getProperty().add(
					new Property(DOMAIN_LOGIC_LOCATOR_CLASS, ctx
							.getProperty(DOMAIN_LOGIC_LOCATOR_CLASS)));
			ctx.addComponent(comp);
			ctx.putObject(DOMAIN_LOGIC_FINALIZER_ENSURED,
					ctx.getProperty(DOMAIN_LOGIC_LOCATOR_CLASS));
		} else if (!val.equals(ctx.getProperty(DOMAIN_LOGIC_LOCATOR_CLASS))) {
			throw new JavascribeException(
					"Found inconsistent values for domain logic locator class");
		}
	}

	public static List<String> getDomainLogicObjectNames(ProcessorContext ctx) {
		List<String> ret = (List<String>) ctx
				.getObject(OBJ_DOMAIN_LOGIC_OBJECT_NAMES);
		return ret;
	}

	public static String getServiceLocatorName(ProcessorContext ctx)
			throws JavascribeException {
		return ctx.getRequiredProperty(DOMAIN_LOGIC_LOCATOR_CLASS);
	}

	public static List<Attribute> getParams(DomainLogicComponent comp,
			ProcessorContext ctx) throws JavascribeException {
		List<Attribute> ret = null;

		// Read rule parameters
		String paramString = comp.getParams();
		if (paramString.trim().length() > 0) {
			ret = JavascribeUtils.readAttributes(ctx, paramString);
		} else {
			ret = new ArrayList<Attribute>();
		}

		return ret;
	}

	public static String getServiceObj(DomainLogicComponent comp,
			ProcessorContext ctx) throws JavascribeException {
		String ret = null;
		if (comp.getServiceObj().trim().length() > 0) {
			ret = comp.getServiceObj();
		} else if (ctx.getProperty(DOMAIN_LOGIC_SERVICE_OBJ) != null) {
			ret = ctx.getProperty(DOMAIN_LOGIC_SERVICE_OBJ);
		} else {
			throw new JavascribeException(
					"Attribute ServiceObj Name must be specified in the component or in property '"
							+ DOMAIN_LOGIC_SERVICE_OBJ + "'");
		}

		return ret;
	}

	public static LocatedJavaServiceObjectType getDomainObjectType(
			String serviceObj, ProcessorContext ctx) throws JavascribeException {
		String pkg = JavaUtils.findPackageName(ctx,
				ctx.getRequiredProperty(DomainLogicCommon.DOMAIN_LOGIC_PKG));
		LocatedJavaServiceObjectType serviceType = null;

		serviceType = (LocatedJavaServiceObjectType) ctx.getType(serviceObj);
		if (serviceType != null) {
			return serviceType;
		}

		String locatorName = getServiceLocatorName(ctx);
		serviceType = new LocatedJavaServiceObjectType(pkg + '.' + locatorName,
				serviceObj, pkg, serviceObj);
		ctx.getTypes().addType(serviceType);
		return serviceType;
	}

	public static String getDomainLogicPkg(ProcessorContext ctx)
			throws JavascribeException {
		return JavaUtils.findPackageName(ctx,
				ctx.getRequiredProperty(DomainLogicCommon.DOMAIN_LOGIC_PKG));
	}

	public static DomainLogicFile getDomainObjectFile(String serviceObj,
			ProcessorContext ctx) throws JavascribeException {
		String pkg = getDomainLogicPkg(ctx);
		DomainLogicFile serviceFile = null;
		Java5ClassDefinition serviceClass = null;

		serviceFile = (DomainLogicFile) JsomUtils.getJavaFile(pkg + '.'
				+ serviceObj, ctx);
		if (serviceFile != null)
			return serviceFile;

		// Add service file
		serviceFile = new DomainLogicFile(new JavascribeVariableTypeResolver(
				ctx));
		serviceFile.setPackageName(pkg);
		serviceClass = serviceFile.getPublicClass();
		serviceClass.setClassName(serviceObj);
		JsomUtils.addJavaFile(serviceFile, ctx);
		serviceClass = serviceFile.getPublicClass();

		List<String> services = (List<String>) ctx
				.getObject(OBJ_DOMAIN_LOGIC_OBJECT_NAMES);
		if (services == null) {
			services = new ArrayList<String>();
			ctx.putObject(OBJ_DOMAIN_LOGIC_OBJECT_NAMES, services);
		}
		services.add(serviceObj);

		return serviceFile;
	}

	public static DomainServiceLocatorType getServiceLocatorType(
			String serviceLocator, ProcessorContext ctx)
			throws JavascribeException {
		DomainServiceLocatorType ret = null;
		String pkg = JavaUtils.findPackageName(ctx,
				ctx.getRequiredProperty(DOMAIN_LOGIC_PKG));

		ret = (DomainServiceLocatorType) ctx.getType(serviceLocator);
		if (ret == null) {
			ret = new DomainServiceLocatorType(serviceLocator, pkg,
					serviceLocator);
			ctx.getTypes().addType(ret);
		}

		return ret;
	}

	public static void addDependency(String name,LocatedJavaServiceObjectType type,
			DomainLogicFile src,ProcessorContext ctx) throws JavascribeException {
		try {
			if (type.getDependancyNames().contains(name)) return;
			type.addDependancy(name);
			if (!src.getDependencies().contains(name))
			src.getDependencies().add(name);
			String upperCamel = JavascribeUtils.getUpperCamelName(name);
			String typeName = ctx.getAttributeType(name);
			Java5DeclaredMethod setter = JsomUtils.createMethod(ctx);
			setter.setName("set" + upperCamel);
			setter.addArg(typeName, "_d");
			src.getPublicClass().addMemberVariable(name, typeName, null);
			Java5CodeSnippet code = new Java5CodeSnippet();
			setter.setMethodBody(code);
			code.append("this." + name + " = _d;");
			src.getPublicClass().addMethod(setter);
		} catch (CodeGenerationException e) {
			throw new JavascribeException("Couldn't create dependency '" + name
					+ "' for service object '" + type.getName() + "'", e);
		}
	}

}
