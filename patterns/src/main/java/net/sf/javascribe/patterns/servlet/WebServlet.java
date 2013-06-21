package net.sf.javascribe.patterns.servlet;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.patterns.CorePatternConstants;

@Scannable
@XmlRootElement(name="webServlet")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="webServlet",propOrder={ })
public class WebServlet extends ComponentBase {

	public int getPriority() { return CorePatternConstants.PRIORITY_SERVLET_EVENT; }

	@XmlAttribute
	private String params = null;

	@XmlElement
	private List<ServletForward> forward = new ArrayList<ServletForward>();
	
	/*
	@XmlAttribute
	private String invalidForward = null;
	
	@XmlAttribute
	private String validForward = null;
	
	@XmlAttribute
	private String errorForward = null;
	
	@XmlAttribute
	private String defaultForward = null;
	*/
	
	@XmlAttribute
	private String name = null;
	
	@XmlAttribute
	private String filters = null;
	
	@XmlAttribute
	private String sessionDataType = null;
	
	@XmlAttribute
	private String service = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFilters() {
		return filters;
	}

	public void setFilters(String filters) {
		this.filters = filters;
	}

	public String getSessionDataType() {
		return sessionDataType;
	}

	public void setSessionDataType(String sessionDataType) {
		this.sessionDataType = sessionDataType;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	/*
	public String getInvalidForward() {
		return invalidForward;
	}

	public void setInvalidForward(String invalidForward) {
		this.invalidForward = invalidForward;
	}

	public String getValidForward() {
		return validForward;
	}

	public void setValidForward(String validForward) {
		this.validForward = validForward;
	}

	public String getErrorForward() {
		return errorForward;
	}

	public void setErrorForward(String errorForward) {
		this.errorForward = errorForward;
	}

	public String getDefaultForward() {
		return defaultForward;
	}

	public void setDefaultForward(String defaultForward) {
		this.defaultForward = defaultForward;
	}
	*/

	public List<ServletForward> getForward() {
		return forward;
	}

	public void setForward(List<ServletForward> forward) {
		this.forward = forward;
	}
	
}

