package net.sf.javascribe.patterns.xml.page;

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
@XmlRootElement(name="fn")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="fn",propOrder={ "code" })
public class PageFunc extends ComponentBase {

	@Override
	public int getPriority() { return CorePatternConstants.PRIORITY_PAGE_FUNCTION; }
	
	@XmlAttribute
	private String pageName = null;
	
	@XmlElement
	private JsCode code = null;
	
	@XmlAttribute
	private String params = null;
	
	@XmlAttribute
	private String name = null;

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public JsCode getCode() {
		return code;
	}

	public void setCode(JsCode code) {
		this.code = code;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

