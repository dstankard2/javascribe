package net.sf.javascribe.patterns.xml.js.page;

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
import net.sf.javascribe.patterns.xml.java.dataobject.DataObject;

@Builder
@XmlConfig
@Plugin
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name="page")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="page",propOrder={ })
public class Page extends Component {

	public int getPriority() {
		return PatternPriority.PAGE;
	}

	@Getter
	@Setter
	@Builder.Default
	@XmlAttribute(required = true)
	private String name = "";

	@Override
	public String getComponentName() {
		return "JavascriptPage[name=\""+name+"\"]";
	}
}
