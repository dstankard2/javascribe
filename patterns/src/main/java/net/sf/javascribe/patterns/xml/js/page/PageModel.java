package net.sf.javascribe.patterns.xml.js.page;

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

@Builder
@Getter
@Setter
@XmlConfig
@NoArgsConstructor
@AllArgsConstructor
@Plugin
@XmlRootElement(name="pageModel")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="pageModel",propOrder={ })
public class PageModel extends Component {

	public int getPriority() {
		return PatternPriority.PAGE_MODEL;
	}

	@Builder.Default
	@XmlAttribute(required = true)
	private String pageName = "";

	@Builder.Default
	@XmlAttribute(required = true)
	private String properties = "";

	@Override
	public String getComponentName() {
		return "PageModel["+getPageName()+"]";
	}

}

