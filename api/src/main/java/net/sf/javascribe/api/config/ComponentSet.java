package net.sf.javascribe.api.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;

/**
 * JAXB-annotated class that represents the root element of a component 
 * XML file.  It contains a list of property elements and a list of 
 * component elements (subclasses of ComponentBase).
 * @author DCS
 *
 */
@Scannable
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="componentSet",propOrder={ "property", "component" })
public class ComponentSet {

	@XmlElement
	private List<Property> property = new ArrayList<Property>();
	
	@XmlElementRef
	private List<ComponentBase> component = new ArrayList<ComponentBase>();

	public List<Property> getProperty() {
		return property;
	}

	public void setProperty(List<Property> property) {
		this.property = property;
	}

	public List<ComponentBase> getComponent() {
		return component;
	}

	public void setComponent(List<ComponentBase> component) {
		this.component = component;
	}

}
