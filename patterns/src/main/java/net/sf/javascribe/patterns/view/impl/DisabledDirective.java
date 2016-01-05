package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.view.AttributeDirectiveBase;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;

@Scannable
public class DisabledDirective extends AttributeDirectiveBase {

	@Override
	public String getAttributeName() {
		return "js-disabled";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder code = ctx.getCode();
		String value = ctx.getTemplateAttributes().get("js-disabled");
		
		ctx.getTemplateAttributes().remove("js-disabled");
		ctx.continueRenderElement(ctx.getExecCtx());
		
		JavascriptEvaluator eval = new JavascriptEvaluator(value,ctx.getExecCtx());
		DirectiveUtils.populateImpliedVariables(eval);
		JavascriptEvalResult result = eval.evalExpression();
		if (result.getErrorMessage()!=null) {
			throw new JavascribeException(result.getErrorMessage());
		}
		String cond = result.getResult().toString();
		code.append("try {\n");
		code.append("if ("+cond+") "+ctx.getElementVarName()+".disabled = true;\n");
		code.append("else "+ctx.getElementVarName()+".disabled = false;\n");
		code.append("}catch(_err){");
		code.append(ctx.getElementVarName()+".disabled = false;\n");
		code.append("}\n");
	}

}
