package net.sf.javascribe.patterns.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.patterns.CorePatternConstants;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;

/**
 * Represents a JPA Entity Manager with a Hibernate implementation.
 * @author Dave
 */
@Scannable
@XmlRootElement(name="entityManager")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="entityManager",propOrder={ })
public class EntityManagerComponent extends ComponentBase {

	public static final String RUNTIME_JPA_URL = "net.sf.javascribe.patterns.model.EntityManagerComponent.runtime.jpaUrl";
	public static final String RUNTIME_JPA_USERNAME = "net.sf.javascribe.patterns.model.EntityManagerComponent.runtime.jpaUsername";
	public static final String RUNTIME_JPA_PASSWORD = "net.sf.javascribe.patterns.model.EntityManagerComponent.runtime.jpaPassword";
	public static final String RUNTIME_JPA_DRIVER = "net.sf.javascribe.patterns.model.EntityManagerComponent.runtime.jpaDriver";

	public static final String CODEGEN_JPA_URL = "net.sf.javascribe.patterns.model.EntityManagerComponent.codegen.jpaUrl";
	public static final String CODEGEN_JPA_USERNAME = "net.sf.javascribe.patterns.model.EntityManagerComponent.codegen.jpaUsername";
	public static final String CODEGEN_JPA_PASSWORD = "net.sf.javascribe.patterns.model.EntityManagerComponent.codegen.jpaPassword";
	public static final String CODEGEN_JPA_CATALOG = "net.sf.javascribe.patterns.model.EntityManagerComponent.codegen.catalog";

	public static final String VERSION_FIELD = "net.sf.javascribe.patterns.model.EntityManagerComponent.jpaVersionField";
	public static final String ENTITY_PACKAGE = "net.sf.javascribe.patterns.model.EntityManagerComponent.pkg";
	public static final String SHOW_SQL = "net.sf.javascribe.patterns.model.EntityManagerComponent.showSql";

	public static final String CATALOG = "net.sf.javascribe.patterns.model.EntityManagerComponent.codegen.catalog";
	public static final String NAME_RESOLVER = "net.sf.javascribe.patterns.model.EntityManagerComponent.databaseObjectNameResolver";
	public static final String SCHEMA_READERS = "net.sf.javascribe.patterns.model.SchemaReaders";
	public static final String NAME_RESOLVERS = "net.sf.javascribe.patterns.model.EntityManagerComponent.DatabaseObjectNameResolvers";
	public static final String DATABASE_TYPE = "net.sf.javascribe.patterns.model.EntityManagerComponent.databaseType";

	public int getPriority() { return CorePatternConstants.PRIORITY_ENTITY_MANAGER; }
	
	@XmlAttribute
	private String name = "";
	
	@XmlAttribute
	private String ref = "";
	
	@XmlAttribute
	private String databaseType = "";
	
	@XmlAttribute
	private String catalog = "";

	public String getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}

