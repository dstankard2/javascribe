package net.sf.javascribe.api.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="javascribeConfig",propOrder={ "properties" })
public class JavascribeConfig {

	@XmlAttribute
	private String appName = null;
	
	@XmlAttribute
	private String destRoot = null;
	
	@XmlElement
	private PropertyList properties = new PropertyList();

	public PropertyList getProperties() {
		return properties;
	}

	public void setProperties(PropertyList properties) {
		this.properties = properties;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getDestRoot() {
		return destRoot;
	}

	public void setDestRoot(String destRoot) {
		this.destRoot = destRoot;
	}

}
