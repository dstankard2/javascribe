package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.js.page.PageModelProcessor;
import net.sf.javascribe.patterns.js.page.PageModelType;
import net.sf.javascribe.patterns.js.page.PageUtils;
import net.sf.javascribe.patterns.view.Directive;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;
import net.sf.javascribe.patterns.view.Restrictions;

@Scannable
public class ModelAttributeDirective implements Directive {

	@Override
	public Restrictions[] getRestrictions() {
		return new Restrictions[] { Restrictions.ELEMENT };
	}

	@Override
	public String getName() {
		return "js-model-attribute";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder code = ctx.getCode();
		String name = ctx.getAttributes().get("name");
		String onChange = ctx.getAttributes().get("onChange");
		
		if ((name==null) || (name.trim().length()==0)) {
			throw new JavascribeException("Directive js-model-attribute requires a name attribute");
		}
		if (DirectiveUtils.getPageName(ctx)==null) {
			throw new JavascribeException("Directive js-model-attribute can only be used on a page template");
		}
		PageModelType modelType = DirectiveUtils.getPageModelType(ctx);
		
		if (modelType.getAttributeType(name)!=null) {
			throw new JavascribeException("Directive js-model-attribute tried to add attribute '"+name+"' to the model but it was already there.");
		}
		String typeName = ctx.getProcessorContext().getAttributeType(name);
		if (typeName==null) {
			throw new JavascribeException("Directive js-model-attribute couldn't find a type for attribute '"+name+"'");
		}

		String pageName = DirectiveUtils.getPageName(ctx);
		StringBuilder initCode = PageUtils.getInitFunction(ctx.getProcessorContext(), pageName);
		PageModelProcessor.addModelAttribute(modelType, name, typeName, initCode, onChange, pageName);
	}

}
