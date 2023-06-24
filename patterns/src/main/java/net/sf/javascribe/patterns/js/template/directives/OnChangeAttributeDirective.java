package net.sf.javascribe.patterns.js.template.directives;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.patterns.js.template.parsing.AttributeDirectiveBase;
import net.sf.javascribe.patterns.js.template.parsing.DirectiveContext;
import net.sf.javascribe.patterns.js.template.parsing.DirectiveUtils;

@Plugin
public class OnChangeAttributeDirective extends AttributeDirectiveBase {

	@Override
	public String getAttributeName() {
		return "js-onchange";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String change = ctx.getTemplateAttribute("js-onchange");
		String eltName = ctx.getElementName();
		String var = ctx.getElementVarName();
		StringBuilder code = ctx.getCode();
		CodeExecutionContext execCtx = ctx.getExecCtx();
		
		// The ModelAttributeDirective will handle this attribute.
		if (ctx.getTemplateAttribute("js-model")!=null) {
			ctx.continueRenderElement(ctx.getExecCtx());
			return;
		}
		
		String domEvent = null;
		if (eltName.equals("select")) {
			domEvent = "change";
		} else if (eltName.equals("input")) {
			String type = ctx.getDomAttribute("type");
			if (type==null) type = "text";
			if (type.equals("text")) domEvent = "keyup";
			else domEvent = "change";
		} else if (eltName.equals("textarea")) {
			domEvent = "keyup";
		} else {
			throw new JavascribeException("OnChangeDirective cannot be used on element '"+eltName+"'");
		}
		
		String dispatcherRef = DirectiveUtils.EVENT_DISPATCHER_FN_VAR;
		if (execCtx.getVariableType(dispatcherRef)==null) {
			throw new JavascribeException("There is no event dispatcher available to trigger an event on");
		}

		ctx.continueRenderElement(ctx.getExecCtx());
		String changeEventStr = DirectiveUtils.parsePartialExpression(change, execCtx);
		StringBuilder fn = new StringBuilder();
		fn.append("function() {\n");
		fn.append(dispatcherRef+"("+changeEventStr+");\n}\n");
		code.append(var+".addEventListener('"+domEvent+"',"+fn.toString()+");\n");
	}
	
}

