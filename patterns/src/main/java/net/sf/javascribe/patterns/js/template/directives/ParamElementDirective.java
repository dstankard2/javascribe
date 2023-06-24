package net.sf.javascribe.patterns.js.template.directives;

import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.types.ServiceOperation;
import net.sf.javascribe.patterns.js.template.parsing.DirectiveContext;
import net.sf.javascribe.patterns.js.template.parsing.ElementDirective;

@Plugin
public class ParamElementDirective implements ElementDirective {

	@Override
	public String getElementName() {
		return "js-param";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String name = ctx.getDomAttribute("name");
		String type = ctx.getDomAttribute("type");

		if ((name==null) || (name.trim().length()==0)) {
			throw new JavascribeException("Directive js-param requires a name");
		}
		
		if ((type==null) || (type.trim().length()==0)) {
			type = ctx.getProcessorContext().getSystemAttribute(name);
		}
		if (type==null) throw new JavascribeException("Couldn't find a type for template parameter '"+name+"'");
		ServiceOperation op = ctx.getFunction();
		if (op.getParamNames().contains(name)) {
			throw new JavascribeException("Template function already has a parameter called '"+name+"'");
		}
		op.addParam(name, type);
		ctx.getExecCtx().addVariable(name, type);
	}

}
