package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.VariableType;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.javascript.JavascriptUtils;
import net.sf.javascribe.patterns.js.page.PageModelType;
import net.sf.javascribe.patterns.js.page.PageType;
import net.sf.javascribe.patterns.js.page.PageUtils;
import net.sf.javascribe.patterns.view.Directive;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;
import net.sf.javascribe.patterns.view.Restrictions;

@Scannable
public class PageDirective implements Directive {

	@Override
	public Restrictions[] getRestrictions() {
		return new Restrictions[] { Restrictions.ATTRIBUTE };
	}

	@Override
	public String getName() {
		return "js-page";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String pageName = ctx.getAttributes().get("js-page");
		StringBuilder b = ctx.getCode();
		CodeExecutionContext execCtx = ctx.getExecCtx();

		ctx.getAttributes().remove("js-page");
		String typeName = ctx.getProcessorContext().getAttributeType(pageName);
		if (typeName==null) {
			throw new JavascribeException("Could not find a page called '"+pageName+"'");
		}
		VariableType t = ctx.getProcessorContext().getType(typeName);
		if (!(t instanceof PageType)) {
			throw new JavascribeException("The type for '"+pageName+"' is not a page");
		}
		if (DirectiveUtils.getPageName(ctx)!=null) {
			throw new JavascribeException("Only one page may be defined for any element - pages must be in separate locations");
		}
		PageType pageType = (PageType)t;
		String pageVar = DirectiveUtils.PAGE_VAR;
		b.append(pageType.declare(pageVar, execCtx).getCodeText());
		b.append(pageType.instantiate(pageVar, null, execCtx).getCodeText());
		execCtx.addVariable(pageVar, pageType.getName());

		ctx.continueRenderElement(execCtx);

		String var = ctx.getElementVarName();
		b.append(var+".id = '"+pageName+"';\n");
		StringBuilder init = PageUtils.getInitFunction(ctx.getProcessorContext(), pageName);
		init.append("var _page;\n");
		execCtx.addVariable("_page", "div");
		init.append("var "+ctx.getTemplateObj()+" = window."+ctx.getTemplateObj()+";\n");
		init.append(JavascriptUtils.invokeFunction("_page", ctx.getTemplateObj(), ctx.getFunction(), execCtx).getCodeText());
		init.append("this.view.page.parentNode.replaceChild(_page,this.view.page);\n");
		init.append("this.view.page = _page;\n");
	}
	
	public static String getPageName(DirectiveContext ctx) {
		String ret = null;
		
		if (ctx.getExecCtx().getVariableType("_page")!=null) {
			ret = ctx.getExecCtx().getVariableType("_page");
		}
		
		return ret;
	}
	
	public static PageModelType getPageModelType(DirectiveContext ctx) throws JavascribeException {
		PageModelType ret = null;
		
		PageType page = (PageType)ctx.getExecCtx().getTypeForVariable("_page");
		String modelTypeName = page.getAttributeType("model");
		ret = (PageModelType)ctx.getProcessorContext().getType(modelTypeName);
		
		return ret;
	}
	
}

