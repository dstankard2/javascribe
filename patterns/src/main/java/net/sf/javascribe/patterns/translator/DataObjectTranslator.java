package net.sf.javascribe.patterns.translator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.patterns.CorePatternConstants;

@Scannable
@XmlRootElement(name="dataObjectTranslator")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="dataObjectTranslator",propOrder={ })
public class DataObjectTranslator extends ComponentBase {

	@Override
	public int getPriority() { return CorePatternConstants.PRIORITY_DATA_TRANSLATOR; }

	@XmlAttribute
	private String params = null;
	@XmlAttribute
	private String returnType = null;
	@XmlAttribute
	private String object = null;
	@XmlAttribute
	private String name = null;

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

}
