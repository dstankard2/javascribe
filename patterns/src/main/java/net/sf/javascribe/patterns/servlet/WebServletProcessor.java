package net.sf.javascribe.patterns.servlet;

import java.util.List;

import org.apache.log4j.Logger;

import net.sf.javascribe.api.Attribute;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.expressions.ExpressionUtil;
import net.sf.javascribe.langsupport.java.JavaBeanType;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaOperation;
import net.sf.javascribe.langsupport.java.JavaServiceObjectType;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.jsom.JavascribeVariableTypeResolver;
import net.sf.javascribe.langsupport.java.jsom.JsomUtils;
import net.sf.javascribe.patterns.xml.servlet.ServletForward;
import net.sf.javascribe.patterns.xml.servlet.WebServlet;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5DeclaredMethod;
import net.sf.jsom.java5.Java5SourceFile;

@Scannable
@Processor
public class WebServletProcessor {

	private static final Logger log = Logger.getLogger(WebServletProcessor.class);

	public static final String WEB_SERVLET_PKG = "net.sf.javascribe.patterns.servlet.WebServlet.pkg";

	@ProcessorMethod(componentClass=WebServlet.class)
	public void process(WebServlet servlet,ProcessorContext ctx) throws JavascribeException {
		String className = null;
		String pkg = null;
		
		try {
			ctx.setLanguageSupport("Java");
			// Set instance variables
			className = servlet.getName();
			log.info("Processing web servlet with path '"+className+"'");

			pkg = JavaUtils.findPackageName(ctx, ctx.getRequiredProperty(WEB_SERVLET_PKG));

			WebUtils.addHttpTypes(ctx);
			modifyWebXml(ctx,servlet,pkg,className);
			createServletClass(ctx,pkg,className,servlet);
		} catch(CodeGenerationException e) {
			throw new JavascribeException("JSOM exception while processing component",e);
		}

	}

