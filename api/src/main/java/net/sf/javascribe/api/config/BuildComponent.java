package net.sf.javascribe.api.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;

@Plugin
@XmlConfig
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="buildComponent",propOrder={ })
@XmlRootElement(name="buildComponent")
public abstract class BuildComponent extends Component {

	@Getter
	@Setter
	@XmlAttribute
	private String id;
	
	@XmlAttribute
	private String buildCommand;

	@XmlAttribute
	private String deployCommand;

	public String getBuildCommand() {
		return buildCommand;
	}
	public void setBuildCommand(String buildCommand) {
		this.buildCommand = buildCommand;
	}
	public String getDeployCommand() {
		return deployCommand;
	}
	public void setDeployCommand(String deployCommand) {
		this.deployCommand = deployCommand;
	}

}
