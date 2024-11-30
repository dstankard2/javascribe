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
import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.patterns.model.ModelUtils;

@XmlConfig
@Plugin
@XmlRootElement(name="tableSet")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="tableSet",propOrder={ })
public class TableSetComponent extends Component {

	@XmlAttribute
	private String databaseType = "";
	
	@XmlTransient
	private String url = "";

	@XmlTransient
	private String username = "";

	@XmlTransient
	private String password = "";

	@XmlAttribute
	private String dbSchema = "";
	
	@XmlAttribute
	private String id = "";

	public int getPriority() {
		return 5;
	}

	public String getUrl() {
		return url;
	}

	@ConfigProperty(required = true, name = ModelUtils.CONFIG_JDBC_URL,
			description = "Database URL to connect to to read ")
	public void setUrl(String url) {
		this.url = url;
	}

	@ConfigProperty(required = true, name = ModelUtils.CONFIG_JDBC_USERNAME)
	public void setUsername(String username) {
		this.username = username;
	}

	@ConfigProperty(required = true, name = ModelUtils.CONFIG_JDBC_PASSWORD)
	public void setPassword(String password) {
		this.password = password;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String getPatternName() {
		return "DbSchema";
	}

	public String getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}

	public String getDbSchema() {
		return dbSchema;
	}

	public void setDbSchema(String dbSchema) {
		this.dbSchema = dbSchema;
	}
	
	@Override
	public String getComponentName() {
		return "TableSet[id='"+id+"']";
	}

}
