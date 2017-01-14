package net.sf.javascribe.patterns.xml.custom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="javaType",propOrder={ })
public class JavaType {

	@XmlAttribute
	private String name = "";
	
	@XmlAttribute(name="class")
	private String clazz = "";

	@XmlAttribute(name="import")
	private String im = "";
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getIm() {
		return im;
	}

	public void setIm(String im) {
		this.im = im;
	}
	
}
