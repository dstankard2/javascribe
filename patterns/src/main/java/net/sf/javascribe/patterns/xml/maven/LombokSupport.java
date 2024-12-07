package net.sf.javascribe.patterns.xml.maven;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import net.sf.javascribe.api.annotation.ConfigProperty;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;
import net.sf.javascribe.api.config.Component;

@Getter
@XmlConfig
@Plugin
@XmlRootElement(name="lombokProcessing")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="lombokProcessing",propOrder={ })
public class LombokSupport extends Component {

	private String lombokVersion = null;

	@ConfigProperty(required = true, name = "maven.dependency.lombok",
			description = "Version of Lombok dependency to be used", example = "1.18.32")
	public void setJavaVersion(String lombokVersion) {
		this.lombokVersion = lombokVersion;
	}

}
