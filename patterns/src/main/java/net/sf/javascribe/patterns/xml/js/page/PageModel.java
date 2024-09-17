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

