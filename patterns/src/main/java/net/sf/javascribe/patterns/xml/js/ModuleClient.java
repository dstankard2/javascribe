package net.sf.javascribe.patterns.xml.js;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;

@Builder
@Plugin
@XmlConfig
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="webService",propOrder={  })
@Getter
@Setter
public class ModuleClient {

	@Builder.Default
	@XmlAttribute
	private String name = "";
	
	@Builder.Default
	@XmlAttribute
	private String ref = "";
	
	@Builder.Default
	@XmlAttribute
	private String module = "";

}

