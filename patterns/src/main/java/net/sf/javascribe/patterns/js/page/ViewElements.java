package net.sf.javascribe.patterns.js.page;

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
@XmlRootElement(name="viewElements")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="viewElements",propOrder={ "element" })
public class ViewElements extends ComponentBase {

	@XmlElement
	private List<Element> element = new ArrayList<Element>();
	
	@XmlAttribute
	private String pageName = null;
	
	@Override
	public int getPriority() { return CorePatternConstants.PRIORITY_PAGE_VIEW_ELEMENTS; }
	
	public List<Element> getElement() {
		return element;
	}

	public void setElement(List<Element> element) {
		this.element = element;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

}

