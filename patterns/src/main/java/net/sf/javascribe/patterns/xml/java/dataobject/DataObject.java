package net.sf.javascribe.patterns.xml.java.dataobject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import net.sf.javascribe.api.annotation.ConfigProperty;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;
import net.sf.javascribe.langsupport.java.JavaComponent;
import net.sf.javascribe.patterns.PatternPriority;

@XmlConfig
@Plugin
@XmlRootElement(name="dataObject")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="dataObject",propOrder={ })
public class DataObject extends JavaComponent {

	@XmlTransient
	private String pkg = null;

	@Getter
	@XmlAttribute(name = "extends")
	private String extend = "";

	@Getter
	@XmlAttribute(name = "properties")
	private String properties = "";

	@Getter
	@XmlAttribute
	private String name = "";
	
	public int getPriority() {
		return PatternPriority.DATA_OBJECT;
	}

	public String getPkg() {
		return pkg;
	}

	@ConfigProperty(required = true, name = "java.dataObject.package",
			description = "Sub-package under the java root package that the data object class will be created in.", 
			example = "dto")
	public void setPkg(String pkg) {
		this.pkg = pkg;
	}

	@Override
	public String getComponentName() {
		return "DataObject:"+getName();
	}

}
