package net.sf.javascribe.patterns.js.template.parsing;

public interface AttributeDirective extends Directive,Comparable<AttributeDirective> {

	public String getAttributeName();
	public int getPriority();

}
