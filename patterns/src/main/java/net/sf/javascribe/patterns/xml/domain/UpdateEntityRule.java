package net.sf.javascribe.patterns.xml.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.patterns.CorePatternConstants;
import net.sf.javascribe.patterns.domain.DomainLogicComponent;

//@Scannable
@XmlRootElement(name="updateEntityRule")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="updateEntityRule",propOrder={ })
public class UpdateEntityRule extends ComponentBase implements DomainLogicComponent {

	@Override
	public int getPriority() { return CorePatternConstants.PRIORITY_UPDATE_ENTITY_RULE; }
	
	@XmlAttribute
	private String entity = null;
	
	@XmlAttribute
	private String selectBy = null;
	
	@XmlAttribute
	private String params = null;
	
	@XmlAttribute
	private String rule = null;
	
	@XmlAttribute
	private String serviceObj = null;
	
	@XmlAttribute
	private String daoFactoryRef = null;
	
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
	public String getSelectBy() {
		return selectBy;
	}
	public void setSelectBy(String selectBy) {
		this.selectBy = selectBy;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}
	public String getServiceObj() {
		return serviceObj;
	}
	public void setServiceObj(String serviceObj) {
		this.serviceObj = serviceObj;
	}
	public String getDaoFactoryRef() {
		return daoFactoryRef;
	}
	public void setDaoFactoryRef(String daoFactoryRef) {
		this.daoFactoryRef = daoFactoryRef;
	}

}

