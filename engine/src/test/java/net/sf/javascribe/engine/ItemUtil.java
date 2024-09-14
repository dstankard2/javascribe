package net.sf.javascribe.engine;

import java.util.HashMap;
import java.util.Map;

import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
import net.sf.javascribe.engine.data.processing.ComponentItem;
import net.sf.javascribe.engine.service.RegisteredComponentPattern;

public class ItemUtil {

	public static ComponentItem createComponentItem(Component component, ApplicationData application) {
		int id = application.getProcessingData().nextId();
		Map<String,String> configs = new HashMap<>();
		RegisteredComponentPattern pattern = null;
		ApplicationFolderImpl folder = application.getRootFolder();
		
		return new ComponentItem(id, component, configs, pattern, 0, folder, application, 0);
	}

}
