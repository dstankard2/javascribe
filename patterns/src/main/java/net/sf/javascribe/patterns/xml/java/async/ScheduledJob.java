
package net.sf.javascribe.patterns.xml.java.async;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import net.sf.javascribe.api.annotation.ConfigProperty;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;
import net.sf.javascribe.langsupport.java.JavaComponent;
import net.sf.javascribe.patterns.PatternPriority;

@XmlConfig
@Plugin
@XmlRootElement(name="scheduledJob")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="scheduledJob",propOrder={ })
public class ScheduledJob extends JavaComponent {

	@XmlTransient
	private String pkg = null;

	@Getter
	@XmlAttribute(name = "cronString")
	private String cronString = "";

	@Getter
	@XmlAttribute
	private String name = "";
	
	@Getter
	@XmlAttribute
	private String rule = "";
	
	public int getPriority() {
		return PatternPriority.SCHEDULED_JOB;
	}

	public String getPkg() {
		return pkg;
	}

	@ConfigProperty(required = true, name = "java.async.package",
			description = "Sub-package under the java root package that the scheduled job class will be created in.", 
			example = "dto")
	public void setPkg(String pkg) {
		this.pkg = pkg;
	}

	@Override
	public String getComponentName() {
		return "ScheduledJob:"+getName();
	}

}
