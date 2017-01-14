package net.sf.javascribe.patterns.js.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.VariableType;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.java.JavaBeanType;
import net.sf.javascribe.langsupport.javascript.JavascriptBaseObjectType;
import net.sf.javascribe.langsupport.javascript.JavascriptUtils;
import net.sf.javascribe.patterns.js.page.elements.BinderUtils;
import net.sf.javascribe.patterns.servlet.SingleUrlService;
import net.sf.javascribe.patterns.servlet.UrlWebServiceType;
import net.sf.javascribe.patterns.xml.page.WsClient;

import org.apache.log4j.Logger;

@Scannable
@Processor
public class WsClientProcessor {

	private static final Logger log = Logger.getLogger(WsClientProcessor.class);

	protected List<JavaBeanType> addJavaBeanTypes(String typeName,ProcessorContext ctx) {
		List<JavaBeanType> ret = new ArrayList<JavaBeanType>();
		
		if (typeName.startsWith("list/"))
			typeName = typeName.substring(5);
		VariableType type = ctx.getType(typeName);
		if (type instanceof JavaBeanType) {
			JavaBeanType beanType = (JavaBeanType)type;
			ret.add(beanType);
			for(String name : beanType.getAttributeNames()) {
				String attrType = beanType.getAttributeType(name);
				ret.addAll(addJavaBeanTypes(attrType,ctx));
			}
		}
		
		return ret;
	}
	
