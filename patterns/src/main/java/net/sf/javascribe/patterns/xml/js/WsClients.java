package net.sf.javascribe.patterns.xml.js;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

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
import net.sf.javascribe.patterns.xml.js.page.Page;

@Builder
@XmlConfig
@NoArgsConstructor
@AllArgsConstructor
@Plugin
@XmlRootElement(name="wsClients")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="wsClients",propOrder={ "moduleClient" })
@Getter
@Setter
public class WsClients extends Component {

	@Builder.Default
	@XmlElement
	private List<ModuleClient> moduleClient = new ArrayList<>();
	
	@Builder.Default
	@XmlAttribute
	private String buildId = "";

	@Builder.Default
	@XmlTransient
	private String preprocessing = "";

	@Builder.Default
	@XmlTransient
	private String ajaxProvider = "";

	@Builder.Default
	@XmlAttribute
	private String urlPrefix = "";
	
	public int getPriority() {
		return PatternPriority.WS_CLIENT;
	}

	@ConfigProperty(required = false, name = "wsClient.requestPreprocess",
			description = "API to invoke before a AJAX web service call", example = "window.service.preprocessRequest")
	public void setPreprocessing(String preprocessing) {
		this.preprocessing = preprocessing;
	}
	
	public String getPreprocessing() {
		return this.preprocessing;
	}
	
	@ConfigProperty(required = false, name = "wsClient.ajaxProvider",
			description = "Name of provider for WS Client processing (defaults to 'XMLHttpRequest')", example = "'Fetch' or 'XMLHttpRequest'")
	public void setAjaxProvider(String ajaxProvider) {
		this.ajaxProvider = ajaxProvider;
	}
	
	public String getAjaxProvider() {
		return this.ajaxProvider;
	}

	@Override
	public String getComponentName() {
		return "WsClients["+urlPrefix+"/*]";
	}

}

