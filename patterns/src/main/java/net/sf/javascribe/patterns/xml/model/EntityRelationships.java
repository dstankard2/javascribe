package net.sf.javascribe.patterns.xml.model;

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
@XmlRootElement(name="entityRelationships")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="entityRelationships",propOrder={ "rel" })
public class EntityRelationships extends ComponentBase {

	public static final String ORDERBY_PK = "net.sf.javascribe.patterns.model.EntityRelationships.orderByPK";

	@Override
	public int getPriority() { return CorePatternConstants.PRIORITY_ENTITY_RELATIONSHIPS; }
	
	@XmlElement
	private List<Relationship> rel = new ArrayList<Relationship>();
	
	@XmlAttribute
	private String entityManager = "";

	@XmlAttribute
	private String jpaDaoFactory = "";

	public String getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(String entityManager) {
		this.entityManager = entityManager;
	}

	public String getJpaDaoFactory() {
		return jpaDaoFactory;
	}

	public void setJpaDaoFactory(String jpaDaoFactory) {
		this.jpaDaoFactory = jpaDaoFactory;
	}

	public List<Relationship> getRel() {
		return rel;
	}

	public void setRel(List<Relationship> rel) {
		this.rel = rel;
	}

}

