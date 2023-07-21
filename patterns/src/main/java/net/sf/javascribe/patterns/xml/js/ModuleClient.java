package net.sf.javascribe.patterns.xml.js;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;

@Plugin
@XmlConfig
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="webService",propOrder={  })
@Getter
@Setter
public class ModuleClient {

	@XmlAttribute
	private String name = "";
	
	@XmlAttribute
	private String ref = "";
	
	@XmlAttribute
	private String module = "";

}

