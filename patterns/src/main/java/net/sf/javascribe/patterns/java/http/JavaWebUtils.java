package net.sf.javascribe.patterns.java.http;

import java.util.List;

import net.sf.javascribe.api.BuildContext;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.RuntimePlatform;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.types.JavaVariableType;
import net.sf.javascribe.langsupport.java.types.impl.ExceptionJavaType;
import net.sf.javascribe.langsupport.java.types.impl.JavaVariableTypeImpl;
import net.sf.javascribe.patterns.web.JavaWebappRuntimePlatform;

public class JavaWebUtils {

	public static boolean isWebLanguage(String lang) {
		if (lang.equals("html"))
			return true;
		if (lang.equals("sass"))
			return true;
		if (lang.equals("javascript"))
			return true;
		if (lang.equals("css"))
			return true;
		return false;
	}

	public static void addServletTypes(ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Java8");
		//ctx.getBuildContext().addDependency("java-servlets");

		JavaVariableType type = null;

		if (ctx.getVariableType("HttpServletRequest")==null) {
			type = new JavaVariableTypeImpl("HttpServletRequest", "javax.servlet.http.HttpServletRequest", null);
			ctx.addVariableType(type);
		}
		if (ctx.getVariableType("HttpServletResponse")==null) {
			type = new JavaVariableTypeImpl("HttpServletResponse", "javax.servlet.http.HttpServletResponse", null);
			ctx.addVariableType(type);
		}
		if (ctx.getVariableType("HttpSession")==null) {
			type = new JavaVariableTypeImpl("HttpSession", "javax.servlet.http.HttpServletResponse", null);
			ctx.addVariableType(type);
		}

		if (ctx.getVariableType("ServletException")==null) {
			type = new ExceptionJavaType("ServletException", "javax.servlet", null);
			ctx.addVariableType(type);
		}
		if (ctx.getVariableType("IOException")==null) {
			type = new ExceptionJavaType("IOException", "java.io", null);
			ctx.addVariableType(type);
		}

		// For generating context listeners
		if (ctx.getVariableType("ServletContextEvent")==null) {
			type = new JavaVariableTypeImpl("ServletContextEvent", "javax.servlet.ServletContextEvent", null);
			ctx.addVariableType(type);
		}
	}
	
	public static JavaWebappRuntimePlatform getWebPlatform(ProcessorContext ctx) throws JavascribeException {
		BuildContext bctx = ctx.getBuildContext();
		RuntimePlatform platform = bctx.getRuntimePlatform();

		if (platform==null) {
			throw new JavascribeException("The current Build Context has no runtime platform defined");
		}
		if (!(platform instanceof JavaWebappRuntimePlatform)) {
			throw new JavascribeException("The runtime platform of the current build context is not a Java Web Application");
		}
		return (JavaWebappRuntimePlatform)platform;
	}

	public static void addServlet(String servletName,String servletClass,ProcessorContext ctx) throws JavascribeException {
		getWebPlatform(ctx).addServlet(servletName, servletClass);
	}

	public static void addServletMapping(String uri,String servletName,ProcessorContext ctx) throws JavascribeException {
		getWebPlatform(ctx).addServletMapping(uri, servletName);
	}
	
	public static void addServletFilterChain(String name,String[] filters,ProcessorContext ctx) throws JavascribeException {
		String objName = "FilterChain_"+name;
		ServletFilterChainDef chainDef = (ServletFilterChainDef)ctx.getObject(objName);

		if (chainDef!=null) {
			throw new JavascribeException("Found duplicate servlet filter chain '"+name+"'");
		}
		chainDef = new ServletFilterChainDef();
		chainDef.setName(name);
		for(String filter : filters) {
			chainDef.getFilterNames().add(filter);
		}
		ctx.setObject(objName, chainDef);
	}
	
	public static void applyServletFilterChain(String uri, String name, ProcessorContext ctx) throws JavascribeException {
		String objName = "FilterChain_"+name;
		RuntimePlatform platform = ctx.getBuildContext().getRuntimePlatform();
		ServletFilterChainDef chainDef = (ServletFilterChainDef)ctx.getObject(objName);

		if (chainDef==null) {
			throw new JavascribeException("Could not find filter chain '"+name+"'");
		}
		if (platform==null) {
			throw new JavascribeException("Cannot apply servlet filter chain since there is no runtime platform in the current build context");
		}
		if (!(platform instanceof JavaWebappRuntimePlatform)) {
			throw new JavascribeException("The runtime platform of the current build context is not a Java Webapp");
		}
		
		JavaWebappRuntimePlatform webapp = (JavaWebappRuntimePlatform)platform;
		List<String> filtersCurrentlyMapped = webapp.getFiltersForUri(uri);
		if (filtersCurrentlyMapped!=null) {
			if (filtersCurrentlyMapped.size()!=chainDef.getFilterNames().size()) {
				throw new JavascribeException("Found conflicting filter mappings for URI '"+uri+"'");
			}
			for(int i=0;i<chainDef.getFilterNames().size();i++) {
				if (!chainDef.getFilterNames().get(i).equals(filtersCurrentlyMapped.get(i))) {
					throw new JavascribeException("Found conflicting filter mappings for URI '"+uri+"'");
				}
			}
		} else {
			for(String s : chainDef.getFilterNames()) {
				webapp.addFilterMapping(uri, s);
			}
		}
	}

}

