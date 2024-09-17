package net.sf.javascribe.patterns.xml.java.http;

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
import net.sf.javascribe.langsupport.java.JavaComponent;
import net.sf.javascribe.patterns.PatternPriority;
import net.sf.javascribe.patterns.http.HttpMethod;

@Getter
@Setter
@XmlConfig
@Plugin
@XmlRootElement(name="endpoint")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="endpoint",propOrder={ "response" })
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Endpoint extends JavaComponent {

	@XmlAttribute
	@Builder.Default
	private String functionName = "";
	
	@XmlAttribute
	@Builder.Default
	private String module = "";

	@XmlAttribute
	private HttpMethod method;

	@XmlAttribute
	@Builder.Default
	private String path = "";
	
	@XmlAttribute
	@Builder.Default
	private String requestBody = "";
	
	@XmlAttribute
	@Builder.Default
	private String requestParameters = "";

	@XmlAttribute
	@Builder.Default
	private Boolean asynch = false;
	
	@XmlAttribute
	@Builder.Default
	private String operation = "";

	@XmlElement
	@Builder.Default
	private List<Response> response = new ArrayList<>();
	
	@XmlTransient
	@Builder.Default
	private String pkg = null;

	@Override
	public int getPriority() {
		return PatternPriority.SERVLET_ENDPOINT;
	}
	
	@XmlTransient
	@Builder.Default
	private String operationResult = null;

	@ConfigProperty(required = true, name = "java.httpendpoint.operationResult",
			description = "Reference to HttpServletRequest object, so it can be used in web service logic", 
			example = "request")
	public void setOperationResult(String operationResult) {
		this.operationResult = operationResult;
	}
	
	public String getOperationResult() {
		return operationResult;
	}

	@Override
	public String getComponentName() {
		if ((getMethod()!=null) && (getPath().trim().length()>0)) {
			return getMethod().name()+" "+getPath();
		} else if ((getMethod()==null) && (getPath().trim().length()==0)) {
			return "HTTP Endpoint <METHOD> <PATH>";
		} else if (getMethod()==null) {
			return "HTTP Endpoint <METHOD> "+getPath();
		}
		return getMethod().name()+" <PATH>";
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public String getRequestParameters() {
		return requestParameters;
	}

	public void setRequestParameters(String requestParameters) {
		this.requestParameters = requestParameters;
	}

	public Boolean getAsynch() {
		return asynch;
	}

	public void setAsynch(Boolean asynch) {
		this.asynch = asynch;
	}

	public String getPkg() {
		return pkg;
	}

	@ConfigProperty(required = true, name = "java.webservice.pkg",
			description = "Sub-package that the servlet class is located in, under the Java root package.", 
			example = "servlet")
	public void setPkg(String pkg) {
		this.pkg = pkg;
	}

}

