package net.sf.javascribe.patterns.js.page.elements;

import java.util.HashMap;

import net.sf.javascribe.api.GeneratorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.javascript.JavascriptConstants;
import net.sf.javascribe.langsupport.javascript.JavascriptVariableType;
import net.sf.javascribe.patterns.js.page.PageUtils;

public class ElementBinderContext {
	private GeneratorContext ctx = null;
	private HashMap<String,String> modelAttributes = null;
	private String pageName = null;
	private JavascriptVariableType pageType = null;
	private JavascriptVariableType modelType = null;
	
	public ElementBinderContext(GeneratorContext ctx,HashMap<String,String> modelAttributes,String pageName) {
		this.ctx = ctx;
		this.modelAttributes = modelAttributes;
		this.pageName = pageName;
		this.pageType = (JavascriptVariableType)ctx.getType(JavascriptConstants.JS_TYPE+pageName);
		this.modelType = (JavascriptVariableType)ctx.getType(pageType.getAttributeType("model"));
	}
	
	public static ElementBinderContext newInstance(GeneratorContext ctx,String pageName) throws JavascribeException {
		ElementBinderContext ret = null;

		HashMap<String,String> modelAttributes = PageUtils.getModelAttributes(ctx, pageName);
		ret = new ElementBinderContext(ctx,modelAttributes,pageName);
		
		return ret;
	}
	
	public GeneratorContext getCtx() {
		return this.ctx;
	}
	
	public String getPageName() {
		return pageName;
	}

	public String getModelAttributeType(String name) {
		return modelAttributes.get(name);
	}

	public String getTypeForAttribute(String name) {
		return ctx.getAttributeType(name);
	}

	public JavascriptVariableType getPageType() {
		return pageType;
	}

	public JavascriptVariableType getModelType() {
		return modelType;
	}

}
