package net.sf.javascribe.patterns.xml.view;

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
@XmlRootElement(name="templateSet")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="templateSet",propOrder={ "template" })
public class TemplateSet extends ComponentBase {

	@Override
	public int getPriority() { return CorePatternConstants.PRIORITY_VIEW_TEMPLATE_SET; }

	@XmlAttribute
	private String objName = "";
	
	@XmlAttribute
	private String ref = "";
	
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

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

}
