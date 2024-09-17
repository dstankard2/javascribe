package net.sf.javascribe.patterns.java.http;

import java.util.List;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.types.JavaVariableType;
import net.sf.javascribe.langsupport.java.types.impl.ExceptionJavaType;
import net.sf.javascribe.langsupport.java.types.impl.JavaVariableTypeImpl;
import net.sf.javascribe.patterns.tomcat.EmbedTomcatRuntimePlatform;
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
		if (ctx.getVariableType("WebSocketSession")==null) {
			ctx.addVariableType(new JavaVariableTypeImpl("WebSocketSession", "Session", "javax.websocket.Session", ctx.getBuildContext()));
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
	
	public static EmbedTomcatRuntimePlatform addWebPlatform(ProcessorContext ctx) throws JavascribeException {
		String name = ctx.getBuildContext().getId()+"JavaWebapp";
		EmbedTomcatRuntimePlatform platform = (EmbedTomcatRuntimePlatform)ctx.getObject(name);
		
		if (platform!=null) {
			throw new JavascribeException("There is already an embedded tomcat component for this build context - can't add another");
		}
		platform = new EmbedTomcatRuntimePlatform();
		ctx.setObject(name, platform);
		return platform;
	}
	
	// Ensure that the specified build ID has a webapp runtime platform
	public static void ensureWebPlatform(ProcessorContext ctx, String buildId) throws JavascribeException {
		String name = buildId+"JavaWebapp";
		
		if (ctx.getObject(name)==null) {
			throw new JavascribeException("There is no embedded tomcat component - can't add or query web platform items");
		}
	}

	public static EmbedTomcatRuntimePlatform getWebPlatform(ProcessorContext ctx) throws JavascribeException {
		String name = ctx.getBuildContext().getId()+"JavaWebapp";
		EmbedTomcatRuntimePlatform platform = (EmbedTomcatRuntimePlatform)ctx.getObject(name);

		if (platform==null) {
			throw new JavascribeException("There is no embedded tomcat component - can't add or query web platform items");
		}
		return platform;
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
		EmbedTomcatRuntimePlatform platform = getWebPlatform(ctx);
		ServletFilterChainDef chainDef = (ServletFilterChainDef)ctx.getObject(objName);

		if (chainDef==null) {
			throw new JavascribeException("Could not find filter chain '"+name+"'");
		}
		
		List<String> filtersCurrentlyMapped = platform.getFiltersForUri(uri);
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
				platform.addFilterMapping(uri, s);
			}
		}
	}

}

