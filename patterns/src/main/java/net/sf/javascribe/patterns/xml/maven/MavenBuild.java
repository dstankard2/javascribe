package net.sf.javascribe.patterns.xml.maven;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.javascribe.api.annotation.ConfigProperty;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;
import net.sf.javascribe.api.config.BuildComponent;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Plugin
@XmlConfig
@XmlRootElement(name="project")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="project",propOrder={ "description", "modules", "dependencies" })
public class MavenBuild extends BuildComponent {

	@Builder.Default
	@XmlElement
	private Dependencies dependencies = new Dependencies();
	
	@Builder.Default
	@XmlElement
	private Modules modules = new Modules();

	@Builder.Default
	@XmlElement
	private String description = "";
	
	@Builder.Default
	@XmlAttribute
	private String artifact = "";

	@Builder.Default
	@XmlAttribute
	private String parent = "";

	@Builder.Default
	@XmlAttribute
	private String packaging = "";
	
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@Builder.Default
	@XmlTransient
	private String javaVersion = "";
	
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@Builder.Default
	@XmlTransient
	private String buildPhases = "";

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@Builder.Default
	@XmlTransient
	private String deployPhases = "";

	@Override
	public String getComponentName() {
		return "Mavenbuild["+getArtifact()+"]";
	}

	@ConfigProperty(required = true, name = "maven.java.version",
			description = "Java compiler target version for Maven", example = "9")
	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}
	
	public String getJavaVersion() {
		return this.javaVersion;
	}

	@ConfigProperty(required = true, name = "maven.build.phases",
			description = "Maven build phase(s) to execute when Javascribe builds the application", example = "compile,package")
	public void setBuildPhases(String buildPhases) {
		this.buildPhases = buildPhases;
	}
	
	public String getBuildPhases() {
		return this.buildPhases;
	}

	@ConfigProperty(required = true, name = "maven.deploy.phases",
			description = "Maven build phase(s) to execute when Javascribe deploys the application", example = "install")
	public void setDeployPhases(String deployPhases) {
		this.deployPhases = deployPhases;
	}
	
	public String getDeployPhases() {
		return this.deployPhases;
	}

}
