package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.patterns.view.AttributeDirective;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;

//This is not scannable because it will be added to the renderer list manually.
public class ShowDirective implements AttributeDirective {

	@Override
	public String getAttributeName() {
		return "js-show";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder b = ctx.getCode();
		String cond = ctx.getTemplateAttributes().get("js-show");
		CodeExecutionContext execCtx = null;
		String eltVar = ctx.getElementVarName();

		ctx.continueRenderElement(ctx.getExecCtx());
		
		String boolVar = ctx.newVarName("_b", "boolean", execCtx);
		b.append("var "+boolVar+";\n");
		b.append("try {\n");
		String eval = DirectiveUtils.evaluateIf(cond, execCtx);
		b.append("if ("+eval+") "+boolVar+" = true;\n");
		b.append("}catch(_err){};\n");
		b.append("if (!"+boolVar+") "+eltVar+".style.display = 'none';\n");
	}

}