	@ProcessorMethod(componentClass=WsClient.class)
	public void process(WsClient comp,ProcessorContext ctx) throws JavascribeException {

		if ((comp.getPageName()==null) || (comp.getPageName().trim().length()==0)) {
			throw new JavascribeException("Found a web service client with no pageName");
		}
		if ((comp.getModule()==null) || (comp.getModule().trim().length()==0)) {
			throw new JavascribeException("Found a web service client with no module");
		}
		if ((comp.getService()==null) || (comp.getService().trim().length()==0)) {
			throw new JavascribeException("Found a web service client with no service");
		}

		String pageName = comp.getPageName();

		ctx.setLanguageSupport("Javascript");
		
		log.info("Processing WSClient on page '"+pageName+"': "+comp.getModule()+"."+comp.getService());

		//StringBuilder code = PageUtils.getInitFunction(ctx, pageName);
		
		PageType pageType = PageUtils.getPageType(ctx, pageName);
		
		if (pageType==null) {
			throw new JavascribeException("There is no Page called '"+pageName+"'");
		}

		PageUtils.ensureModel(ctx, pageType);
		PageModelType modelType = PageUtils.getModelType(ctx, pageName);

		if (modelType==null) {
			throw new JavascribeException("Page '"+pageName+"' is required to have a model.");
		}
		
		ctx.setLanguageSupport("Java");
		SingleUrlService srv = findUrlService(ctx,comp.getModule(),comp.getService());
		if (srv==null) {
			throw new JavascribeException("Couldn't find URL service "+comp.getModule()+'/'+comp.getService());
		}
		
		List<JavaBeanType> javaBeansToConvert = new ArrayList<JavaBeanType>();
		// The service result type doesn't need to be added
		// but all attribute holders it contains must be.
		JavaBeanType serviceResultType = (JavaBeanType)ctx.getType(srv.getReturnType());
		for(String att : serviceResultType.getAttributeNames()) {
			String type = ctx.getAttributeType(att);
			javaBeansToConvert.addAll(addJavaBeanTypes(type,ctx));
			if (modelType.getAttributeType(att)==null)
				PageUtils.addModelAttribute(modelType, att, type, null, pageName, ctx);
		}
		if (!JavascribeUtils.isEmpty(srv.getRequestBody())) {
			String typeName = ctx.getAttributeType(srv.getRequestBody());
			if (typeName.startsWith("list/")) typeName = typeName.substring(5);
			VariableType t = ctx.getType(typeName);
			if (t instanceof JavaBeanType) {
				JavaBeanType b = (JavaBeanType)t;
				javaBeansToConvert.add(b);
			}
		}

		StringBuilder code = PageUtils.getInitFunction(ctx, pageName);

		ctx.setLanguageSupport("Javascript");
		for(JavaBeanType t : javaBeansToConvert) {
			if (ctx.getType(t.getName())==null) {
				JavascriptBaseObjectType type = JavascriptUtils.makeDataObject(t);
				ctx.getTypes().addType(type);
			}
		}

		// Now build the web service client code
		StringBuilder totalBuild = new StringBuilder();
		String ajaxFunc = null;
//		StringBuilder funcBody = new StringBuilder();
		StringBuilder ajaxParam = new StringBuilder();
		StringBuilder funcDec = new StringBuilder();
		StringBuilder dataString = new StringBuilder();
		StringBuilder successFunc = new StringBuilder();
		
		String requestBody = srv.getRequestBody();
		List<String> params = new ArrayList<String>();
		
		funcDec.append("function(");
		
		params.addAll(srv.getQueryParams());
		if (requestBody.trim().length()>0) {
			params.add(requestBody);
		}
		
		boolean firstParam = true;
		HashMap<String,String> paramRefs = new HashMap<String,String>();
		for(String p : params) {
			if (modelType.getAttributeType(p)!=null) {
				paramRefs.put(p,"this.model."+BinderUtils.getGetter(p));
			} else {
				if (!firstParam) funcDec.append(',');
				else firstParam = false;
				funcDec.append(p);
				paramRefs.put(p, p);
			}
		}
		
		funcDec.append(')');

		// Build JQuery Ajax function object parameter
		
		ajaxParam.append("{\ndataType: 'json',\n");
		
		boolean urlParam = false;
		boolean queryParamsInUrl = false;
		if (srv.getRequestMethod().equals("GET")){
			ajaxFunc = "ajax";
			ajaxParam.append("context: document,\n");
			ajaxParam.append("traditional: true,\n");
			ajaxParam.append("type: 'GET',\n");
			urlParam = true;
//			ajaxParam.append("url: '"+srv.getPath().substring(1)+"',\n");
			dataString.append("data: {\n");
			boolean firstData = true;
			for(String s : srv.getQueryParams()) {
				if (!firstData) dataString.append(",\n");
				else firstData = false;
				dataString.append(s+": "+paramRefs.get(s));
			}
			dataString.append("},\n");
		} else if (srv.getRequestMethod().equals("POST")) {
			ajaxFunc = "ajax";
			urlParam = true;
			queryParamsInUrl = true;
			if (requestBody!=null) {
				dataString.append("data: JSON.stringify("+paramRefs.get(requestBody)+"),\n");
				dataString.append("processData: false,\n");
				dataString.append("contentType: 'application/json',\n");
			}
			ajaxParam.append("type: 'POST',\n");
		} else if (srv.getRequestMethod().equals("PUT")) {
			throw new JavascribeException("Ws Client doesn't support PUT method");
		} else if (srv.getRequestMethod().equals("DELETE")){
			throw new JavascribeException("Ws Client doesn't support DELETE method");
		}

		ajaxParam.append(dataString.toString());
		
		// Build success func
		successFunc.append("success: function(data) {\n");
		for(String n : serviceResultType.getAttributeNames()) {
			successFunc.append("this.model.set"+Character.toUpperCase(n.charAt(0))+n.substring(1)+"(data."+n+");\n");
		}
		if ((comp.getCompleteEvent()!=null) && (comp.getCompleteEvent().trim().length()>0)) {
			successFunc.append("this.eventDispatcher.dispatch(\""+comp.getCompleteEvent()+"\");\n");
		}
		successFunc.append("}.bind("+comp.getPageName()+")\n");
		
		ajaxParam.append(successFunc);
		ajaxParam.append("}");
		
		// Success function

		// Put everything together
		totalBuild.append(pageName+"."+comp.getFn()+" = "+funcDec+" {\n");
		
		totalBuild.append("$."+ajaxFunc+"(");
		if (urlParam) {
			totalBuild.append("'"+srv.getPath().substring(1));
			if (queryParamsInUrl) {
				if (srv.getQueryParams().size()>0) {
					totalBuild.append('?');
					boolean first = true;
					for(String s : srv.getQueryParams()) {
						if (first) first = false;
						else totalBuild.append('&');
						totalBuild.append(s+"='+");
						totalBuild.append(paramRefs.get(s));
						totalBuild.append("+'");
					}
				}
			}
			totalBuild.append("',");
		}
		totalBuild.append(ajaxParam.toString());
		totalBuild.append(")");
		totalBuild.append("};\n");
		code.append(totalBuild);
		
	}

	private SingleUrlService findUrlService(ProcessorContext ctx,String module,String service) throws JavascribeException {
		
		SingleUrlService ret = null;
		UrlWebServiceType srv = null;
		String name = '/'+module+'/'+service;

		if (!(ctx.getType(module) instanceof UrlWebServiceType)) {
			throw new JavascribeException("Could not find web service module '"+module+"' or it is not a web service");
		}
		srv = (UrlWebServiceType)ctx.getType(module);
		ret = srv.getServices().get(name);

		return ret;
	}
	
}

