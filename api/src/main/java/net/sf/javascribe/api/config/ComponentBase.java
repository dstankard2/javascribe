package net.sf.javascribe.api.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;

/**
 * Base class for any component pattern.  The implementation class 
 * should be JAXB-annotated.  The implementation should also override 
 * getPriority() so that components of this pattern are processed in 
 * their due place.
 * @author DCS
 */
@Scannable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="component",propOrder={ "property" })
@XmlRootElement(name="component")
public class ComponentBase implements Comparable<ComponentBase>,ComponentConfigElement {

	/**
	 * Get the priority for this pattern.  Components are processed in 
	 * ascending order of priority.  This method returns INTEGER.MIN_VALUE 
	 * unless overridden.
	 * @return Priority level for this component.
	 */
	public int getPriority() { return Integer.MIN_VALUE; }

	@XmlElement
	private List<Property> property = new ArrayList<Property>();

	public List<Property> getProperty() {
		return property;
	}

	public void setProperty(List<Property> property) {
		this.property = property;
	}

	@Override
	public int compareTo(ComponentBase other) {
		if (other==null) throw new NullPointerException();
		
		if (other.getPriority()>getPriority()) return -1;
		else if (other.getPriority()<getPriority()) return 1;
		
		return 0;
	}
	
	public boolean hasProperty(String name) {
		for(Property prop : property) {
			if (prop.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

}

