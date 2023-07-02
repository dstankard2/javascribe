package net.sf.javascribe.api;

import net.sf.javascribe.api.types.VariableType;

public class PropertyEntry {

	private String name;
	private VariableType type;
	private boolean originator = false;

	public PropertyEntry(String name,VariableType type,boolean originator) {
		super();
		this.name = name;
		this.type = type;
		this.originator = originator;
	}

	public boolean isOriginator() {
		return originator;
	}

	public void setOriginator(boolean originator) {
		this.originator = originator;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public VariableType getType() {
		return type;
	}

	public void setType(VariableType type) {
		this.type = type;
	}

}
