package net.sf.javascribe.patterns.servlet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.patterns.CorePatternConstants;

@Scannable
@XmlRootElement(name="servletWebService")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="servletWebService",propOrder={ })
public class ServletWebService extends ComponentBase {

	public static final String SERVLET_WEB_SERVICE_PKG = "net.sf.javascribe.patterns.servlet.ServletWebService.pkg";
	public static final String SERVLET_WEB_SERVICE_DEFAULT_FILTERS = "net.sf.javascribe.patterns.servlet.ServletWebService.defaultFilters";

	public int getPriority() { return CorePatternConstants.PRIORITY_SERVLET_WEB_SERVICE; }

	@XmlAttribute
	private String webServiceModule = "";

	@XmlAttribute
	private String filters = "";

	@XmlAttribute
	private String requestBody = "";

	@XmlAttribute
	private String path = "";

	@XmlAttribute
	private String sessionDataType = "";

	@XmlAttribute
	private String queryParams = "";

	@XmlAttribute
	private String service = "";

	@XmlAttribute
	private String returnValue = "";

	@XmlAttribute
	private String returnFormat = "";
	
	@XmlAttribute
	private String httpMethod = "";

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getWebServiceModule() {
		return webServiceModule;
	}

	public void setWebServiceModule(String webServiceModule) {
		this.webServiceModule = webServiceModule;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getQueryParams() {
		return queryParams;
	}

	public void setQueryParams(String queryParams) {
		this.queryParams = queryParams;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(String returnValue) {
		this.returnValue = returnValue;
	}

	public String getReturnFormat() {
		return returnFormat;
	}

	public void setReturnFormat(String returnFormat) {
		this.returnFormat = returnFormat;
	}

	public String getSessionDataType() {
		return sessionDataType;
	}

	public void setSessionDataType(String sessionDataType) {
		this.sessionDataType = sessionDataType;
	}

	public String getFilters() {
		return filters;
	}

	public void setFilters(String filters) {
		this.filters = filters;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

}
