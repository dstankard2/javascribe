package net.sf.javascribe.patterns.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentConfigElement;

@Scannable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="serviceOperation",propOrder={  })
@XmlRootElement
public class ServiceOperation implements ComponentConfigElement {

	public ServiceOperation() { }
	public ServiceOperationRenderer getRenderer() { return null; }
	
}

