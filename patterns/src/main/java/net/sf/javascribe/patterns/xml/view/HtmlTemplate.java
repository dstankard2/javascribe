package net.sf.javascribe.patterns.xml.view;

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
@XmlRootElement(name="htmlTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="htmlTemplate",propOrder={ "templateText" })
public class HtmlTemplate extends ComponentBase {

	@Override
	public int getPriority() { return CorePatternConstants.PRIORITY_HTML_TEMPLATE; }

	@XmlAttribute
	private String objName = "";
	
	@XmlAttribute
	private String templateName = "";
	
	@XmlAttribute
	private String objRef = "";
	
	@XmlElement(required=true)
	private TemplateText templateText = null;

	public String getObjName() {
		return objName;
	}
	public void setObjName(String objName) {
		this.objName = objName;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String getObjRef() {
		return objRef;
	}
	public void setObjRef(String objRef) {
		this.objRef = objRef;
	}
	public TemplateText getTemplateText() {
		return templateText;
	}
	public void setTemplateText(TemplateText templateText) {
		this.templateText = templateText;
	}
	
}
