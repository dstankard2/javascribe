package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.js.page.PageModelType;
import net.sf.javascribe.patterns.js.page.PageType;
import net.sf.javascribe.patterns.view.AttributeDirectiveBase;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;

@Scannable
public class PageAwareDirective extends AttributeDirectiveBase {

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String pageTypeName = ctx.getFunction().getName()+"TemplatePage";
		String pageModelTypeName = ctx.getFunction().getName()+"TemplatePageModel";
		String pageVar = DirectiveUtils.PAGE_VAR;
		String ref = ctx.getTemplateAttributes().get("js-page-ref");
		
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
		if ((ref!=null) && (ref.trim().length()>0)) {
			ref = ref.trim();
			if (ctx.getExecCtx().getTypeForVariable(ref)!=null) {
				throw new JavascribeException("Couldn't create a page reference called '"+ref+"' as there is another variable with that name in the template");
			}
			ctx.getCode().append("var "+ref+" = "+pageVar+";\n");
			ctx.getExecCtx().addVariable(ref, pageTypeName);
		}
		ctx.continueRenderElement(ctx.getExecCtx());
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
