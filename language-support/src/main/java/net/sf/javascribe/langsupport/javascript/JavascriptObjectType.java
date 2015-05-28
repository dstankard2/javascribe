package net.sf.javascribe.langsupport.javascript;

// Represents a generic Javascript object
public class JavascriptObjectType extends JavascriptBaseObjectType {
	private String name = null;
	
	public JavascriptObjectType(String name) {
		super();
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
}
