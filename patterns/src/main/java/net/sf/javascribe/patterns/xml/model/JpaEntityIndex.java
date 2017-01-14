package net.sf.javascribe.patterns.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.patterns.CorePatternConstants;

/**
 * Represents a search index on a JPA Entity.  Operations will be added to DAO classes of the 
 * appropriate entities to implement the index.
 * @author DCS
 */
@Scannable
@XmlRootElement(name="jpaEntityIndex")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="jpaEntityIndex",propOrder={ })
public class JpaEntityIndex extends ComponentBase {

	public int getPriority() { return CorePatternConstants.PRIORITY_JPA_ENTITY_INDEX; }
	
	@XmlAttribute
	private String entity = "";
	
	@XmlAttribute
	private String entityManager = "";
	
	@XmlAttribute
	private String name = "";
	
	@XmlAttribute
	private String multiple = "false";
	
	@XmlAttribute
	private String params = "";
	
	@XmlAttribute
	private String indexString = "";
	
	@XmlAttribute
	private String delete = "";

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMultiple() {
		return multiple;
	}

	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}

	public String getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(String entityManager) {
		this.entityManager = entityManager;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getIndexString() {
		return indexString;
	}

	public void setIndexString(String indexString) {
		this.indexString = indexString;
	}

	public String getDelete() {
		return delete;
	}

	public void setDelete(String delete) {
		this.delete = delete;
	}

}

