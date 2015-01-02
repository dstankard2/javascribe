package net.sf.javascribe.patterns.js.page;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.VariableType;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.java.JavaBeanType;
import net.sf.javascribe.langsupport.javascript.JavascriptDataObject;
import net.sf.javascribe.langsupport.javascript.JavascriptFunction;
import net.sf.javascribe.langsupport.javascript.JavascriptSourceFile;
import net.sf.javascribe.langsupport.javascript.JavascriptUtils;
import net.sf.javascribe.patterns.js.page.elements.BinderUtils;
import net.sf.javascribe.patterns.servlet.SingleUrlService;
import net.sf.javascribe.patterns.servlet.UrlWebServiceType;

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
		JavascriptSourceFile src = JavascriptUtils.getSourceFile(ctx);
		StringBuilder code = src.getSource();
		
		PageType pageType = PageUtils.getPageType(ctx, pageName);
		
		if (pageType==null) {
			throw new JavascribeException("There is no Page called '"+pageName+"'");
		}

		PageUtils.ensureModel(ctx, pageType);
		PageModelType modelType = PageUtils.getModelType(ctx, pageName);

//		HashMap<String,String> modelAttributes = PageUtils.getModelAttributes(ctx, pageName);
		
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
				PageModelProcessor.addModelAttribute(modelType, att, type, code, null, pageName);
			/*
			if (type!=null) {
				String addType = null;
				if (type.startsWith("list/")) addType = type.substring(5);
				else addType = type;
				VariableType v = ctx.getType(addType);
				if ((v instanceof JavaBeanType) && (!javaBeansToConvert.contains(v)))
					javaBeansToConvert.add((JavaBeanType)v);
				if (modelType.getAttributeType(att)==null) {
					PageModelProcessor.addModelAttribute(modelType,att,type,code,null,pageName);
				}
			}
			*/
		}

		ctx.setLanguageSupport("Javascript");
		for(JavaBeanType t : javaBeansToConvert) {
			if (ctx.getType(t.getName())==null) {
				JavascriptDataObject type = JavascriptUtils.makeDataObject(t);
				ctx.getTypes().addType(type);
			}
		}
		
		StringBuilder funcBody = new StringBuilder();
		StringBuilder funcDec = new StringBuilder();
		StringBuilder url = new StringBuilder();
		StringBuilder dataString = new StringBuilder();
		StringBuilder successFunc = new StringBuilder();
		
		funcDec.append("function (");
		funcBody.append("$.ajax({\ntraditional:true,\ncontext:document,\ndataType:'json',\n");
		
		// For query parameters and path parameters, look for them in the model object.  If they 
		// are not there, add them as parameters to the function.
		
		dataString.append("{");
		boolean firstData = true;
		boolean firstParam = true;
		// First query parameters
		List<String> fnParams = new ArrayList<String>();
		for(String p : srv.getQueryParams()) {
			String modelAttrib = null;
			if (!firstData) dataString.append(',');
			else firstData = false;

			if (modelType.getAttributeType(p)!=null) modelAttrib = p;
			else {
				// Try to find parameter in attribute holders in model?
/*
				for(String name : modelType.getAttributeNames()) {
					String type = ctx.getAttributeType(name);
					if (type==null) {
						throw new JavascribeException("Could find a type for model attribute '"+name+"'");
					}
					VariableType t = ctx.getType(type);
					if (t instanceof JavascriptVariableTypeImpl) continue;
					if (!(t instanceof AttributeHolder)) continue;
					AttributeHolder h = (AttributeHolder)t;
					if (h.getAttributeType(p)!=null) {
						modelAttrib = name+'.'+p;
						break;
					}
				}
				*/
			}
			

			dataString.append(p+":");
			if (modelAttrib==null) {
				if (firstParam) firstParam = false;
				else funcDec.append(',');
				funcDec.append(p);
				fnParams.add(p);
				dataString.append(p);
			} else {
				dataString.append("this.model."+BinderUtils.getGetter(modelAttrib));
			}
		}
		dataString.append('}');
		url.append(srv.getPath().substring(1));
		// Add URL parameters

		funcDec.append(") {\n");

		// Build success function
		successFunc.append("function success(data) {\n");

		for(String n : serviceResultType.getAttributeNames()) {
			successFunc.append("this.model.set"+Character.toUpperCase(n.charAt(0))+n.substring(1)+"(data."+n+");\n");
		}
		if ((comp.getCompleteEvent()!=null) && (comp.getCompleteEvent().trim().length()>0)) {
			successFunc.append(pageName+".controller.dispatch(\""+comp.getCompleteEvent()+"\");\n");
		}
		successFunc.append("}.bind("+comp.getPageName()+")\n");

		if (comp.getFn()==null) {
			throw new JavascribeException("WsClient component requires attribute 'fn'");
		}
		JavascriptFunction fn = new JavascriptFunction(pageName,comp.getFn());
		for(String p : fnParams) {
			String type = ctx.getAttributeType(p);
			fn.addParam(p, type);
		}
		fn.setReturnValue(true);
		pageType.addOperation(fn);
		code.append(comp.getPageName()+"."+comp.getFn()+" = ");
		// Append code to the javascript file

		code.append(funcDec.toString());
		code.append(funcBody.toString());
		code.append("success:"+successFunc.toString()+",\n");
		code.append("data:"+dataString.toString()+",\n");
		code.append("url:'"+url.toString()+"'\n");
		code.append("});\n");
		code.append("}.bind("+comp.getPageName()+");\n");
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

