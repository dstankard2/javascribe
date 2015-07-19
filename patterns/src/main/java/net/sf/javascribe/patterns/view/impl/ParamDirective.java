package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.javascript.JavascriptFunctionType;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.ElementDirective;

@Scannable
public class ParamDirective implements ElementDirective {

	@Override
	public String getElementName() {
		return "js-param";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String name = ctx.getDomAttributes().get("name");
		String type = ctx.getDomAttributes().get("type");
		
		if ((name==null) || (name.trim().length()==0)) {
			throw new JavascribeException("Directive js-param requires a name");
		}
		
		if ((type==null) || (type.trim().length()==0)) {
			type = ctx.getProcessorContext().getAttributeType(name);
			if (type==null) throw new JavascribeException("Couldn't find a type for template parameter '"+name+"'");
		}
		
		JavascriptFunctionType fn = ctx.getFunction();
		fn.addParam(name, type);
		ctx.getExecCtx().addVariable(name, type);
	}

}

