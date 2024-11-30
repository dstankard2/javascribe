package net.sf.javascribe.patterns.xml.java.dataobject;

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
import net.sf.javascribe.patterns.PatternPriority;

@Plugin
@XmlConfig
@XmlRootElement(name="jsonObject")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="jsonObject",propOrder={ })
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JsonObject extends Component {

	@Getter
	@Setter
	@XmlAttribute
	@Builder.Default
	private String name = "";
	
	public int getPriority() { return PatternPriority.JSON_OBJECT; }

	public String getComponentName() {
		return "JsonObject:"+getName();
	}

}

