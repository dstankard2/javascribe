package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.js.page.PageModelProcessor;
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

		String typeName = ctx.getProcessorContext().getAttributeType(name);
		if (typeName==null) {
			throw new JavascribeException("Directive js-model-attribute couldn't find a type for attribute '"+name+"'");
		}
		
		if (pageType.getAttributeType("_isTemplate")==null) {
			PageUtils.ensureModel(ctx.getProcessorContext(), pageType);
			PageModelType modelType = DirectiveUtils.getPageModelType(ctx);
			
			if (modelType.getAttributeType(name)!=null) {
				throw new JavascribeException("Directive js-model-attribute tried to add attribute '"+name+"' to the model but it was already there.");
			}

			StringBuilder initCode = PageUtils.getInitFunction(ctx.getProcessorContext(), pageName);
			PageModelProcessor.addModelAttribute(modelType, name, typeName, initCode, onChange, pageName);
		} else {
			String modelTypeName = pageType.getAttributeType("model");
			PageModelType modelType = (PageModelType)ctx.getProcessorContext().getType(modelTypeName);
			modelType.addAttribute(name, typeName);
		}
		
	}

}
