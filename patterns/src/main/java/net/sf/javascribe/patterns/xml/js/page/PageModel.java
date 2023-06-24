package net.sf.javascribe.patterns.xml.js.page;

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
@XmlRootElement(name="pageModel")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="pageModel",propOrder={ })
public class PageModel extends Component {

	public int getPriority() {
		return PatternPriority.PAGE_MODEL;
	}

	@XmlAttribute(required = true)
	private String pageName = "";

	@XmlAttribute(required = true)
	private String properties = "";

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

	@Override
	public String getComponentName() {
		return "PageModel["+getPageName()+"]";
	}

}

