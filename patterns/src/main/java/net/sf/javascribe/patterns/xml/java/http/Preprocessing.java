package net.sf.javascribe.patterns.xml.java.http;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@XmlConfig
@Plugin
@XmlRootElement(name="preprocessing")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="preprocessing",propOrder={  })
public class Preprocessing {

	@Builder.Default
	@XmlAttribute
	private String ref = "";
	
	@Builder.Default
	@XmlAttribute
	private String rule = "";
	
	@Builder.Default
	@XmlAttribute
	private String source = "";

}
