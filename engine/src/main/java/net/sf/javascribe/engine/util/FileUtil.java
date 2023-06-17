package net.sf.javascribe.engine.util;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.SneakyThrows;
import net.sf.javascribe.api.config.BuildComponent;
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
	
	private Map<String,List<File>> filesRemoved = new HashMap<>();
	private Map<String,List<File>> filesAdded = new HashMap<>();

	ComponentFileService componentFileService;
	
	@ComponentDependency
	public void setComponentFileService(ComponentFileService s) {
		this.componentFileService = s;
	}
	
	public FileUtil() {
		
	}

	protected void readWatchKey(ApplicationFolderImpl folder, String pathStr) {
		WatchKey key = watchKeys.get(pathStr);
		List<WatchEvent<?>> events = key.pollEvents();
		if (events.size()>0) {
			List<File> added = filesAdded.get(pathStr);
			List<File> removed = filesRemoved.get(pathStr);
			
			events.forEach(event -> {
				Path p = (Path)event.context();
				File file = new File(folder.getFolderFile(), p.toString());

				if (event.kind()==null) {
				} else if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
					added.add(file);
				} else if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {
					removed.add(file);
				} else if (event.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
					added.add(file);
					removed.add(file);
				}
				
			});
		}
	}

	// Called when there's a change to jasper.properties, systemAttributes.properties, a build component, or if the folder is 
	// changed/removed
	// When these events occur, all folder contents must be cleared out to be re-added.
	// Need to clear all files out of the folder object, reset the files found by the watchKey, and ensure that all files 
	// in the directory are in addedFiles for the key.
	private void clearFolder(ApplicationFolderImpl folder, List<WatchedResource> changes) {
		File f = folder.getFolderFile();
		String pathKey = folder.getPath();
		
		folder.getUserFiles().forEach((name, userFile) -> {
			changes.add(userFile);
		});
		folder.getUserFiles().clear();

		folder.getComponentFiles().values().forEach(cf -> {
			changes.add(cf);
		});
		folder.getComponentFiles().clear();
		
		folder.getSubFolders().values().forEach(fo -> {
			clearFolder(fo, changes);
		});
		folder.getSubFolders().clear();
		
		folder.setJasperProperties(null);

		filesRemoved.get(pathKey).clear();
		filesAdded.get(pathKey).clear();
		filesAdded.get(pathKey).addAll(Arrays.asList(f.listFiles()));
	}

	public List<WatchedResource> findFilesRemoved(ApplicationFolderImpl folder) {
		String key = folder.getPath();
		List<WatchedResource> ret = new ArrayList<>();
		AtomicBoolean clearFolder = new AtomicBoolean(false);

		readWatchKey(folder, key);
		
		List<File> removed = filesRemoved.get(key);
		
//		List<ApplicationFolderImpl> foldersToCheck = new ArrayList<>(folder.getSubFolders().values());
		
		// Handle removed files
		removed.forEach(f -> {
			String name = f.getName();
			// When jasper.properties changes, we need to re-process this folder
			if (name.equals("javascribe.properties")) {
				clearFolder.set(true);
			}
			if ((name.equals("systemAttributes.properties")) && (folder.getParent()==null)) {
				// When systemAttributes.properties changes, we need to re-process this folder
				clearFolder.set(true);
			}
			if (folder.getSubFolders().containsKey(name)) {
				clearFolder(folder.getSubFolders().get(name), ret);
			}
			if (folder.getUserFiles().get(name)!=null) {
				ret.add(folder.getUserFiles().get(name));
				folder.getUserFiles().remove(name);
			}
			if (folder.getComponentFiles().get(name)!=null) {
				ComponentFile compFile = folder.getComponentFiles().get(name);
				boolean buildChanged = compFile.getComponentSet().getComponent().stream().anyMatch(comp -> {
					return comp instanceof BuildComponent;
				});
				if (buildChanged) {
					clearFolder.set(true);
				}
				ret.add(folder.getComponentFiles().get(name));
				folder.getComponentFiles().remove(name);
			}
		});
		
		folder.getSubFolders().values().forEach(f -> {
			ret.addAll(findFilesRemoved(f));
		});
		
		// If a file has changed which requires this folder to be read again, we have to clear this folder so it 
		// can files can be re-added.
		if (clearFolder.get()) {
			this.clearFolder(folder, ret);
		}
		
		removed.clear();
		
		return ret;
	}
	
	public List<WatchedResource> findFilesAdded(ApplicationFolderImpl folder) {
		String key = folder.getPath();
		List<WatchedResource> ret = new ArrayList<>();
		
		readWatchKey(folder, key);
		
		List<File> addedFiles = this.filesAdded.get(key);
		
		// Read javascribe.properties first
		File jp = addedFiles.stream().filter(f -> f.getName().equals("javascribe.properties")).findFirst().orElse(null);
		if (jp!=null) {
			addedFiles.remove(jp);
			ret.add(readFile(jp, folder));
		}
		// Read systemAttributes.properties if this is the root folder
		File sp = addedFiles.stream().filter(f -> f.getName().equals("systemAttributes.properties")).findFirst().orElse(null);
		if (sp!=null) {
			addedFiles.remove(sp);
			ret.add(readFile(sp, folder));
		}

		List<File> subdirs = new ArrayList<>();
		for(File f : addedFiles) {
			if (f.isDirectory()) {
				subdirs.add(f);
			} else {
				WatchedResource addition = readFile(f, folder);
				if (addition != null) {
					ret.add(addition);
				}
			}
		}
		addedFiles.clear();

		subdirs.forEach(d -> {
			ApplicationFolderImpl subFolder = new ApplicationFolderImpl(d, folder);
			this.initFolder(subFolder);
			folder.getSubFolders().put(d.getName(), subFolder);
		});
		folder.getSubFolders().entrySet().forEach(entry -> {
			List<WatchedResource> added = findFilesAdded(entry.getValue());
			ret.addAll(added);
			
		});

		return ret;
	}

	// Removes the watch on this directory
	protected void removeFolder(ApplicationFolderImpl folder) {
		String pathStr = folder.getPath();

		WatchKey watchKey = watchKeys.get(pathStr);
		if (watchKey!=null) {
			watchKey.pollEvents();
			watchKey.cancel();
			watchKeys.remove(pathStr);
			filesAdded.remove(pathStr);
			filesRemoved.remove(pathStr);
		}
	}

	@SneakyThrows
	public void initFolder(ApplicationFolderImpl folder) {
		WatchService watchService = FileSystems.getDefault().newWatchService();
		String pathStr = folder.getPath();
		File directory = folder.getFolderFile();
		String filePathStr = directory.getAbsolutePath();
		Path path = Paths.get(filePathStr);

		WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, 
				StandardWatchEventKinds.ENTRY_DELETE);
		watchKeys.put(pathStr, watchKey);
		filesAdded.put(pathStr, new ArrayList<>(Arrays.asList(directory.listFiles())));
		filesRemoved.put(pathStr, new ArrayList<>());
	}

	protected WatchedResource readFile(File f, ApplicationFolderImpl folder) {
		String name = f.getName();
		
		if (f.isDirectory()) {
			return null;
		}
		else if (name.equals("javascribe.properties")) {
			JavascribePropertiesFile propFile = new JavascribePropertiesFile(f, folder);
			folder.setJasperProperties(propFile);
			return propFile;
		}
		else if ((name.equals("systemAttributes.properties")) && (folder.getParent()==null)) {
			SystemAttributesFile attrs = new SystemAttributesFile(f, folder);
			return attrs;
		}
		else if (folder.isIgnore(name)) {
			return null;
		}
		else if (name.endsWith(".xml")) {
			ComponentSet set = componentFileService.readFile(f);
			if (set!=null) {
				ComponentFile compFile = new ComponentFile(set, f, folder);
				folder.getComponentFiles().put(compFile.getName(), compFile);
				return compFile;
			}
		}
		UserFile userFile = new UserFile(f, folder);
		folder.getUserFiles().put(userFile.getName(), userFile);
		return userFile;
	}

}

