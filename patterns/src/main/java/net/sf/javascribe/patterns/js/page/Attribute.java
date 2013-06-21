package net.sf.javascribe.patterns.js.page;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentConfigElement;

@Scannable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="attribute",propOrder={ })
public class Attribute implements ComponentConfigElement {

	@XmlAttribute
	private String onChange = "";
	
	@XmlValue
	private String name = "";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOnChange() {
		return onChange;
	}

	public void setOnChange(String onChange) {
		this.onChange = onChange;
	}
	
}

