package net.sf.javascribe.patterns.xml.java.handwritten;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;
import net.sf.javascribe.api.config.Component;

@XmlConfig
@Plugin
@XmlRootElement(name="userCode")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="userCode",propOrder={ })
public class HandwrittenCode extends Component {

	@XmlAttribute(required=true)
	private String path = "";
	
	@XmlAttribute
	private int priority = 0;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getComponentName() {
		return "HandwrittenCode at '"+path+"'";
	}

}
