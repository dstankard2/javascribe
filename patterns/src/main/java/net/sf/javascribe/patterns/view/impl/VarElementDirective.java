package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.ElementDirective;

@Scannable
public class VarElementDirective implements ElementDirective {

	@Override
	public String getElementName() {
		return "js-var";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder b = ctx.getCode();
		String name = ctx.getDomAttributes().get("name");
		String type = ctx.getDomAttributes().get("type");

		if ((name==null) || (type==null) || (name.trim().length()==0) || (type.trim().length()==0)) {
			throw new JavascribeException("js-var element directive requires 'name' and 'type'");
		}
		if (ctx.getExecCtx().getVariableType(name)!=null) {
			throw new JavascribeException("Cannot declare a variable named '"+name+"' as there is already another one");
		}
		b.append("var "+name+";\n");
		ctx.getExecCtx().addVariable(name, type);
		/*
		if ((value.startsWith("${")) && (value.endsWith("}"))) {
			valueExpr = value.substring(2, value.length()-1);
			String finalValue = DirectiveUtils.getValidReference(valueExpr, ctx.getExecCtx());
			if (finalValue!=null) {
				b.append("var "+name+" = "+finalValue+";\n");
				String type = DirectiveUtils.getReferenceType(valueExpr, ctx.getExecCtx());
				ctx.getExecCtx().addVariable(name, type);
				done = true;
			} else {
				String type = ctx.getDomAttributes().get("type");
				if (type!=null) {
					b.append("var "+name+" = "+value+";\n");
					ctx.getExecCtx().addVariable(name, type);
					done = true;
				}
			}
		}
		*/
		
	}

}
