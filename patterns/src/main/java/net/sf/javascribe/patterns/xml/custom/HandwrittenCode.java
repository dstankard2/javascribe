package net.sf.javascribe.patterns.xml.custom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;

@Scannable
@XmlRootElement(name="handwrittenCode")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="handwrittenCode",propOrder={ })
public class HandwrittenCode extends ComponentBase {

	public int getPriority() { return priority; }

	@XmlAttribute
	private int priority = 0;
	
	@XmlAttribute
	private String srcRoot = "";

	public String getSrcRoot() {
		return srcRoot;
	}

	public void setSrcRoot(String srcRoot) {
		this.srcRoot = srcRoot;
	}
	
}

