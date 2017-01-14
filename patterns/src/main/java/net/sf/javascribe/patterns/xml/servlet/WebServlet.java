package net.sf.javascribe.patterns.xml.servlet;

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
	private String params = "";

	@XmlElement
	private List<ServletForward> forward = new ArrayList<ServletForward>();
	
	@XmlAttribute
	private String name = "";
	
	@XmlAttribute
	private String filters = "";
	
	@XmlAttribute
	private String sessionDataType = "";
	
	@XmlAttribute
	private String service = "";

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

	public List<ServletForward> getForward() {
		return forward;
	}

	public void setForward(List<ServletForward> forward) {
		this.forward = forward;
	}
	
}

