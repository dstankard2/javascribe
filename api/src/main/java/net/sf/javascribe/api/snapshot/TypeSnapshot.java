package net.sf.javascribe.api.snapshot;

public class TypeSnapshot {

	private String lang = null;
	private String name = null;

	public TypeSnapshot(String lang, String name) {
		super();
		this.lang = lang;
		this.name = name;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
