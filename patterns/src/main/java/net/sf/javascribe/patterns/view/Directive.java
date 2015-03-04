package net.sf.javascribe.patterns.view;

import net.sf.javascribe.api.JavascribeException;

public interface Directive {

	public void generateCode(DirectiveContext ctx) throws JavascribeException;

}

