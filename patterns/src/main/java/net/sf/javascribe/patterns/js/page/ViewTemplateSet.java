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
@XmlRootElement(name="viewTemplateSet")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="viewTemplateSet",propOrder={ "template" })
public class ViewTemplateSet extends ComponentBase {

	@Override
	public int getPriority() { return CorePatternConstants.PRIORITY_VIEW_TEMPLATE_SET; }

	@XmlAttribute
	private String objName = "";
	
	@XmlElement
	private List<SingleTemplate> template = new ArrayList<SingleTemplate>();
	
	public String getObjName() {
		return objName;
	}

	public void setObjName(String objName) {
		this.objName = objName;
	}

	public List<SingleTemplate> getTemplate() {
		return template;
	}

	public void setTemplate(List<SingleTemplate> template) {
		this.template = template;
	}

}
