package net.sf.javascribe.patterns.view.impl.events;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.patterns.view.AttributeDirectiveBase;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;
import net.sf.javascribe.patterns.view.impl.JaEval2;
import net.sf.javascribe.patterns.view.impl.JaEvalResult;

public abstract class AbstractElementDomEventDirective extends AttributeDirectiveBase {

	@Override
	public abstract String getAttributeName();

	protected final void addEventListener(StringBuilder code,String eltVarName,String event,String fn) {
		code.append(eltVarName+".addEventListener('"+event+"',"+fn+");\n");
	}

	protected abstract String getDomEvent();
	
	protected String getEventCode(DirectiveContext ctx) throws JavascribeException {
		String userString = ctx.getTemplateAttributes().get(getAttributeName());
		StringBuilder b = new StringBuilder();
		CodeExecutionContext execCtx = new CodeExecutionContext(ctx.getExecCtx());
		
		execCtx.addVariable("$event", "DomEvent");
		
		JaEval2 eval = new JaEval2(userString,execCtx);
		DirectiveUtils.populateImpliedVariables(eval);
		JaEvalResult result = eval.parseCodeBlock();
		if (result.getErrorMessage()!=null) {
			throw new JavascribeException("Couldn't build DOM event lsitener: "+result.getErrorMessage());
		}
		b.append(result.getResult().toString());
		
		return b.toString();
	}
	
	@Override
	public final void generateCode(DirectiveContext ctx) throws JavascribeException {
		String elt = ctx.getElementVarName();
		StringBuilder code = ctx.getCode();
		String domEvent = getDomEvent();

		ctx.continueRenderElement(ctx.getExecCtx());
		
		StringBuilder eventCode = new StringBuilder();
		eventCode.append("function($event) {\n");
		eventCode.append(getEventCode(ctx));
		eventCode.append("}\n");
		String ev = eventCode.toString();
		
		addEventListener(code,elt,domEvent,ev);
	}

}
