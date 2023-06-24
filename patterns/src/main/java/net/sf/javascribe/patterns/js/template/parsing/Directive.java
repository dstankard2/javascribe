package net.sf.javascribe.patterns.js.template.parsing;

import net.sf.javascribe.api.exception.JavascribeException;

public interface Directive {

	public void generateCode(DirectiveContext ctx) throws JavascribeException;

}

