package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.patterns.js.page.PageUtils;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;
import net.sf.javascribe.patterns.view.ElementDirective;

public class TabDirective implements ElementDirective {

	@Override
	public String getElementName() {
		return "js-tab";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String pageName = DirectiveUtils.getPageName(ctx);
		
		if (pageName==null) {
			throw new JavascribeException("A tabset may only be used on a page template");
		}
		if (ctx.getExecCtx().getVariableType("_tabset")==null) {
			throw new JavascribeException("You may not have nested tabsets");
		}

		StringBuilder initFunc = PageUtils.getInitFunction(ctx.getProcessorContext(), pageName);
		StringBuilder code = ctx.getCode();
	}

}
