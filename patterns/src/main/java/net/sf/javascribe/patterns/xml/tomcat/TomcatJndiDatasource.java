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
import net.sf.javascribe.patterns.PatternPriority;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Plugin
@XmlConfig
@XmlRootElement(name="tomcatJndiDatasource")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="tomcatJndiDatasource",propOrder={ })
public class TomcatJndiDatasource extends Component {

	public int getPriority() {
		return PatternPriority.EMBED_TOMCAT_JAR;
	}

	@Getter
	@Setter
	@Builder.Default
	@XmlAttribute
	private String name = "";
	
	@Getter
	@Setter
	@Builder.Default
	@XmlAttribute
	private String username = "";
	
	@Getter
	@Setter
	@Builder.Default
	@XmlAttribute
	private String password = "";
	
	@Getter
	@Setter
	@Builder.Default
	@XmlAttribute
	private String url = "";
	
	@Builder.Default
	@XmlTransient
	private String driverClass = "";
	
	public String getComponentName() {
		return "TomcatJndiDatasource:"+name;
	}

	public String getDriverClass() {
		return driverClass;
	}

	@ConfigProperty(description = "JDBC Driver Class", example = "com.mysql.jdbc.Driver", name = "tomcat.jndi.datasource.driverClass", required = true)
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

}

