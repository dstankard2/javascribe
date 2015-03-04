package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.patterns.view.AttributeDirective;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;

//This is not scannable because it will be added to the renderer list manually.
public class IfDirective implements AttributeDirective {

	@Override
	public String getAttributeName() {
		return "js-if";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder b = ctx.getCode();
		String cond = ctx.getTemplateAttributes().get("js-if");
		CodeExecutionContext existingCtx = ctx.getExecCtx();

		ctx.getTemplateAttributes().remove("js-if");
		// If there is a page in the execution context, 
		if (existingCtx.getVariableType(DirectiveUtils.PAGE_VAR)==null) {
			b.append("if ("+cond+") {\n");
			CodeExecutionContext newCtx = new CodeExecutionContext(existingCtx);
			ctx.continueRenderElement(newCtx);
			b.append("}\n");
		} else {
			String expr = DirectiveUtils.evaluateIf(cond, ctx.getExecCtx());

			String boolVar = ctx.newVarName("_b", "boolean", existingCtx);
			String n = ctx.getElementVarName();
			String c = ctx.getContainerVarName();
			String in = ctx.newVarName("_i", "integer", existingCtx);
			b.append("for("+in+"=0;"+in+"<"+c+".childNodes.length;"+in+"++){\n");
			b.append("if ("+c+".childNodes["+in+"].classList.contains('"+n+"')){\n");
			b.append(c+".removeChild("+c+".childNodes["+in+"]);\n");
			b.append(in+"--;\n");
			b.append("}\n");
			b.append("}\n");

			b.append("var "+boolVar+" = false;\n");
			b.append("try {\n"+boolVar+" = ("+expr+");\n} catch(_err) { }\n");
			b.append("if ("+boolVar+") {\n");
			CodeExecutionContext newCtx = new CodeExecutionContext(existingCtx);
			ctx.continueRenderElement(newCtx);
			b.append("}\n");
		}
	}
	
}

