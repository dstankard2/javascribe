package net.sf.javascribe.patterns.xml.java.dataobject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;
import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.patterns.PatternPriority;

@Plugin
@XmlConfig
@XmlRootElement(name="jsonObject")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="jsonObject",propOrder={ })
public class JsonObject extends Component {

	@XmlAttribute
	private String name = "";
	
	public int getPriority() { return PatternPriority.JSON_OBJECT; }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getComponentName() {
		return "JsonObject:"+getName();
	}

}

