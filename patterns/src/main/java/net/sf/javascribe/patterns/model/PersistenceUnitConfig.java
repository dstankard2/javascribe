package net.sf.javascribe.patterns.model;

import net.sf.javascribe.xsd.persistence_1_0.ObjectFactory;
import net.sf.javascribe.xsd.persistence_1_0.Persistence.PersistenceUnit;
import net.sf.javascribe.xsd.persistence_1_0.Persistence.PersistenceUnit.Properties;
import net.sf.javascribe.xsd.persistence_1_0.Persistence.PersistenceUnit.Properties.Property;
import net.sf.javascribe.xsd.persistence_1_0.PersistenceUnitTransactionType;


public class PersistenceUnitConfig {
	ObjectFactory factory = null;
	String name = null;
	PersistenceUnit unit = null;
	Properties props = null;
	
	public PersistenceUnitConfig(String name,ObjectFactory fac) {
		this.name = name;
		factory = fac;
		unit = factory.createPersistencePersistenceUnit();
		unit.setName(name);
		props = factory.createPersistencePersistenceUnitProperties();
		unit.setProperties(props);
	}
	
	public void setTransactionType(String s) {
		if (s.equals("local")) {
			unit.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
		} else {
			throw new IllegalArgumentException("Transaction type must be defined as 'local'");
		}
	}
	
	public void setProvider(String s) {
		unit.setProvider(s);
	}
	
	public void addProperty(String name,String value) {
		Property prop = null;
		prop = factory.createPersistencePersistenceUnitPropertiesProperty();
		prop.setName(name);
		prop.setValue(value);
		props.getProperty().add(prop);
	}
	
	public PersistenceUnit getPersistence() {
		return unit;
	}
	
	public void addClass(String cl) {
		unit.getClazz().add(cl);
	}
	
}

