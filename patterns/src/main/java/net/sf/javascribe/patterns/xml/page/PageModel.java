package net.sf.javascribe.patterns.xml.page;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.patterns.CorePatternConstants;

@Scannable
@XmlRootElement(name="pageModel")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="pageModel",propOrder={ "attribute" })
public class PageModel extends ComponentBase {

	@XmlElement
	private List<Attribute> attribute = new ArrayList<Attribute>();
	
	@XmlAttribute
	private String pageName = null;
	
	@Override
	public int getPriority() { return CorePatternConstants.PRIORITY_PAGE_MODEL; }
	
	public List<Attribute> getAttribute() {
		return attribute;
	}

	public void setAttribute(List<Attribute> attribute) {
		this.attribute = attribute;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	
}
