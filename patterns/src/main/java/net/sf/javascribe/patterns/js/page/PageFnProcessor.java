package net.sf.javascribe.patterns.js.page;

import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.types.ServiceOperation;
import net.sf.javascribe.langsupport.javascript.types.JavascriptType;
import net.sf.javascribe.langsupport.javascript.types.ModuleType;
import net.sf.javascribe.langsupport.javascript.types.PromiseType;
import net.sf.javascribe.patterns.xml.js.page.PageFn;

/**
 * PageFn which either has handwritten code or wraps around a web service
 * @author DCS
 *
 */
@Plugin
public class PageFnProcessor implements ComponentProcessor<PageFn> {

	@Override
	public void process(PageFn comp, ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Javascript");
		String pageName = comp.getPageName();
		
		PageInfo info = PageUtils.getPageInfo(pageName, ctx);
		if (info==null) {
			throw new JavascribeException("Couldn't find page '"+pageName+"' for pageModel component");
		}

		// This component should depend on the page type
		String pageTypeName = info.getPageTypeName();
		JavascribeUtils.getType(JavascriptType.class, pageTypeName, ctx);

		String name = comp.getName();
		String event = comp.getEvent();
		if ((name.trim().equals("")) && (event.trim().equals(""))) {
			throw new JavascribeException("Page function requires either a name or an event");
		}
		
		boolean delegate = !comp.getService().trim().equals("");
		
		boolean hasCode = !comp.getCode().trim().equals("");
		
		if ((!delegate) && (!hasCode)) {
			throw new JavascribeException("A page function must either reference a service to delegate to, or define its own code");
		}
		if (delegate && hasCode) {
			throw new JavascribeException("A page function must either reference a service to delegate to, or define its own code, but not both");
		}
		
		if (hasCode) {
			PageFnDef fn = new PageFnDef();
			if (event.trim().length()>0) {
				fn.setEvent(event);
			}
			fn.setCode(comp.getCode());
			info.getFunctions().add(fn);
			if (comp.getName().trim().length()>0) {
				fn.setName(comp.getName());
			}
			return;
		}
		
		String operationRef = comp.getService();
		List<ServiceOperation> ops = JavascribeUtils.findRuleFromRef(operationRef, ctx);
		if (ops.size()==0) {
			throw new JavascribeException("Could not find rule '"+comp.getService()+"'");
		}
		else if (ops.size()>1) {
			throw new JavascribeException("Found multiple rules from reference "+comp.getService()+"'");
		}

		ServiceOperation op = ops.get(0);
		String returnTypeName = op.getReturnType();
		
		if (op.getReturnType()==null) {
			throw new JavascribeException("Page function can only refer to a service method that returns a Javascript promise");
		}
		
		PromiseType returnType = JavascribeUtils.getType(PromiseType.class, returnTypeName, ctx);
		
		if (!(returnType instanceof PromiseType)) {
			throw new JavascribeException("PageFn only supports services that return a promise");
		}
		
		PageFnDef def = new PageFnDef();
		info.getFunctions().add(def);
		def.setName(name);

		int i = operationRef.indexOf('.');
		String serviceAttributeName = operationRef.substring(0, i);
		JavascriptType serviceType = JavascribeUtils.getTypeForSystemAttribute(JavascriptType.class, serviceAttributeName, ctx);
		if (serviceType!=null) {
			if (serviceType instanceof ModuleType) {
				info.getImportedRefs().add(serviceAttributeName);
			}
		}
		
		StringBuilder code = new StringBuilder();
		CodeExecutionContext execCtx = new CodeExecutionContext(ctx);
		String modelTypeName = info.getModelTypeName();
		PageModelType modelType = JavascribeUtils.getType(PageModelType.class, modelTypeName, ctx);

		for(String p : op.getParamNames()) {
			execCtx.addVariable(p, op.getParamType(p));
			if (modelType.getAttributeType(p)!=null) {
				code.append("var "+p+" = _obj.model."+p+";\n");
			} else {
				def.getParams().add(p);
			}
		}

		returnType.instantiate("_service");
		code.append("var _promise;\n");
		code.append("_promise = "+operationRef+"(");
		boolean first = true;
		for(String param : op.getParamNames()) {
			if (first) {
				first = false;
			} else {
				code.append(',');
			}
			code.append(param);
		}
		code.append(");\n");

		// Code that handles the web servicie result and sets page model attributes
		// Also ensures that the page model attributes exist.
		StringBuilder handleResultCode = new StringBuilder();

		JavascriptType resolveType = returnType.getResolveType();
		if (resolveType!=null) {
			for(String attr : resolveType.getAttributeNames()) {
				String attribType = resolveType.getAttributeType(attr);
				if (modelType.getAttributeType(attr)!=null) {
					if (!modelType.getAttributeType(attr).equals(attribType)) {
						throw new JavascribeException("Found inconsistent types for model attribute '"+attr+"': '"+modelType.getAttributeType(attr)+"' and '"+attribType+"'");
					}
				} else {
					PageUtils.addModelProperty(info.getName(), attr, attribType, ctx);
				}
				handleResultCode.append("_obj.model."+attr+" = _result."+attr+";\n");
			}
		}

		code.append("_promise.then("); // _promise.then

		code.append("function(_result) {\n"); // Start of resolve
		code.append(handleResultCode.toString());
		code.append("_obj.event('"+name+"Success');\n");
		code.append("_obj.event('"+name+"Complete');\n");
		
		code.append("}, function(_result) {\n"); // End of resolve, start of reject
		code.append(handleResultCode.toString());
		code.append("_obj.event('"+name+"Fail');\n");
		code.append("_obj.event('"+name+"Complete');\n");
		
		code.append("}"); // End of reject
		
		code.append(");\n");// _promise.then
		def.setCode(code.toString());
		def.setReturnType(null);
	}
	
}

