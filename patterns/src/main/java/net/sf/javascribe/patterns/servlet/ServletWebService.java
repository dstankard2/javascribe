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

	@XmlAttribute
	private String webServiceModule = "";

	@XmlAttribute
	private String filters = "";

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

	public int getPriority() { return CorePatternConstants.PRIORITY_SERVLET_WEB_SERVICE; }

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

}
