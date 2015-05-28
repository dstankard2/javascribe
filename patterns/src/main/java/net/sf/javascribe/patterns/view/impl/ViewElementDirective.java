package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.view.AttributeDirective;
import net.sf.javascribe.patterns.view.DirectiveContext;

@Scannable
public class ViewElementDirective implements AttributeDirective {

	@Override
	public String getAttributeName() {
		return "js-view-element";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder code = ctx.getCode();
		String classValue = ctx.getTemplateAttributes().get("js-view-element").trim();
		
		ctx.continueRenderElement(ctx.getExecCtx());
		
	}

}
