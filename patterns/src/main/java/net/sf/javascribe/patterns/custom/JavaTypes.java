package net.sf.javascribe.patterns.custom;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.patterns.CorePatternConstants;

@Scannable
@XmlRootElement(name="javaTypes")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="javaTypes",propOrder={ "javaType" })
public class JavaTypes extends ComponentBase {

	@XmlElement
	private List<JavaType> javaType = new ArrayList<JavaType>();
	
	@Override
	public int getPriority() { return CorePatternConstants.PRIORITY_GENERIC_JAVA_CLASSES; }
	
	public List<JavaType> getJavaType() {
		return javaType;
	}

	public void setJavaType(List<JavaType> javaType) {
		this.javaType = javaType;
	}

}

