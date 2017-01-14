package net.sf.javascribe.patterns.xml.dataobject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.patterns.CorePatternConstants;

@Scannable
@XmlRootElement(name="jsonObject")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="jsonObject",propOrder={ })
public class JsonObject extends ComponentBase {

	@XmlAttribute
	private String name = "";
	
	public int getPriority() { return CorePatternConstants.PRIORITY_SERVICE+1; }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

