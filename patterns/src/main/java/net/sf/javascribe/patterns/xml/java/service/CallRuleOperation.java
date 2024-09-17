package net.sf.javascribe.patterns.xml.java.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;
import net.sf.javascribe.patterns.java.service.CallRuleRenderer;
import net.sf.javascribe.patterns.java.service.OperationRenderer;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Plugin
@XmlConfig
@XmlRootElement(name="callRule")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="callRule",propOrder={ })
public class CallRuleOperation extends Operation {

	@Override 
	public OperationRenderer getRenderer() { return new CallRuleRenderer(this); }

	@Builder.Default
	@XmlAttribute
	private String result = "";
	
	@Builder.Default
	@XmlAttribute
	private String params = "";

	@Builder.Default
	@XmlAttribute
	private String rule = "";
	
}

