package net.sf.javascribe.patterns.servlet;

import java.io.File;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.GeneratorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.java.ExceptionType;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaVariableType;
import net.sf.javascribe.langsupport.java.JavaVariableTypeImpl;
import net.sf.javascribe.langsupport.java.jsom.JavascribeJavaCodeSnippet;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.java5.Java5CompatibleCodeSnippet;

public class WebUtils {

	private static String getWebXmlPath(GeneratorContext ctx) {
		return ctx.getBuildRoot()+File.separatorChar+"www"+File.separatorChar+"WEB-INF"+
		File.separatorChar+"web.xml";
	}
	
	public static WebXmlFile getWebXml(GeneratorContext ctx) throws JavascribeException {
		WebXmlFile ret = null;

		ret = getWebXmlSourceFile(ctx);
		
		if (ret==null) {
			ret = new WebXmlFile();
			ret.setPath(getWebXmlPath(ctx));
			ctx.addSourceFile(ret);
		}

		return ret;
	}
	
	public static void addContextListener(GeneratorContext ctx,String className) throws JavascribeException {
		WebXmlFile webXml = getWebXml(ctx);
		webXml.addContextListener(className);
	}
	
	private static WebXmlFile getWebXmlSourceFile(GeneratorContext ctx) {
		WebXmlFile ret = null;
		String path = getWebXmlPath(ctx);
		
		ret = (WebXmlFile)ctx.getSourceFile(path);
		return ret;
	}
	
	public static boolean webXmlHasServlet(GeneratorContext ctx,String servletName) throws JavascribeException {
		WebXmlFile src = getWebXml(ctx);
		
		return src.getServlet(servletName) != null;
	}

/*
	public static Element getServletElement(Document doc,String servletName) {
		Element root = doc.getRootElement();
		List<Element> elts = root.elements();
		
		for(Element elt : elts) {
			if (elt.getName().equals("servlet")) {
				Element name = elt.element("servlet-name");
				if (name==null) continue;
				if (name.getText()==null) continue;
				if (name.getText().equals(servletName)) return elt;
			}
		}
		
		return null;
	}
*/
	
	public static void addHttpTypes(GeneratorContext ctx) throws JavascribeException {
		JavaVariableTypeImpl type = null;
		
		if (ctx.getTypes().getType("HttpServletRequest")==null) {
			type = new JavaVariableTypeImpl("HttpServletRequest","javax.servlet.http.HttpServletRequest","HttpServletRequest");
			ctx.getTypes().addType(type);
		}
		if (ctx.getTypes().getType("HttpServletResponse")==null) {
			type = new JavaVariableTypeImpl("HttpServletResponse","javax.servlet.http.HttpServletResponse","HttpServletResponse");
			ctx.getTypes().addType(type);
		}

		if (ctx.getType("ServletException")==null) {
			ExceptionType exType = new ExceptionType("ServletException","javax.servlet","ServletException");
			ctx.getTypes().addType(exType);
		}
		if (ctx.getType("IOException")==null) {
			ExceptionType exType = new ExceptionType("IOException","java.io","IOException");
			ctx.getTypes().addType(exType);
		}

		// For generating context listeners
		if (ctx.getTypes().getType("ServletContextEvent")==null) {
			type = new JavaVariableTypeImpl("ServletContextEvent","javax.servlet.ServletContextEvent","ServletContextEvent");
			ctx.getTypes().addType(type);
		}
	}

	// Given a variable of type HttpServletRequest named request, read the parameter of the 
	// given name with the given type and in the given executionContext.  Append the code to 
	// the specified code snippet.
	public static void handleQueryParam(GeneratorContext ctx,String name,String typeName,Java5CompatibleCodeSnippet code,CodeExecutionContext execCtx) throws JavascribeException,CodeGenerationException {
		JavaVariableType type = (JavaVariableType)ctx.getType(typeName);

		if (type==null) {
			throw new JavascribeException("Couldn't find type '"+typeName+"' for servlet input param '"+name+"'");
		}
		code.merge(new JavascribeJavaCodeSnippet((JavaCode)type.declare(name,execCtx)));
		
		if (typeName.equals("string")) {
			code.append(name+" = request.getParameter(\""+name+"\");\n");
			code.append("if (("+name+"!=null) && ("+name+".trim().length()==0)) "+name+" = null;\n");
		}
		else if (typeName.equals("integer")) {
			code.append("if (request.getParameter(\""+name+"\")!=null) {\n");
			code.append("try {\n");
			code.append(name+" = Integer.parseInt(request.getParameter(\""+name+"\"));\n");
			code.append("} catch(Exception e) { }\n}\n");
		}
		else {
			// No other parameter types supported yet
			throw new JavascribeException("Found a webServlet input parameter '"+name+"' of unsupported type '"+typeName+"'");
		}
		execCtx.addVariable(name, typeName);
		code.append("request.setAttribute(\"input_"+name+"\","+name+");\n");
	}
	
}
