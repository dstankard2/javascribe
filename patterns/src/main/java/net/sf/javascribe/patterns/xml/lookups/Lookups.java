package net.sf.javascribe.patterns.xml.lookups;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.patterns.CorePatternConstants;

@Scannable
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="lookups",propOrder={ "entity" })
public class Lookups extends ComponentBase {

	@XmlAttribute
	private String locator = "";

	@XmlElement
	private List<Entity> entity = new ArrayList<Entity>();

	public int getPriority() { return CorePatternConstants.PRIORITY_LOOKUPS; }
	
	public List<Entity> getEntity() {
		return entity;
	}

	public void setEntity(List<Entity> entity) {
		this.entity = entity;
	}

	public String getLocator() {
		return locator;
	}

	public void setLocator(String locator) {
		this.locator = locator;
	}

}

