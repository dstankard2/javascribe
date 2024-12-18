package net.sf.javascribe.patterns.xml.java.service;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;
import net.sf.javascribe.patterns.java.service.OperationRenderer;
import net.sf.javascribe.patterns.java.service.SetVarRenderer;

@Getter
@Setter
@Plugin
@XmlConfig
@XmlRootElement(name="setVar")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="setVar",propOrder={ })
public class SetVarOperation extends Operation {

	@XmlAttribute
	private String name;
	
	@XmlAttribute
	private String type;
	
	@XmlAttribute
	private String value;
	
	@Override
	public OperationRenderer getRenderer() { 
		return new SetVarRenderer(this); 
	}

}

