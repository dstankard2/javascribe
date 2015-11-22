package net.sf.javascribe.patterns.view;

import java.util.List;
import java.util.StringTokenizer;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;

@Scannable
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
		String event = ctx.getTemplateAttributes().get("js-event");
		CodeExecutionContext execCtx = ctx.getExecCtx();
		
		String dispatcher = null;
		if (execCtx.getVariableType(DirectiveUtils.PAGE_VAR)!=null) dispatcher = DirectiveUtils.PAGE_VAR;
		else if (execCtx.getVariableType(DirectiveUtils.EVENT_DISPATCHER_VAR)!=null) 
			dispatcher = DirectiveUtils.EVENT_DISPATCHER_VAR;
		
		if (dispatcher==null) {
			throw new JavascribeException("You can only use the js-event directive on a template that uses js-page, js-page-aware or js-event-dispatcher");
		}
		
		String fnVar = DirectiveUtils.newVarName("_f","object",execCtx);
		String containerVar = ctx.getContainerVarName();
		String eltVar = ctx.getElementVarName();
		code.append("var "+fnVar+" = function() {\n");
		CodeExecutionContext newCtx = new CodeExecutionContext(execCtx);
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
		code.append("if ("+eltVar+")");
		code.append("window._ins("+containerVar+","+eltVar+","+elList+");\n");
		
		code.append("};\n");
		
		StringTokenizer tok = new StringTokenizer(event,",");
		while(tok.hasMoreTokens()) {
			String s = tok.nextToken().trim();
			String ref = DirectiveUtils.parsePartialExpression(s, newCtx);
			code.append(dispatcher+".event("+ref+","+fnVar+","+containerVar+");\n");
		}
		code.append(fnVar+"();\n");
	}
	
	
}
