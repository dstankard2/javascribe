package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.ElementDirective;

@Scannable
public class ImportDirective implements ElementDirective {

	@Override
	public String getElementName() {
		return "js-import";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String ref = ctx.getDomAttributes().get("ref");
		StringBuilder code = ctx.getCode();
		
		if ((ref==null) || (ref.trim().length()==0)) {
			throw new JavascribeException("Directive js-import requires a 'ref' to import");
		}
		if (ctx.getExecCtx().getVariableType(ref)!=null) return;
		String typeName = ctx.getProcessorContext().getAttributeType(ref);
		if (typeName==null) {
			throw new JavascribeException("Directive js-import found an invalid ref '"+ref+"'");
		}
		
		code.append("var "+ref+" = window."+ref+";\n");
		ctx.getExecCtx().addVariable(ref, typeName);
	}

}

