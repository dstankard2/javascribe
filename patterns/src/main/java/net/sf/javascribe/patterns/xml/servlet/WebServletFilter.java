package net.sf.javascribe.patterns.xml.servlet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.patterns.CorePatternConstants;

@Scannable
@XmlRootElement(name="webServletFilter")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="webServletFilter",propOrder={  })
public class WebServletFilter extends ComponentBase {

	public int getPriority() { return CorePatternConstants.PRIORITY_SERVLET_FILTER; }

	@XmlAttribute
	private String name = null;

	@XmlAttribute
	private String className = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}
