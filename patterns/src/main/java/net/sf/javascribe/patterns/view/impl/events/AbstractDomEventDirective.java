package net.sf.javascribe.patterns.view.impl.events;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.patterns.view.Directive;
import net.sf.javascribe.patterns.view.DirectiveContext;

public abstract class AbstractDomEventDirective implements Directive {

	@Override
	public abstract void generateCode(DirectiveContext ctx) throws JavascribeException;

}
