package net.sf.javascribe.patterns.xml.java.http;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;
import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.patterns.PatternPriority;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@XmlConfig
@Plugin
@XmlRootElement(name="filterGroup")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="filterGroup",propOrder={ })
public class ServletFilterGroup extends Component {

	public int getPriority() {
		return PatternPriority.SERVLET_FILTER_GROUP;
	}
	
	@Getter
	@Setter
	@Builder.Default
	@XmlAttribute(required = true)
	private String name = "";
	
	@Getter
	@Setter
	@Builder.Default
	@XmlAttribute(required = true)
	private String filters = "";

	@Override
	public String getComponentName() {
		return "ServletFilterGroup["+name+"]";
	}

}
