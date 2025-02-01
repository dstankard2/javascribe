package net.sf.javascribe.patterns.xml.java.dataobject;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.ConfigProperty;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;
import net.sf.javascribe.langsupport.java.JavaComponent;
import net.sf.javascribe.patterns.PatternPriority;

@XmlConfig
@Plugin
@XmlRootElement(name="classification")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="classification",propOrder={ })
public class Classification extends JavaComponent {

	@XmlTransient
	private String pkg = null;
	
	@XmlAttribute
	private String name = "";
	
	@XmlAttribute
	private String properties = "";

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

	@XmlAttribute(name = "extends")
	private String extend = "";

	@Override
	public String getComponentName() {
		return "Classification["+name+"]";
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExtend() {
		return extend;
	}

	public void setExtend(String extend) {
		this.extend = extend;
	}

	public int getPriority() {
		return PatternPriority.CLASSIFICATION;
	}

	public String getPkg() {
		return pkg;
	}

	@ConfigProperty(required = true, name = "java.classification.pkg",
			description = "Sub-package that the classification Java interface will be created in, under the Java root package.", 
			example = "dto")
	public void setPkg(String pkg) {
		this.pkg = pkg;
	}

}
