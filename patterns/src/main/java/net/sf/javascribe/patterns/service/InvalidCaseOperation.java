package net.sf.javascribe.patterns.service;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;

@Scannable
@XmlRootElement(name="invalidCase")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="invalidCase",propOrder={ "operation" })
public class InvalidCaseOperation extends ServiceOperation implements NestingOperation {

	public ServiceOperationRenderer getRenderer() { return new InvalidCaseRenderer(this); }

	@XmlElementRef
	private List<ServiceOperation> operation = new ArrayList<ServiceOperation>();
	
	@Override
	public List<ServiceOperation> getOperation() {
		return operation;
	}

	public void setOperation(List<ServiceOperation> operation) {
		this.operation = operation;
	}

}
