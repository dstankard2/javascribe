package net.sf.javascribe.engine.util;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;
import net.sf.javascribe.api.config.ComponentSet;
import net.sf.javascribe.engine.ComponentDependency;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
import net.sf.javascribe.engine.data.files.ComponentFile;
import net.sf.javascribe.engine.data.files.JavascribePropertiesFile;
import net.sf.javascribe.engine.data.files.SystemAttributesFile;
import net.sf.javascribe.engine.data.files.UserFile;
import net.sf.javascribe.engine.data.files.WatchedResource;
import net.sf.javascribe.engine.service.ComponentFileService;

// Performs operations for getting references to File objects.
// Can be mocked in junit tests at the service level.
public class FileUtil {

	private Map<String,WatchKey> watchKeys = new HashMap<>();

	ComponentFileService componentFileService;
	
	@ComponentDependency
	public void setComponentFileService(ComponentFileService s) {
		this.componentFileService = s;
	}
	
	public FileUtil() {
		
	}

	public List<WatchedResource> findFilesAdded(ApplicationFolderImpl folder) {
		String key = folder.getPath();
		if (!watchKeys.containsKey(key)) {
			return initFolder(folder);
		} else {
			// TODO: Handle subsequent scans
		}
		return new ArrayList<>();
	}

	// TODO: Handle the exception in the watch service.
	@SneakyThrows
	protected List<WatchedResource> initFolder(ApplicationFolderImpl folder) {
		File file = folder.getFolderFile();
		WatchService watchService = FileSystems.getDefault().newWatchService();
		String pathStr = folder.getPath();
		Path path = Paths.get(pathStr);

		WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, 
				StandardWatchEventKinds.ENTRY_DELETE);
		watchKeys.put(pathStr, watchKey);
		
		File[] contents = file.listFiles();
		return filesAdded(Arrays.asList(contents), folder);
	}

	protected List<WatchedResource> filesAdded(List<File> files, ApplicationFolderImpl folder) {
		List<WatchedResource> ret = new ArrayList<>();
		ApplicationFolderImpl parent = folder.getParent();
		
		// If we're in the root directory, look for systemAttributes.properties
		if (parent==null) {
			File attrs = files.stream().filter(f -> f.getName().equals("systemAttributes.properties")).findFirst().orElse(null);
			if (attrs!=null) {
				ret.add(new SystemAttributesFile(attrs, folder));
			}
		}

		// Look for jasper.properties
		File props = files.stream().filter(f -> f.getName().equals("jasper.properties")).findFirst().orElse(null);
		if (props!=null) {
			JavascribePropertiesFile propFile = new JavascribePropertiesFile(props, folder);
			ret.add(propFile);
			folder.setJasperProperties(propFile);
		}
		
		List<File> foldersToCheck = new ArrayList<>();
		for(File file : files) {
			if (folder.isIgnore(file.getName())) {
				continue;
			}
			if (file.isDirectory()) {
				foldersToCheck.add(file);
			} else {
				ComponentFile compFile = null;
				if (file.getName().endsWith(".xml")) {
					ComponentSet set = componentFileService.readFile(file);
					if (set!=null) {
						compFile = new ComponentFile(set, file, folder);
						ret.add(compFile);
						folder.getComponentFiles().put(compFile.getName(), compFile);
					}
				}
				if ((compFile==null) && (!file.getName().equals("jasper.properties")) 
						&& (!file.getName().equals("systemAttributes.properties"))) {
					UserFile uf = new UserFile(file, folder);
					ret.add(uf);
					folder.getUserFiles().put(uf.getName(), uf);
				}
			}
		}

		return ret;
	}

}

