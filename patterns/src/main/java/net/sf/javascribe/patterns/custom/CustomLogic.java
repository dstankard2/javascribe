package net.sf.javascribe.patterns.custom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.patterns.CorePatternConstants;

@Scannable
@XmlRootElement(name="customLogic")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="customLogic",propOrder={ })
public class CustomLogic extends ComponentBase {

	@XmlAttribute
	private String src = "";
	
	@XmlAttribute
	private String pkg = "";
	
	@XmlAttribute
	private String serviceGroupName = "";
	
	@Override
	public int getPriority() { return CorePatternConstants.PRIORITY_CUSTOM_LOGIC; }
	
	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getPkg() {
		return pkg;
	}

	public void setPkg(String pkg) {
		this.pkg = pkg;
	}

	public String getServiceGroupName() {
		return serviceGroupName;
	}

	public void setServiceGroupName(String serviceGroupName) {
		this.serviceGroupName = serviceGroupName;
	}

}

