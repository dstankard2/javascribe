package net.sf.javascribe.patterns.xml.page;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentConfigElement;

@Scannable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="code",propOrder={ })
public class JsCode implements ComponentConfigElement {

	@XmlValue
	private String value = "";

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}

