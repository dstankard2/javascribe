package net.sf.javascribe.patterns.view;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.view.impl.JavascriptEvalResult;
import net.sf.javascribe.patterns.view.impl.JavascriptEvaluator;

@Scannable
public class RemoveAttributeDirective extends AttributeDirectiveBase {

	@Override
	public String getAttributeName() {
		return "js-onremove";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String code = ctx.getTemplateAttributes().get("js-onremove");

		ctx.continueRenderElement();
		StringBuilder fn = ctx.getCode();
		CodeExecutionContext execCtx = ctx.getExecCtx();
		CodeExecutionContext newCtx = new CodeExecutionContext(execCtx);
		JavascriptEvaluator eval = new JavascriptEvaluator(code, newCtx);
		DirectiveUtils.populateImpliedVariables(eval);
		JavascriptEvalResult result = eval.evalCodeBlock();
		if (result.getErrorMessage()!=null) {
			throw new JavascribeException(result.getErrorMessage());
		}
		fn.append(ctx.getElementVarName()+".$$remove = function() {\n");
		fn.append(result.getResult().toString());
		fn.append("\n};\n");
	}

}
