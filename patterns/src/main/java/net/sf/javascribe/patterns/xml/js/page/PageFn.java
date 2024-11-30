package net.sf.javascribe.patterns.xml.js.page;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
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

