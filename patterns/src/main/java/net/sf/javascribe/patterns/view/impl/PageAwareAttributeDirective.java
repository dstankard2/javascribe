package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.js.page.PageModelType;
import net.sf.javascribe.patterns.js.page.PageType;
import net.sf.javascribe.patterns.view.AttributeDirectiveBase;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;

@Scannable
public class PageAwareAttributeDirective extends AttributeDirectiveBase {

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String ref = ctx.getTemplateAttributes().get("js-page-ref");
		handlePageAware(ctx, ref);
	}

	@Override
	public String getAttributeName() {
		return "js-page-aware";
	}
	
	public static boolean isPageTemplatePageType(PageType pageType) throws JavascribeException {
		if (pageType.getAttributeType("_isTemplate")!=null) return true;
		return false;
	}
	
	public static void handlePageAware(DirectiveContext ctx,String pageRef) throws JavascribeException {
		String pageTypeName = ctx.getFunction().getName()+"TemplatePage";
		//String pageModelTypeName = ctx.getFunction().getName()+"TemplatePageModel";
		String pageVar = DirectiveUtils.PAGE_VAR;
		
		if (ctx.getExecCtx().getVariableType(pageVar)!=null) {
			throw new JavascribeException("Directive js-page-aware cannot be used on a template that is either a page template or is already page aware");
		}
		
		PageType pageType = new PageType(pageTypeName);
		PageModelType modelType = new PageModelType(pageTypeName);
		
		ctx.getProcessorContext().getTypes().addType(pageType);
		ctx.getProcessorContext().getTypes().addType(modelType);
		pageType.addAttribute("model", modelType.getName());
		pageType.addAttribute("_isTemplate", "string");
		ctx.getFunction().addParam(pageVar, pageTypeName);
		ctx.getExecCtx().addVariable(pageVar, pageTypeName);
		if ((pageRef!=null) && (pageRef.trim().length()>0)) {
			pageRef = pageRef.trim();
			if (ctx.getExecCtx().getTypeForVariable(pageRef)!=null) {
				throw new JavascribeException("Couldn't create a page reference called '"+pageRef+"' as there is another variable with that name in the template");
			}
			ctx.getCode().append("var "+pageRef+" = "+pageVar+";\n");
			ctx.getExecCtx().addVariable(pageRef, pageTypeName);
		}
		ctx.continueRenderElement(ctx.getExecCtx());
	}

}
