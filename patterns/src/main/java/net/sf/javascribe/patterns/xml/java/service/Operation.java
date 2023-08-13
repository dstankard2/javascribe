package net.sf.javascribe.patterns.xml.java.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;
import net.sf.javascribe.patterns.java.service.OperationRenderer;

@Plugin
@XmlConfig
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="serviceOperation",propOrder={  })
@XmlRootElement
public class Operation {

	public Operation() { }
	public OperationRenderer getRenderer() { return null; }

}

