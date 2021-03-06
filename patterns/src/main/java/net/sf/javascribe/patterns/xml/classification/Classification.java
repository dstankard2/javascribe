package net.sf.javascribe.patterns.xml.classification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.patterns.CorePatternConstants;

@Scannable
@XmlRootElement(name="classification")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="classification",propOrder={ })
public class Classification extends ComponentBase {

	public static final String CLASSIFICATION_PKG = "net.sf.javascribe.patterns.classification.Classification.pkg";

	@XmlTransient
	private ProcessorContext myProcessorContext = null;
	
	public ProcessorContext getMyProcessorContext() {
		return myProcessorContext;
	}

	public void setMyProcessorContext(ProcessorContext myProcessorContext) {
		this.myProcessorContext = myProcessorContext;
	}

	@XmlAttribute
	private String name = "";
	
	@XmlAttribute
	private String attributes = "";

	@XmlAttribute(name="extends")
	private String ext = "";
	
	/**
	 * Depends on domain data and entity manager
	 */
	@Override
	public int getPriority() { return CorePatternConstants.PRIORITY_CLASSIFICATION; }
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

}
