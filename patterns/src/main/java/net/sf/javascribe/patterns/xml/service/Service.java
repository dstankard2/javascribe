package net.sf.javascribe.patterns.xml.service;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.patterns.CorePatternConstants;

@Scannable
@XmlRootElement(name="service")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="service",propOrder={ "serviceOperation" })
public class Service extends ComponentBase {

	@Override
	public int getPriority() { return CorePatternConstants.PRIORITY_SERVICE; }
	
	@XmlElementRef
	private List<ServiceOperation> serviceOperation = new ArrayList<ServiceOperation>();
	
	@XmlAttribute
	private String result = null;
	
	@XmlAttribute
	private String params = null;
	
	@XmlAttribute
	private String module = null;

	@XmlAttribute
	private String name = null;

	public List<ServiceOperation> getServiceOperation() {
		return serviceOperation;
	}

	public void setServiceOperation(List<ServiceOperation> serviceOperation) {
		this.serviceOperation = serviceOperation;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

