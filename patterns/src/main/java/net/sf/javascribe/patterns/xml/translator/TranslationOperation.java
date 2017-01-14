package net.sf.javascribe.patterns.xml.translator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentConfigElement;

@Scannable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="operation",propOrder={ })
public class TranslationOperation implements ComponentConfigElement {

	@XmlAttribute
	private String name = null;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
