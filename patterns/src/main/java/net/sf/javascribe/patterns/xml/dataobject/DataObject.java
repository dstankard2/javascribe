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
@XmlRootElement(name="dataObject")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="dataObject",propOrder={ })
public class DataObject extends ComponentBase {

	public static final String DATA_OBJECT_PKG = "net.sf.javascribe.patterns.dataobject.DataObject.pkg";

	@XmlAttribute
	private String name = null;
	
	@XmlAttribute
	private String attributes = null;

	public int getPriority() { return CorePatternConstants.PRIORITY_DATA_OBJECT; }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}
	
}
