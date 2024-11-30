package net.sf.javascribe.patterns.xml.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.ConfigProperty;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;
import net.sf.javascribe.langsupport.java.JavaComponent;
import net.sf.javascribe.patterns.PatternPriority;

@Plugin
@XmlConfig
@XmlRootElement(name="persistenceUnit")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="persistenceUnit",propOrder={ })
public class PersistenceUnit extends JavaComponent {

	@XmlAttribute
	private String txRef = "";
	
	@XmlAttribute
	private String name = "";
	
	@XmlAttribute
	private String tableSetId = "";

	@XmlTransient
	private String pkg = null;

	@XmlTransient
	private String jndiDataSource = null;
	
	@XmlTransient
	private String showSql = null;
	
	public int getPriority() {
		return PatternPriority.PERSISTENCE_UNIT;
	}
	
	public String getJndiDataSource() {
		return jndiDataSource;
	}

	@ConfigProperty(required = false, name = "jpa.jndiDataSource", example = "java:comp/env/someDataSource",
			description = "JNDI reference to a data source available")
	public void setJndiDataSource(String jndiDataSource) {
		this.jndiDataSource = jndiDataSource;
	}

	public String getShowSql() {
		return showSql;
	}

	@ConfigProperty(required = false, name = "jpa.hibernate.show_sql", example = "T",
			description = "T to have Hibernate log SQL.")
	public void setShowSql(String showSql) {
		this.showSql = showSql;
	}

	public String getTxRef() {
		return txRef;
	}

	public void setTxRef(String txRef) {
		this.txRef = txRef;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPkg() {
		return pkg;
	}

	@ConfigProperty(required = true, name = "java.entity.package", example = "entity",
			description = "Sub-package that the data object class will be created in, under the Java root package.")
	public void setPkg(String pkg) {
		this.pkg = pkg;
	}

	public String getTableSetId() {
		return tableSetId;
	}

	public void setTableSetId(String tableSetId) {
		this.tableSetId = tableSetId;
	}
	
	public String getComponentName() {
		return "PersistenceUnit:"+getName();
	}

}

