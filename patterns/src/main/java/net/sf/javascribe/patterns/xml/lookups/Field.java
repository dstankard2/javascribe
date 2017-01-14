package net.sf.javascribe.patterns.xml.lookups;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="field",propOrder={"value" })
public class Field {

	@XmlAttribute
	private String name = null;
	
	@XmlElement
	private List<FieldValue> value = new ArrayList<FieldValue>();

	public List<FieldValue> getValue() {
		return value;
	}

	public void setValue(List<FieldValue> value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
