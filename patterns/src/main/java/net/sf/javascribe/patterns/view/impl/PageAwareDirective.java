package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.patterns.js.page.PageType;
import net.sf.javascribe.patterns.js.page.PageUtils;
import net.sf.javascribe.patterns.view.AttributeDirective;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;

public class PageAwareDirective implements AttributeDirective {

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String pageTypeName = ctx.getFunction().getName()+"TemplatePage";
		String pageVar = DirectiveUtils.PAGE_VAR;
		
		if (ctx.getExecCtx().getVariableType("_page")!=null) {
			throw new JavascribeException("Directive js-page-aware cannot be used on a template that is either a page template or is already page aware");
		}
		
		PageType pageType = new PageType(pageTypeName);
		ctx.getProcessorContext().getTypes().addType(pageType);
		pageType.addAttribute("_isTemplate", "string");
		PageUtils.ensureModel(ctx.getProcessorContext(), pageType);
		ctx.getFunction().addParam(pageVar, pageTypeName);
		ctx.getExecCtx().addVariable(pageVar, pageTypeName);
		ctx.continueRenderElement();
	}

	@Override
	public String getAttributeName() {
		return "js-page-aware";
	}
	
	public static boolean isPageTemplatePageType(PageType pageType) throws JavascribeException {
		if (pageType.getAttributeType("_isTemplate")!=null) return true;
		return false;
	}

}
