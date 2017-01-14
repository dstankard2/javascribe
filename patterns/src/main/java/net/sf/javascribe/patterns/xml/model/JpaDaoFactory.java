package net.sf.javascribe.patterns.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.patterns.CorePatternConstants;

@Scannable
@XmlRootElement(name="jpaDaoFactory")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="jpaDaoFactory",propOrder={ })
public class JpaDaoFactory extends ComponentBase {

	@XmlAttribute(required=true)
	private String entityManager = null;
	
	@XmlAttribute
	private String ref = null;
	
	@XmlAttribute
	private String locator = null;
	
	public int getPriority() { return CorePatternConstants.PRIORITY_JPA_DAO_FACTORY; }
	
	public String getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(String entityManager) {
		this.entityManager = entityManager;
	}

	public String getLocator() {
		return locator;
	}

	public void setLocator(String locator) {
		this.locator = locator;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

}

