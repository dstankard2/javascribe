package net.sf.javascribe.patterns.xml.java.service;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.javascribe.api.annotation.ConfigProperty;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;
import net.sf.javascribe.langsupport.java.JavaComponent;
import net.sf.javascribe.patterns.PatternPriority;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@XmlConfig
@Plugin
@XmlRootElement(name="service")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="service",propOrder={ })
public class Service extends JavaComponent {

	@Builder.Default
	private String pkg = null;

	@Getter
	@Setter
	@Builder.Default
	@XmlElementRef
	private List<Operation> serviceOperation = new ArrayList<Operation>();

	@Getter
	@Setter
	@Builder.Default
	@XmlAttribute
	private String params = null;
	
	@Getter
	@Setter
	@Builder.Default
	@XmlAttribute
	private String module = null;

	@Getter
	@Setter
	@Builder.Default
	@XmlAttribute
	private String name = null;

	@Override
	public int getPriority() {
		return PatternPriority.BUSINESS_SERVICE;
	}
	
	@Override
	public String getComponentName() {
		StringBuilder b = new StringBuilder();
		b.append(getModule()+'.'+getName()+'(');
		b.append(this.getParams()+')');
		return b.toString();
	}

	public String getPkg() {
		return pkg;
	}

	@ConfigProperty(required = true, name = "java.service.pkg",
			description = "Sub-package that the data object class will be created in, under the Java root package.", 
			example = "dto")
	public void setPkg(String pkg) {
		this.pkg = pkg;
	}

}
