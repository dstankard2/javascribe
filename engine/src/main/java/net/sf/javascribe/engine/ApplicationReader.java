package net.sf.javascribe.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.api.config.ComponentSet;
import net.sf.javascribe.api.config.JavascribeConfig;
import net.sf.javascribe.api.config.Property;
import net.sf.javascribe.api.config.PropertyList;

public class ApplicationReader {
	private JAXBContext componentContext = null;
	
	public ApplicationReader(JAXBContext componentContext) {
		this.componentContext = componentContext;
	}
	
	public ApplicationDefinition readApplication(ZipFile zip) throws JavascribeException,IOException,JAXBException {
		ApplicationDefinition ret = new ApplicationDefinition();
		
		ret.setAttributes(readAttributes(zip));
		JavascribeConfig config = readGeneratorConfig(zip);
		if (config==null) throw new JavascribeException("Couldn't find META-INF/generator.xml in archive");
		ret.setAppName(config.getAppName());
		ret.setGlobalProperties(getProperties(config.getProperties()));
		ret.setBuildRoot(config.getDestRoot());
		ret.setComponents(findComponents(zip));
		
		return ret;
	}
	
	private List<ComponentSet> findComponents(ZipFile zip) throws JAXBException,IOException,JavascribeException {
		List<ComponentSet> ret = new ArrayList<ComponentSet>();

		Unmarshaller um = componentContext.createUnmarshaller();
		Enumeration<? extends ZipEntry> entries = zip.entries();
		
		while(entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			if (entry.getName().endsWith(".xml")) {
				try {
					if (!entry.getName().equals("META-INF/generator.xml")) {
						ComponentSet comp = (ComponentSet)um.unmarshal(zip.getInputStream(entry));
						applyFileBasedProperties(comp.getProperty(),comp.getComponent());
						ret.add(comp);
					}
				} catch(Exception e) {
					throw new JavascribeException("Exception while unmarshalling component file '"+entry.getName()+"'",e);
				}
			}
		}
		
		return ret;
	}
	
	private void applyFileBasedProperties(List<Property> properties,List<ComponentBase> comps) {
		for(Property prop : properties) {
			for(ComponentBase comp : comps) {
				if (!comp.hasProperty(prop.getName())) {
					comp.getProperty().add(new Property(prop.getName(),prop.getValue()));
				}
			}
		}
	}
	
	private HashMap<String,String> getProperties(PropertyList props) {
		HashMap<String,String> ret = new HashMap<String,String>();
		
		if (props!=null) {
			for(Property prop : props.getProperty()) {
				ret.put(prop.getName(), prop.getValue());
			}
		}
		
		return ret;
	}
	
	private JavascribeConfig readGeneratorConfig(ZipFile zip) throws IOException,JAXBException {
		JavascribeConfig ret = null;
		ZipEntry entry = zip.getEntry("META-INF/generator.xml");
		
		if (entry!=null) {
			InputStream in = null;
			
			try {
				in = zip.getInputStream(entry);
				JAXBContext ctx = JAXBContext.newInstance("net.sf.javascribe.api.config");
				Unmarshaller um = ctx.createUnmarshaller();
				ret = (JavascribeConfig)um.unmarshal(in);
			} finally {
				if (in!=null) {
					try { in.close(); } catch(Exception e) { }
				}
			}
		}
		
		return ret;
	}
	
	private HashMap<String,String> readAttributes(ZipFile zip) {
		HashMap<String,String> ret = new HashMap<String,String>();
		ZipEntry entry = zip.getEntry("META-INF/attributes.properties");
		
		if (entry!=null) {
			InputStream in = null;
			try {
				in = zip.getInputStream(entry);
				Properties props = new Properties();
				props.load(in);
				for(Object o : props.keySet()) {
					ret.put(o.toString(), props.getProperty(o.toString()));
				}
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				if (in!=null) {
					try { in.close(); } catch(Exception e) { }
				}
			}
		}
		
		return ret;
	}
	
}
