package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.view.AttributeDirectiveBase;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;

@Scannable
public class EnabledDirective extends AttributeDirectiveBase {

	@Override
	public String getAttributeName() {
		return "js-enabled";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder code = ctx.getCode();
		String value = ctx.getTemplateAttributes().get("js-enabled");
		
		ctx.continueRenderElement(ctx.getExecCtx());
		
		JavascriptEvaluator eval = new JavascriptEvaluator(value,ctx.getExecCtx());
		DirectiveUtils.populateImpliedVariables(eval);
		JavascriptEvalResult res = eval.evalExpression();
		if (res.getErrorMessage()!=null) {
			throw new JavascribeException(res.getErrorMessage());
		}
		String cond = res.getResult().toString();
		code.append("try {\n");
		code.append("if ("+cond+") "+ctx.getElementVarName()+".disabled = false;\n");
		code.append("else "+ctx.getElementVarName()+".disabled = true;\n");
		code.append("}catch(_err){");
		code.append(ctx.getElementVarName()+".disabled = true;");
		code.append("}\n");
	}

}