	private void createServletClass(ProcessorContext ctx,String pkg,String className,WebServlet servlet) throws JavascribeException,CodeGenerationException {
		Java5SourceFile servletFile = null;
		JavascribeVariableTypeResolver types = new JavascribeVariableTypeResolver(ctx.getTypes());
		Java5DeclaredMethod method = new Java5DeclaredMethod(types);
		servletFile = new Java5SourceFile(types);
		servletFile.setPackageName(pkg);
		servletFile.getPublicClass().setClassName(className);
		JsomUtils.addJavaFile(servletFile, ctx);
		servletFile.addImport("javax.servlet.http.HttpServlet");
		servletFile.getPublicClass().setSuperClass("HttpServlet");

		method.setName("service");
		method.addArg("HttpServletRequest", "request");
		method.addArg("HttpServletResponse", "response");
		method.addThrownException("ServletException");
		method.addThrownException("IOException");
		servletFile.getPublicClass().addMethod(method);

		CodeExecutionContext execCtx = new CodeExecutionContext(null,ctx.getTypes());
		Java5CodeSnippet methodCode = new Java5CodeSnippet();
		method.setMethodBody(methodCode);

		// Handle servlet parameters if they are there
		if (servlet.getParams().trim().length()>0) {
			List<Attribute> attribs = JavascribeUtils.readAttributes(ctx, servlet.getParams());
			for(Attribute attrib : attribs) {
				WebUtils.handleQueryParam(ctx,attrib.getName(),attrib.getType(),methodCode,execCtx);
			}
		}

		// Handle session data object
		if (servlet.getSessionDataType().trim().length()>0) {
			if (!(ctx.getType(servlet.getSessionDataType()) instanceof JavaBeanType)) {
				throw new JavascribeException("Type '"+servlet.getSessionDataType()+"' is not a data object type");
			}
			JavaBeanType type = (JavaBeanType)ctx.getType(servlet.getSessionDataType());
			String varName = JavascribeUtils.getLowerCamelName(servlet.getSessionDataType());
			JsomUtils.merge(methodCode, (JavaCode)type.declare(varName,execCtx));
			methodCode.append("if (request.getSession(false)!=null) {\n");
			methodCode.append(varName+" = ("+type.getClassName()+")request.getSession().getAttribute(\""+varName+"\");\n");
			methodCode.append("}\n");
			execCtx.addVariable(varName, servlet.getSessionDataType());
			methodCode.append("request.setAttribute(\""+varName+"\","+varName+");\n");
		}

		// Handle service call if it is there.
		String resultName = null;
		if (servlet.getService().trim().length()>0) {
			String objName = JavascribeUtils.getObjectName(servlet.getService());
			String ruleName = JavascribeUtils.getRuleName(servlet.getService());
			String objInst = JavascribeUtils.getLowerCamelName(objName);
			JavaBeanType resultType = null;

			JavaServiceObjectType obj = (JavaServiceObjectType)ctx.getType(objName);
			if (obj==null) {
				throw new CodeGenerationException("Couldn't find business object type '"+objName+"'");
			}
			List<JavaOperation> ops = obj.getMethods(ruleName);
			if (ops.size()==0) {
				throw new CodeGenerationException("Couldn't find business rule '"+objName+"."+ruleName+"'");
			} else if (ops.size()>1) {
				throw new JavascribeException("Web Servlet Processor does not support overloaded service methods, as it cannot implicitly determine which method to invoke.");
			}
			JavaOperation op = ops.get(0);
			JsomUtils.merge(methodCode, obj.declare(objInst));
			JsomUtils.merge(methodCode, obj.instantiate(objInst,null));
			resultName = op.getReturnType();
			if (resultName!=null) {
				resultType = (JavaBeanType)ctx.getTypes().getType(resultName);
				JsomUtils.merge(methodCode, (JavaCode)resultType.declare("serviceResult",execCtx));
				execCtx.addVariable("serviceResult", resultName);
				JsomUtils.merge(methodCode,JavaUtils.callJavaOperation("serviceResult", objInst, op, execCtx, null));
				//methodCode.append(JavaUtils.callJavaOperation("serviceResult", objInst, op, execCtx, null));
				List<String> attNames = resultType.getAttributeNames();
				for(String a : attNames) {
					methodCode.append("request.setAttribute(\""+a+"\",");
					methodCode.append(resultType.getCodeToRetrieveAttribute("serviceResult", a, resultType.getAttributeType(a),execCtx));
					methodCode.append(");\n");
				}
			} else {
				JsomUtils.merge(methodCode, JavaUtils.callJavaOperation(null, objInst, op, execCtx, null));
				//methodCode.merge(JavaUtils.callJavaOperation(null, objInst, op, execCtx, null));
			}
		}
		methodCode.append("boolean forwarded = false;\n");
		String defaultForward = null;

		for(ServletForward forward : servlet.getForward()) {
			if (forward.getCondition().equals("success")) {
				methodCode.append("if ((serviceResult.getStatus()==0) && (!forwarded)) {\n");
				methodCode.append("request.getRequestDispatcher(\"/"+forward.getForward()+"\").forward(request,response);\n");
				methodCode.append("forwarded = true;\n}\n");
			}
			else if ((forward.getCondition()==null) || (forward.getCondition().equals("default"))) {
				defaultForward = forward.getForward();
			}
			else if (forward.getCondition().equals("invalid")) {
				methodCode.append("if ((serviceResult.getStatus()==1) && (!forwarded)) {\n");
				methodCode.append("request.getRequestDispatcher(\"/"+forward.getForward()+"\").forward(request,response);\n");
				methodCode.append("forwarded = true;\n}\n");
			}
			else if (forward.getCondition().equals("error")) {
				methodCode.append("if ((serviceResult.getStatus()==2) && (!forwarded)) {\n");
				methodCode.append("request.getRequestDispatcher(\"/"+forward.getForward()+"\").forward(request,response);\n");
				methodCode.append("forwarded = true;\n}\n");
			}
			else {
				methodCode.append("if (("+ExpressionUtil.evaluateBooleanExpression(forward.getCondition(), execCtx)+") && (!forwarded)) {\n");
				methodCode.append("request.getRequestDispatcher(\"/"+forward.getForward()+"\").forward(request,response);\n");
				methodCode.append("forwarded = true;\n}\n");
			}
		}

		if (defaultForward!=null) {
			methodCode.append("if (!forwarded) {\n");
			methodCode.append("request.getRequestDispatcher(\"/"+defaultForward+"\").forward(request,response);\n");
			methodCode.append("forwarded = true;\n}\n");
		}

	}

	private void modifyWebXml(ProcessorContext ctx,WebServlet servlet,String pkg,String className) throws JavascribeException {
		WebXmlFile webXml = null;

		// Add servlet element to web.xml
		webXml = WebUtils.getWebXml(ctx);
		webXml.addServlet(servlet.getName(), servlet.getName(), pkg+'.'+className);

		// Add Servlet Mapping
		webXml.addServletMapping(servlet.getName(), '/'+servlet.getName());

		// Add filter mappings
		String filterString = servlet.getFilters();
		if ((filterString!=null) && (filterString.trim().length()>0)) {
			filterString = filterString.trim();
			String[] filters = filterString.split(",");
			for(String f : filters) {
				webXml.addFilterMapping(f, '/'+servlet.getName());
			}
		}

	}

}

