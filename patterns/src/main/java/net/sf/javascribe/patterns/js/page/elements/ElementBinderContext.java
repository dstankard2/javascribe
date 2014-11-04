package net.sf.javascribe.patterns.js.page.elements;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.patterns.js.page.PageModelType;
import net.sf.javascribe.patterns.js.page.PageType;
import net.sf.javascribe.patterns.js.page.PageUtils;

public class ElementBinderContext {
	private ProcessorContext ctx = null;
	private String pageName = null;
	private PageType pageType = null;
	private PageModelType modelType = null;
	
	public ElementBinderContext(ProcessorContext ctx,String pageName) throws JavascribeException {
		this.ctx = ctx;
		this.pageName = pageName;
		this.pageType = (PageType)ctx.getType(pageName);
		if (pageType.getAttributeType("model")!=null)
			this.modelType = PageUtils.getModelType(ctx, pageName);
	}
	
	public static ElementBinderContext newInstance(ProcessorContext ctx,String pageName) throws JavascribeException {
		ElementBinderContext ret = null;

		if (ctx.getType(pageName)==null) {
			throw new JavascribeException("Couldn't initialize bindings - no page called '"+pageName+"' found");
		}
		ret = new ElementBinderContext(ctx,pageName);
		
		return ret;
	}
	
	public ProcessorContext getCtx() {
		return this.ctx;
	}
	
	public String getPageName() {
		return pageName;
	}

	public String getModelAttributeType(String name) {
		return modelType.getAttributeType(name);
	}

	public String getTypeForAttribute(String name) {
		return ctx.getAttributeType(name);
	}

	public PageType getPageType() {
		return pageType;
	}

	public PageModelType getModelType() {
		return modelType;
	}

}
