package net.sf.javascribe.patterns.xml.java.dataobject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataObject extends JavaComponent {

	@Getter
	@XmlTransient
	@Builder.Default
	private String pkg = null;

	@Getter
	@XmlAttribute(name = "extends")
	@Builder.Default
	private String extend = "";

	@Getter
	@XmlAttribute(name = "properties")
	@Builder.Default
	private String properties = "";

	@Getter
	@XmlAttribute
	@Builder.Default
	private String name = "";
	
	public int getPriority() {
		return PatternPriority.DATA_OBJECT;
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
