package net.sf.javascribe.patterns.xml.service;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.service.NestingOperation;

@Scannable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="errorCase",propOrder={ "operation" })
public class ErrorCaseOperation extends ServiceOperation implements NestingOperation {

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
