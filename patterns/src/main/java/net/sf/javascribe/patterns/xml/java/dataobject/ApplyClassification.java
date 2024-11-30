package net.sf.javascribe.patterns.xml.java.dataobject;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.RequiredXml;
import net.sf.javascribe.api.annotation.XmlConfig;
import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.patterns.PatternPriority;

@XmlConfig
@Plugin
@XmlRootElement(name="applyClassification")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="applyClassification",propOrder={ })
public class ApplyClassification extends Component {

	@XmlAttribute
	private Boolean autoApply = Boolean.FALSE;

	@RequiredXml
	@XmlAttribute
	private String classificationName = "";
	
	@RequiredXml
	@XmlElement
	private List<String> object = new ArrayList<>();

	public Boolean getAutoApply() {
		return autoApply;
	}

	public void setAutoApply(Boolean autoApply) {
		this.autoApply = autoApply;
	}

	public String getClassificationName() {
		return classificationName;
	}

	public void setClassificationName(String classificationName) {
		this.classificationName = classificationName;
	}

	public List<String> getObject() {
		return object;
	}

	public void setObject(List<String> object) {
		this.object = object;
	}
	
	@Override
	public int getPriority() {
		return PatternPriority.APPLY_CLASSIFICATION;
	}

}

