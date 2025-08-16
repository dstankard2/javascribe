package net.sf.javascribe.patterns.xml.java.service;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;
import net.sf.javascribe.patterns.java.service.JpaTxRenderer;
import net.sf.javascribe.patterns.java.service.OperationRenderer;

@Getter
@Setter
@Plugin
@XmlConfig
@XmlRootElement(name="jpaTx")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="jpaTx",propOrder={ "operation" })
public class JpaTxOperation extends Operation implements NestingOperation {

	@Override
	public OperationRenderer getRenderer() { return new JpaTxRenderer(this); }

	@XmlElementRef
	private List<Operation> operation = new ArrayList<Operation>();
	
	@XmlAttribute
	private String locator = "";
	
	@XmlAttribute
	private Boolean commit = Boolean.FALSE;
	
	@XmlAttribute
	private String ref = "";
	
}

