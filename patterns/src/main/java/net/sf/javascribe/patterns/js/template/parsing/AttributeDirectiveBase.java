package net.sf.javascribe.patterns.js.template.parsing;

public abstract class AttributeDirectiveBase implements AttributeDirective {

	@Override
	public int getPriority() { return 10; }

	@Override
	public int compareTo(AttributeDirective other) {
		return getPriority() - other.getPriority();
	}

}

