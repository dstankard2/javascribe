package net.sf.javascribe.patterns.xml.java.http;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;
import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.patterns.PatternPriority;

@XmlConfig
@Plugin
@XmlRootElement(name="filterGroup")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="filterGroup",propOrder={ })
public class ServletFilterGroup extends Component {

	public int getPriority() {
		return PatternPriority.SERVLET_FILTER_GROUP;
	}
	
	@XmlAttribute(required = true)
	private String name = "";
	
	@XmlAttribute(required = true)
	private String filters = "";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFilters() {
		return filters;
	}

	public void setFilters(String filters) {
		this.filters = filters;
	}

}
