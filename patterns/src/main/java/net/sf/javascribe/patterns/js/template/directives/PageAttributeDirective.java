package net.sf.javascribe.patterns.js.template.directives;


import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.types.ServiceOperation;
import net.sf.javascribe.patterns.js.page.PageInfo;
import net.sf.javascribe.patterns.js.page.PageUtils;
//import net.sf.javascribe.patterns.js.page.PageInfo;
//import net.sf.javascribe.patterns.js.page.PageUtils;
import net.sf.javascribe.patterns.js.template.parsing.AttributeDirectiveBase;
import net.sf.javascribe.patterns.js.template.parsing.DirectiveContext;
import net.sf.javascribe.patterns.js.template.parsing.DirectiveUtils;

@Plugin
public class PageAttributeDirective extends AttributeDirectiveBase {

	@Override
	public String getAttributeName() {
		return "js-page";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String pageName = ctx.getTemplateAttribute("js-page");
		String pageRef = ctx.getTemplateAttribute("js-page-ref");
		String modelRef = ctx.getTemplateAttribute("js-model-ref");
		CodeExecutionContext execCtx = ctx.getExecCtx();

		PageInfo pageInfo = PageUtils.getPageInfo(pageName, ctx.getProcessorContext());

		ctx.getProcessorContext().getVariableType(pageName);
		if (pageInfo==null) {
			throw new JavascribeException("Found no page called '"+pageName+"'");
		}

		if (pageInfo.getPageRendererObj() != null) {
			throw new JavascribeException("Page '"+pageName+"' may only be rendered by one HTML template");
		}
		String modelTypeName = pageInfo.getModelTypeName();
		ServiceOperation op = ctx.getFunction();
		StringBuilder fnCode = ctx.getCode();
		execCtx.addVariable("_page", pageName);
		op.addParam("_page", pageName);

		execCtx.addVariable("_model", modelTypeName);
		fnCode.append("var _model = _page.model;\n");
		
		execCtx.addVariable("_model", modelTypeName);
		if (pageRef!=null) {
			if (execCtx.getVariableType(pageRef)!=null) {
				throw new JavascribeException("Couldn't create variable '"+pageRef+"' for page ref because that variable already exists");
			}
			fnCode.append("var "+pageRef+" = _page;\n");
			execCtx.addVariable(pageRef, pageName);
		}
		
		if (modelRef!=null) {
			if (execCtx.getVariableType(modelRef)!=null) {
				throw new JavascribeException("Couldn't create variable '"+pageRef+"' for page model ref because that variable already exists");
			}
			fnCode.append("var "+modelRef+" = _page.model;\n");
			execCtx.addVariable(modelRef, modelTypeName);
		}

		// Get the page renderer
		String ref = ctx.getProcessorContext().getProperty("page.template.objRef");

		if (ref==null) {
			throw new JavascribeException("Page Attribute directive requires config property 'page.template.objRef' which should be a reference to an object on the window that will render the page");
		}

		//String ref = ctx.getTemplateObj();
		pageInfo.setPageRendererObj(ref);
		pageInfo.setPageRendererRule(op);
		
		// Add event function to execCtx
		execCtx.addVariable(DirectiveUtils.EVENT_DISPATCHER_FN_VAR, "function");
		fnCode.append("var "+DirectiveUtils.EVENT_DISPATCHER_FN_VAR+" = _page.event;\n");
		
		ctx.continueRenderElement(execCtx);
	}

}

