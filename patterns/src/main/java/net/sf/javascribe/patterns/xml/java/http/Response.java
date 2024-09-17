package net.sf.javascribe.patterns.xml.java.http;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Plugin
@XmlConfig
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="response",propOrder={  })
public class Response {

	@Builder.Default
	@XmlAttribute
	private String condition = "";
	
	@Builder.Default
	@XmlAttribute
	private Integer httpStatus = null;

	@Builder.Default
	@XmlAttribute
	private String responseBody = "";

}

