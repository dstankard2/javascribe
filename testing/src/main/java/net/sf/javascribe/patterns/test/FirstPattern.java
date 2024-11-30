package net.sf.javascribe.patterns.test;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;
import net.sf.javascribe.api.config.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name="firstPattern")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="firstPattern",propOrder={  })
@Builder
@XmlConfig
@Plugin
public class FirstPattern extends Component {

	@XmlAttribute
	private String serviceName;
	
	public String getComponentName() {
		return "FirstPattern["+serviceName+"]";
	}

	@Override
	public int getPriority() {
		return 10;
	}

}

