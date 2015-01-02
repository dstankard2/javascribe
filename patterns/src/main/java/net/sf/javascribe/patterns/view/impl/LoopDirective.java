package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.patterns.view.Directive;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;
import net.sf.javascribe.patterns.view.Restrictions;

// This is not scannable because it will be added to the renderer list manually.
public class LoopDirective implements Directive {

	@Override
	public Restrictions[] getRestrictions() {
		return new Restrictions[] { Restrictions.ATTRIBUTE };
	}

	@Override
	public String getName() {
		return "js-loop";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder b = ctx.getCode();
		CodeExecutionContext execCtx = ctx.getExecCtx();
		
		String a = ctx.getAttributes().get("js-loop");
		int i = a.indexOf(" in ");
		String eltVar = a.substring(0, i).trim();
		String list = a.substring(i+4).trim();
		String type = DirectiveUtils.getReferenceType(list, execCtx);
		if (type==null) {
			throw new JavascribeException("Couldn't evaluate type for js-loop list variable '"+list+"'");
		}
		String listStr = DirectiveUtils.getValidReference(list, execCtx);
		if (type.startsWith("list/")) type = type.substring(5);
		String parentNodes = ctx.newVarName("_n", "object", execCtx);
		execCtx = new CodeExecutionContext(execCtx);
		String in = ctx.newVarName("_i","object",execCtx);
		b.append("var "+parentNodes+" = "+ctx.getContainerVarName()+".childNodes;\n");
		b.append("for (var "+in+"=0;"+in+"<"+parentNodes+".length;"+in+"++) {\n");
		b.append("if ("+parentNodes+"["+in+"].classList.contains('"+in+"')){");
		b.append(ctx.getContainerVarName()+".removeChild("+parentNodes+"["+in+"]"+");\n");
		b.append(in+"--;}\n");
		b.append("}\n");
		b.append("for(var "+in+"=0;"+in+"<"+listStr+".length;"+in+"++) {\n");
		b.append("var "+eltVar+" = "+listStr+"["+in+"];\n");
		b.append(ctx.getElementVarName()+" = "+DirectiveUtils.DOCUMENT_REF+".createElement('"+ctx.getElementName()+"');\n");
		b.append(ctx.getElementVarName()+".classList.add('"+ctx.getElementVarName()+"');\n");
		b.append(ctx.getElementVarName()+".classList.add('"+in+"');\n");
		execCtx.addVariable(eltVar, type);
		execCtx.addVariable(in,"integer");
		ctx.continueRenderElement(execCtx);
		b.append("}\n");
	}

}

