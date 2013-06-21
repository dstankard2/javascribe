package net.sf.javascribe.patterns.js.page;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.patterns.CorePatternConstants;

@Scannable
@XmlRootElement(name="page")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="page",propOrder={  })
public class Page extends ComponentBase {

	@XmlAttribute
	private String pageName = null;

	@Override
	public int getPriority() { return CorePatternConstants.PRIORITY_PAGE; }
	
	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

}
