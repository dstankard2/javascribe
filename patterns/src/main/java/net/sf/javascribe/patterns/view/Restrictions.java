package net.sf.javascribe.patterns.view;

public enum Restrictions {

	ELEMENT("E"), ATTRIBUTE("A"), CLASS("C");
	
	private String elementType;
	
	private Restrictions(String s) {
		elementType = s;
	}
	
	public String getElementType() {
		return elementType;
	}

}
