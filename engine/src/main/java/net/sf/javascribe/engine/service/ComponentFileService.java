package net.sf.javascribe.engine.service;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.stereotype.Service;

import net.sf.javascribe.api.annotation.XmlConfig;
import net.sf.javascribe.api.config.ComponentSet;
import net.sf.javascribe.engine.ComponentDependency;
import net.sf.javascribe.engine.EngineInitException;

@Service
public class ComponentFileService {

	private PluginService pluginService;
	JAXBContext ctx = null;
	Unmarshaller um = null;

	@ComponentDependency
	public void setPluginService(PluginService pluginService) {
		this.pluginService = pluginService;
	}

	public void loadPatternDefinitions() {
		List<Class<?>> xmlConfigClasses = pluginService.findClassesWithAnnotation(XmlConfig.class);
		Class<?>[] classes = new Class<?>[xmlConfigClasses.size()];
		xmlConfigClasses.toArray(classes);
		
		try {
			ctx = JAXBContext.newInstance(classes);
			um = ctx.createUnmarshaller();
		} catch(JAXBException e) {
			throw new EngineInitException("Couldn't initialize component file reader", e);
		}
	}

	public ComponentSet readFile(File file) {
		ComponentSet ret = null;
		FileReader reader = null;

		try {
			reader = new FileReader(file);
			ret = (ComponentSet)um.unmarshal(reader);
		} catch(Exception e) {
			//e.printStackTrace();
			// no-op
		} finally {
			if (reader!=null) {
				try {
					reader.close();
				} catch(Exception e) { }
			}
		}
		if ((ret!=null) && (ret.getComponent().size()==0)) {
			System.out.println("WARN: Component File '"+file.getAbsolutePath()+"' had no components");
		}
		return ret;
	}

}
