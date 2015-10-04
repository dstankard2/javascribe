package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.view.AttributeDirective;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;

/**
 * onChange directive.
 * Code generated for this directive requires no memory cleanup because it creates a dom listener 
 * and not an eventDispatcher listener.
 * @author Dave
 *
 */
@Scannable
public class OnChangeDirective implements AttributeDirective {

	@Override
	public String getAttributeName() {
		return "js-onchange";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String change = ctx.getTemplateAttributes().get("js-onchange");
		String eltName = ctx.getElementName();
		String var = ctx.getElementVarName();
		StringBuilder code = ctx.getCode();
		CodeExecutionContext execCtx = ctx.getExecCtx();
		
		// The ModelDirective will handle this attribute.
		if (ctx.getTemplateAttributes().get("js-model")!=null) {
			ctx.continueRenderElement();
			return;
		}
		
		String domEvent = null;
		if (eltName.equals("select")) {
			domEvent = "change";
		} else if (eltName.equals("input")) {
			String type = ctx.getDomAttributes().get("type");
			if (type==null) type = "text";
			if (type.equals("text")) domEvent = "keyup";
			else domEvent = "change";
		} else if (eltName.equals("textarea")) {
			domEvent = "keyup";
		} else {
			throw new JavascribeException("OnChangeDirective cannot be used on element '"+eltName+"'");
		}
		
		String dispatcherRef = null;
		if (execCtx.getVariableType(DirectiveUtils.PAGE_VAR)!=null) {
			dispatcherRef = DirectiveUtils.PAGE_VAR+".event";
		} else if (execCtx.getVariableType(DirectiveUtils.EVENT_DISPATCHER_VAR)!=null) {
			dispatcherRef = DirectiveUtils.EVENT_DISPATCHER_VAR+".event";
		} else {
			throw new JavascribeException("There is no event dispatcher available to trigger an event on");
		}
		ctx.continueRenderElement();
		String changeEventStr = DirectiveUtils.parsePartialExpression(change, execCtx);
		StringBuilder fn = new StringBuilder();
		fn.append("function() {\n");
		fn.append(dispatcherRef+"("+changeEventStr+");\n}\n");
		code.append(var+".addEventListener('"+domEvent+"',"+fn.toString()+");\n");
	}
	
}

