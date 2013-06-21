package net.sf.javascribe.api.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;

@Scannable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="component",propOrder={ "property" })
@XmlRootElement(name="component")
public class ComponentBase implements Comparable<ComponentBase>,ComponentConfigElement {

	/* These methods should be overridden by subclasses to define component role in system */
	public int getPriority() { return Integer.MIN_VALUE; }

	/* End of methods to override */

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

