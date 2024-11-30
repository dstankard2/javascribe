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
@XmlRootElement(name="testPattern")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="testPattern",propOrder={  })
@Builder
@XmlConfig
@Plugin
public class TestPattern extends Component {

	@XmlAttribute
	private String name;
	
	public String getComponentName() {
		return "TestPattern["+name+"]";
	}

	@Override
	public int getPriority() {
		return 2000;
	}

}

