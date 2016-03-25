package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.ElementDirective;

@Scannable
public class PageAwareElementDirective implements ElementDirective {

	@Override
	public String getElementName() {
		return "js-page-aware";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String ref = ctx.getTemplateAttributes().get("js-page-ref");
		if ((ref!=null) && (ref.trim().length()==0)) ref = null;
		PageAwareAttributeDirective.handlePageAware(ctx, ref);
	}

}

