package net.sf.javascribe.patterns.quartz;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.patterns.CorePatternConstants;

@Scannable
@XmlRootElement(name="scheduledJob")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="scheduledJob",propOrder={ })
public class ScheduledJob extends ComponentBase {
	
	public int getPriority() { return CorePatternConstants.PRIORITY_SCHEDULED_JOB; }
	
	@XmlAttribute
	private String rule = null;
	
	@XmlAttribute
	private String params = null;

	@XmlAttribute
	private String cronString = null;
	
	@XmlAttribute
	private String name = null;

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getCronString() {
		return cronString;
	}

	public void setCronString(String cronString) {
		this.cronString = cronString;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}

