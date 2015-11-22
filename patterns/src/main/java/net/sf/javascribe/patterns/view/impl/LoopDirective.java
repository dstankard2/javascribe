package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.view.AttributeDirectiveBase;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;

@Scannable
public class LoopDirective extends AttributeDirectiveBase {

	@Override
	public int getPriority() { return 2; }
	
	@Override
	public String getAttributeName() {
		return "js-loop";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder b = ctx.getCode();
		CodeExecutionContext execCtx = ctx.getExecCtx();
		
		String a = ctx.getTemplateAttributes().get("js-loop");
		int i = a.indexOf(" in ");
		String eltVar = a.substring(0, i).trim();
		String list = a.substring(i+4).trim();
		String type = DirectiveUtils.getReferenceType(list, execCtx);
		if (type==null) {
			throw new JavascribeException("Couldn't evaluate type for js-loop list variable '"+list+"'");
		}
		String listStr = DirectiveUtils.getValidReference(list, execCtx);
		if (type.startsWith("list/")) type = type.substring(5);
		//String parentNodes = ctx.newVarName("_n", "object", execCtx);
		//execCtx = new CodeExecutionContext(execCtx);
		String in = ctx.newVarName("_i","object",execCtx);
		
		/*
		b.append("try {\n");
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
		b.append("}catch(_err){}\n");
		*/
		
		CodeExecutionContext newCtx = new CodeExecutionContext(execCtx);
		String func = ctx.newVarName("_lf", "function", execCtx);
		b.append("var "+func+" = function("+eltVar+"){\n");
		newCtx.addVariable(eltVar, type);
		ctx.continueRenderElement(newCtx);
		b.append("}\n");
		b.append("try {\n");
		b.append("for(var "+in+"=0;"+in+"<"+listStr+".length;"+in+"++) {\n");
		b.append("var "+eltVar+" = "+listStr+"["+in+"];\n");
		b.append(func+"("+eltVar+");\n}\n");
		b.append("}catch(_err){}\n");
	}

}

