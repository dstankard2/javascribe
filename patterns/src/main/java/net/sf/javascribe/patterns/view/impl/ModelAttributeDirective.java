package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.js.page.PageModelType;
import net.sf.javascribe.patterns.js.page.PageType;
import net.sf.javascribe.patterns.js.page.PageUtils;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;
import net.sf.javascribe.patterns.view.ElementDirective;

@Scannable
public class ModelAttributeDirective implements ElementDirective {

	@Override
	public String getElementName() {
		return "js-model-attribute";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String name = ctx.getDomAttributes().get("name");
		String type = ctx.getDomAttributes().get("type");
		String onChange = ctx.getDomAttributes().get("onChange");
		
		if ((name==null) || (name.trim().length()==0)) {
			throw new JavascribeException("Directive js-model-attribute requires a name attribute");
		}
		if (DirectiveUtils.getPageName(ctx)==null) {
			throw new JavascribeException("Directive js-model-attribute can only be used on a page template");
		}
		String pageName = DirectiveUtils.getPageName(ctx);
		
		if (pageName==null) {
			throw new JavascribeException("Directive js-model-attribute is only valid when used on a page template");
		}
		PageType pageType = (PageType)ctx.getProcessorContext().getType(pageName);
		if (pageType==null) {
			throw new JavascribeException("Page '"+pageName+"' was not found");
		}
		
		String attrType = ctx.getProcessorContext().getAttributeType(name);
		if (attrType==null) {
			if (type==null) throw new JavascribeException("Couldn't find a type for model attribute '"+name+"'");
			ctx.getProcessorContext().addAttribute(name, type);
		} else {
			if ((type!=null) && (!type.equals(attrType))) {
				throw new JavascribeException("Found conflicting types for attribute '"+attrType+"'");
			}
			type = attrType;
		}

		if (pageType.getAttributeType("_isTemplate")==null) {
			PageUtils.ensureModel(ctx.getProcessorContext(), pageType);
			PageModelType modelType = DirectiveUtils.getPageModelType(ctx);
			PageUtils.addModelAttribute(modelType, name, type, onChange, pageName,ctx.getProcessorContext());
		} else {
			String modelTypeName = pageType.getAttributeType("model");
			PageModelType modelType = (PageModelType)ctx.getProcessorContext().getType(modelTypeName);
			modelType.addAttribute(name, type);
		}
		
	}

}
