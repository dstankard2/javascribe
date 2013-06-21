package net.sf.javascribe.patterns.model;

import java.io.StringWriter;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.xsd.persistence_1_0.ObjectFactory;
import net.sf.javascribe.xsd.persistence_1_0.Persistence;

public class PersistenceConfig implements SourceFile {

	ObjectFactory factory = new ObjectFactory();
	Persistence pers = null;
	HashMap<String, PersistenceUnitConfig> units = new HashMap<String, PersistenceUnitConfig>();
	String path = null;

	public PersistenceConfig() {
		pers = factory.createPersistence();
	}

	public PersistenceUnitConfig getPersistenceUnitConfig(String name) {
		return units.get(name);
	}

	public PersistenceUnitConfig createPersistenceUnit(String name) {
		PersistenceUnitConfig ret = new PersistenceUnitConfig(name, factory);
		pers.getPersistenceUnit().add(ret.getPersistence());
		units.put(name, ret);

		return ret;
	}

	public StringBuilder getSource() throws JavascribeException {
		StringBuffer b = null;
		StringBuilder ret = new StringBuilder();
		JAXBContext ctx = null;
		Marshaller m = null;
		StringWriter writer = null;

		try {
			ctx = JAXBContext
					.newInstance("net.sf.javascribe.xsd.persistence_1_0",factory.getClass().getClassLoader());
			m = ctx.createMarshaller();
			writer = new StringWriter();
			m.marshal(pers, writer);
			b = writer.getBuffer();
			ret.append(b.toString());
		} catch (Exception e) {
			throw new JavascribeException("Exception while getting source for JPA persistence file",e);
		}

		return ret;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}

