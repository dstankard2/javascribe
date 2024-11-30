package net.sf.javascribe.patterns.xml.java.http;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.javascribe.api.annotation.ConfigProperty;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;
import net.sf.javascribe.langsupport.java.JavaComponent;
import net.sf.javascribe.patterns.PatternPriority;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlConfig
@Plugin
@XmlRootElement(name="webServiceModule")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="webServiceModule",propOrder={ "preprocessing" })
public class WebServiceModuleComponent extends JavaComponent {

	@Builder.Default
	@XmlTransient
	private String uriPrefix = "";

	@ConfigProperty(required = false, name = "java.http.uriPrefix",
			description = "URL prefix for invoking a HTTP module, from the HTTP client's perspective.  For instance, if a reverse proxy will forward requests at a path to the HTTP server.", 
			example = "/rest")
	public void setUriPrefix(String uriPrefix) {
		this.uriPrefix = uriPrefix;
	}

	@Getter
	@Builder.Default
	@XmlTransient
	private String requestRef = "";
	
	@ConfigProperty(required = false, name = "java.http.requestRef",
			description = "Reference to HttpServletRequest object, so it can be used in web service logic", 
			example = "request")
	public void setRequestRef(String requestRef) {
		this.requestRef = requestRef;
	}

	@Getter
	@Builder.Default
	@XmlTransient
	private String responseRef = "";
	
	@ConfigProperty(required = false, name = "java.http.responseRef",
			description = "Reference to HttpServletResponse object, so it can be used in web service logic", 
			example = "response")
	public void setResponseRef(String responseRef) {
		this.responseRef = responseRef;
	}
	
	@XmlTransient
	private String pkg;

	@Getter
	@Setter
	@Builder.Default
	@XmlElement
	private List<Preprocessing> preprocessing = new ArrayList<>();

	@Getter
	@Setter
	@Builder.Default
 	@XmlAttribute
	private String name = "";

	@Getter
	@Setter
	@Builder.Default
	@XmlAttribute
	private String uri = "";

	@Getter
	@Setter
	@XmlAttribute
	@Builder.Default
	private String filterGroup = "";

	public String getComponentName() {
		return "WebServiceModule["+name+"]";
	}

	public int getPriority() {
		return PatternPriority.SERVLET_MODULE;
	}
	
	public String getPkg() {
		return pkg;
	}

	@ConfigProperty(required = true, name = "java.webservice.pkg",
			description = "Sub-package that the servlet class will be created in, under the Java root package.", 
			example = "servlet")
	public void setPkg(String pkg) {
		this.pkg = pkg;
	}

}

