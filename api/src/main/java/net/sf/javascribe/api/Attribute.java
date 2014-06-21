package net.sf.javascribe.api;

/**
 * Represents a system attribute, and has a name and type.
 * @author DCS
 *
 */
public class Attribute {
	private String name = null;
	private String type = null;

	public Attribute(String name,String type) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
