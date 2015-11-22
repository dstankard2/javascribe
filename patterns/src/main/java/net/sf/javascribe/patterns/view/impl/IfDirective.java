package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.view.AttributeDirectiveBase;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;

@Scannable
public class IfDirective extends AttributeDirectiveBase {

	@Override
	public int getPriority() { return 1; }
	
	@Override
	public String getAttributeName() {
		return "js-if";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder b = ctx.getCode();
		String cond = ctx.getTemplateAttributes().get("js-if");
		CodeExecutionContext existingCtx = ctx.getExecCtx();

		JaEval2 eval = new JaEval2(cond,existingCtx);
		DirectiveUtils.populateImpliedVariables(eval);
		JaEvalResult result = eval.parseExpression();
		if (result.getErrorMessage()!=null) {
			throw new JavascribeException(result.getErrorMessage());
		}
		String finalCond = result.getResult().toString();

		String boolVar = ctx.newVarName("_b", "boolean", existingCtx);
		b.append("var "+boolVar+" = false;\n");
		b.append("try {\n"+boolVar+" = ("+finalCond+");\n} catch(_err) { }\n");
		b.append("if ("+boolVar+") {\n");

		CodeExecutionContext newCtx = new CodeExecutionContext(existingCtx);
		ctx.continueRenderElement(newCtx);

		b.append("}\n");
		
	}
	
}

