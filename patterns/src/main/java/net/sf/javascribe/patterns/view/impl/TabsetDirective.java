package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.patterns.js.page.PageUtils;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;
import net.sf.javascribe.patterns.view.ElementDirective;

public class TabsetDirective implements ElementDirective {

	@Override
	public String getElementName() {
		return "js-tabset";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String pageName = DirectiveUtils.getPageName(ctx);
		
		if (pageName==null) {
			throw new JavascribeException("A tabset may only be used on a page template");
		}
		if (ctx.getExecCtx().getVariableType("_tabset")!=null) {
			throw new JavascribeException("You may not have nested tabsets");
		}
		ctx.getExecCtx().addVariable("_tabset", "object");

		StringBuilder initFunc = PageUtils.getInitFunction(ctx.getProcessorContext(), pageName);
		StringBuilder code = ctx.getCode();
		String def = ctx.getTemplateAttributes().get("js-default");
		ctx.getTemplateAttributes().remove("default");
		code.append(ctx.getElementVarName()+" = "+DirectiveUtils.DOCUMENT_REF+".createElement('div');\n");
//		code.append()
	}

}
