package net.sf.javascribe.patterns.xml.tomcat;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.javascribe.api.annotation.ConfigProperty;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;
import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.patterns.PatternPriority;

@Plugin
@XmlConfig
@XmlRootElement(name="embedTomcatJar")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="embedTomcatJar",propOrder={ })
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmbedTomcatJar extends Component {

	public int getPriority() {
		return PatternPriority.EMBED_TOMCAT_JAR;
	}

	@Getter
	@Setter
	@Builder.Default
	@XmlAttribute
	private String jarName = "";
	
	@Getter
	@Setter
	@Builder.Default
	@XmlAttribute
	private String context = "";

	@Getter
	@Setter
	@XmlAttribute
	private Integer port;

	@XmlTransient
	private String pkg;
	
	@XmlTransient
	private Integer debugPort;
	
	public String getPkg() {
		return pkg;
	}
	
	public String getComponentName() {
		if (jarName.length() >0) {
			return jarName+".jar";
		}
		return super.getComponentName();
	}
	
	@ConfigProperty(required = true, name = JavaUtils.CONFIG_PROPERTY_JAVA_ROOT_PACKAGE,
			description = "Root Java package of the code distribution.", example = "net.sf.javascribe")
	public void setJavaRootPackage(String pkg) {
		this.pkg = pkg;
	}

	@ConfigProperty(required = false, name = "tomcat.debugPort",
			description = "TCP/IP Port to start Tomcat JDWP debugging on", example = "3333")
	public void setDebugPort(Integer debugPort) {
		this.debugPort = debugPort;
	}
	
	public Integer getDebugPort() {
		return debugPort;
	}

}

