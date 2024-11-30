package net.sf.javascribe.api.config;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;

/**
 * JAXB-annotated class that represents the root element of a component 
 * XML file.  It contains a list of property elements and a list of 
 * component elements (subclasses of ComponentBase).
 * @author DCS
 *
 */
@Plugin
@XmlConfig
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="componentSet",propOrder={ "property", "component" })
public class ComponentSet {

	@XmlElement
	private List<Property> property = new ArrayList<>();
	
	@XmlElementRef
	private List<Component> component = new ArrayList<>();

	public List<Property> getProperty() {
		return property;
	}

	public void setProperty(List<Property> property) {
		this.property = property;
	}

	public List<Component> getComponent() {
		return component;
	}

	public void setComponent(List<Component> component) {
		this.component = component;
	}

}

