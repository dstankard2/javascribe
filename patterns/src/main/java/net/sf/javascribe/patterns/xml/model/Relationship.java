package net.sf.javascribe.patterns.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="rel",propOrder={ })
public class Relationship {

	@XmlValue
	private String value = null;
	
	@XmlAttribute
	private String sortOwnedBy = "";

	public String getSortOwnedBy() {
		return sortOwnedBy;
	}

	public void setSortOwnedBy(String sortOwnedBy) {
		this.sortOwnedBy = sortOwnedBy;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
