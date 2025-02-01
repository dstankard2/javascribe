package net.sf.javascribe.patterns.js.template.directives;

import java.util.List;
import java.util.StringTokenizer;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.patterns.js.template.parsing.AttributeDirectiveBase;
import net.sf.javascribe.patterns.js.template.parsing.DirectiveContext;
import net.sf.javascribe.patterns.js.template.parsing.DirectiveUtils;

@Plugin
public class EventAttributeDirective extends AttributeDirectiveBase {

	@Override
	public int getPriority() { return 0; }

	@Override
	public String getAttributeName() {
		return "js-event";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder code = ctx.getCode();
		String event = ctx.getTemplateAttribute("js-event");
		CodeExecutionContext execCtx = ctx.getExecCtx();
		String parent = ctx.getContainerVarName();
		
		if (parent==null) {
			throw new JavascribeException("You may not use js-event on the root element of a template");
		}
		if (event == null) {
			throw new JavascribeException("Found a js-event directive with an empty event");
		}
		
		String dispatcherTypeName = execCtx.getVariableType(DirectiveUtils.EVENT_DISPATCHER_FN_VAR);
		if (dispatcherTypeName==null) {
			throw new JavascribeException("Couldn't find event dispatcher in this template - You must use the js-event directive on a template that uses js-page, js-page-aware or js-event-dispatcher");
		}
		else if (!dispatcherTypeName.equals("function")) {
			throw new JavascribeException("Found a variable in this template for the event dispatcher but it is not an event dispatcher");
		}
		
		String fn = DirectiveUtils.EVENT_DISPATCHER_FN_VAR;

		String fnVar = DirectiveUtils.newVarName("_f","object",execCtx);
		String containerVar = ctx.getContainerVarName();
		String eltVar = ctx.getElementVarName();
		code.append("var "+fnVar+" = function() {\n");
		CodeExecutionContext newCtx = new CodeExecutionContext(execCtx);
		code.append("if ("+containerVar+") {\n");
		code.append("_rem("+containerVar+",'"+eltVar+"');\n");
		code.append(eltVar+" = null;\n");
		ctx.continueRenderElement(newCtx);

		List<String> previousEltVars = ctx.getPreviousEltVars();
		String elList = DirectiveUtils.newVarName("_x","list/DOMElement",execCtx);

		code.append("var "+elList+" = [");
		for(String s : previousEltVars) {
			code.append('\''+s+"',");
		}
		code.append('\''+eltVar+'\'');
		code.append("];\n");
		code.append("if ("+eltVar+") {\n");
		code.append("_ins("+containerVar+","+eltVar+","+elList+");\n}\n");
		code.append("}\n");
		code.append("};\n");
		
		StringTokenizer tok = new StringTokenizer(event,",");
		while(tok.hasMoreTokens()) {
			String rv = DirectiveUtils.newVarName("_rv", "function", execCtx);
			String s = tok.nextToken().trim();
			String ref = DirectiveUtils.parsePartialExpression(s, newCtx);
			code.append("var "+rv+" = "+fn+"("+ref+","+fnVar+");\n");
			code.append(containerVar+".$$remove.push(function() {"+rv+"();});\n");
		}
		code.append(fnVar+"();\n");
	}
	
}

