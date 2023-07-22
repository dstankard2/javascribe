package net.sf.javascribe.engine.data.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class JavascribePropertiesFile implements WatchedResource {

	public static final String JAVASCRIBE_PROPERTIES_FILE = "javascribe.properties";
	
	private Map<String,String> properties = null;
	private long lastModified = 0L;
	private ApplicationFolderImpl folder = null;
	List<String> ignoreList = new ArrayList<>();
	
	public JavascribePropertiesFile(File file, ApplicationFolderImpl folder) {
		this.properties = readProperties(file);
		this.folder = folder;
		if (properties.containsKey("ignore")) {
			String ignoreString = properties.get("ignore");
			ignoreList = Arrays.asList(ignoreString.split(","));
		}
		this.lastModified = file.lastModified();
	}

	private Map<String,String> readProperties(File f) {
		Properties props = new Properties();
		Map<String,String> values = new HashMap<>();

		try {
			try (FileInputStream fin = new FileInputStream(f)) {
				props.load(fin);
				props.entrySet().forEach(e -> {
					values.put(
							e.getKey() != null ? e.getKey().toString() : "", 
							e.getValue() != null ? e.getValue().toString() : "");
				});
			}
		} catch(FileNotFoundException e) {
			// logically can't happen
		} catch(IOException e) {
			// TODO: ???
		}

		return values;
	}

	@Override
	public String getName() {
		return JAVASCRIBE_PROPERTIES_FILE;
	}

	@Override
	public String getPath() {
		return getFolder().getPath()+JAVASCRIBE_PROPERTIES_FILE;
	}

	@Override
	public long getLastModified() {
		return lastModified;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	@Override
	public ApplicationFolderImpl getFolder() {
		return folder;
	}
	
	public boolean isIgnore(String name) {
		return ignoreList.contains(name);
	}

}
