package net.sf.javascribe.patterns.xml.js.page;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
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
@XmlRootElement(name="pageFn")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="pageFn",propOrder={ })
public class PageFn extends Component {

	public int getPriority() {
		return PatternPriority.PAGE_FN;
	}

	@Builder.Default
	@XmlAttribute
	private Boolean async = false;
	
	@Builder.Default
	@XmlAttribute
	private String pageName = "";
	
	@Builder.Default
	@XmlAttribute
	private String service = "";
	
	@Builder.Default
	@XmlAttribute
	private String name = "";

	@Builder.Default
	@XmlAttribute
	private String event = "";

	@Builder.Default
	@XmlElement(name = "code")
	private String code = "";
	
	@Override
	public String getComponentName() {
		return "PageFn ["+pageName+"."+name+"]";
	}

}

