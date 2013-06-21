package net.sf.javascribe.patterns.service;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;

@Scannable
@XmlRootElement(name="callValidationRule")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="callValidationRule",propOrder={ "operation" })
public class CallValidationRuleOperation extends ServiceOperation implements NestingOperation {

	public ServiceOperationRenderer getRenderer() {
		return new CallValidationRuleRenderer(this); 
	}

	@XmlAttribute
	private String params = null;
	
	@XmlAttribute
	private String service = null;
	
	@XmlElementRef
	private List<ServiceOperation> operation = new ArrayList<ServiceOperation>();

	@Override
	public List<ServiceOperation> getOperation() {
		return operation;
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
	
}

