package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.view.Directive;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;
import net.sf.javascribe.patterns.view.Restrictions;

@Scannable
public class VarDirective implements Directive {

	@Override
	public Restrictions[] getRestrictions() {
		return new Restrictions[] { Restrictions.ELEMENT };
	}

	@Override
	public String getName() {
		return "js-var";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder b = ctx.getCode();
		String name = ctx.getAttributes().get("name");
		String value = ctx.getAttributes().get("value");
		boolean done = false;

		if (ctx.getExecCtx().getVariableType(name)!=null) {
			throw new JavascribeException("Cannot declare a variable named '"+name+"' as there is already another one");
		}
		String valueExpr = null;
		if ((value.startsWith("${")) && (value.endsWith("}"))) {
			valueExpr = value.substring(2, value.length()-1);
			String finalValue = DirectiveUtils.getValidReference(valueExpr, ctx.getExecCtx());
			if (finalValue!=null) {
				b.append("var "+name+" = "+finalValue+";\n");
				String type = DirectiveUtils.getReferenceType(valueExpr, ctx.getExecCtx());
				ctx.getExecCtx().addVariable(name, type);
				done = true;
			}
		}
		
		if (!done) {
			throw new JavascribeException("Invalid value for js-var directive");
		}
	}

}
