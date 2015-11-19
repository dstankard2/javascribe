package net.sf.javascribe.patterns.view;

public abstract class AttributeDirectiveBase implements AttributeDirective,Comparable<AttributeDirective> {

	@Override
	public int getPriority() { return 10; }

	@Override
	public int compareTo(AttributeDirective other) {
		return getPriority() - other.getPriority();
	}

}

