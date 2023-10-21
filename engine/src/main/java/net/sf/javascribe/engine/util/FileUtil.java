package net.sf.javascribe.engine.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.javascribe.api.config.ComponentSet;
import net.sf.javascribe.engine.ComponentDependency;
import net.sf.javascribe.engine.data.ApplicationData;
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

	ComponentFileService componentFileService;
	
	@ComponentDependency
	public void setComponentFileService(ComponentFileService s) {
		this.componentFileService = s;
	}
	
	public FileUtil() {
		
	}

	// Called when there's a change to jasper.properties, systemAttributes.properties, a build component, or if the folder is 
	// changed/removed
	// When these events occur, all folder contents must be cleared out to be re-added.
	// Need to clear all files out of the folder object, reset the files found by the watchKey, and ensure that all files 
	// in the directory are in addedFiles for the key.
	private void clearFolder(ApplicationFolderImpl folder, List<WatchedResource> changes) {
		
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
		
		folder.setJavascribeProperties(null);

	}

	private File getFile(String name, List<File> files) {
		return files.stream().filter(f -> f.getName().equals(name)).findFirst().orElse(null);
	}

	public List<WatchedResource> findFilesRemoved(ApplicationData application, ApplicationFolderImpl folder) {
		List<WatchedResource> ret = new ArrayList<>();
		List<File> contents = new ArrayList<>();
		File folderFile = folder.getFolderFile();

		contents.addAll(Arrays.asList(folderFile.listFiles()));
		
		// First look for modified/removed systemAttributes.properties in root folder
		if (folder.getParent()==null) {
			File attribs = contents.stream().filter(f -> (f.getName().equals("systemAttributes.properties")) && (!f.isDirectory())).findFirst().orElse(null);
			if (application.getSystemAttributesFile()!=null) {
				if (attribs==null) {
					// The file has been removed
					application.setSystemAttributesFile(null);
					clearFolder(folder, ret);
					return ret;
				} else if (application.getSystemAttributesFile().getLastModified() < attribs.lastModified()) {
					// The file has been updated
					application.setSystemAttributesFile(null);
					clearFolder(folder, ret);
					return ret;
				}
			} else if (attribs!=null) {
				// The file has been added.  Need to reset this folder
				clearFolder(folder, ret);
				return ret;
			}
			if (attribs!=null) contents.remove(attribs);
		}
		
		// Look for modified/removed javascribe.properties
		File props = contents.stream().filter(f -> (f.getName().equals("javascribe.properties")) && (!f.isDirectory())).findFirst().orElse(null);
		if (folder.getJasperPropertiesFile()!=null) {
			if (props==null) {
				// If the file is removed, clear the folder
				folder.setJavascribeProperties(null);
				clearFolder(folder, ret);
				return ret;
			} else if (folder.getJasperPropertiesFile().getLastModified() < props.lastModified()) {
				// If the file is modified, clear the folder
				folder.setJavascribeProperties(null);
				clearFolder(folder, ret);
				return ret;
			}
		} else {
			// Look for added javascribe.properties.  The folder will have to be reset.
			if (props!=null) {
				clearFolder(folder, ret);
				return ret;
			}
		}
		if (props!=null) contents.remove(props);

		List<String> namesToRemove = new ArrayList<>();

		// Look for modified/removed user files
		for(String name : folder.getUserFiles().keySet()) {
			UserFile uf = folder.getUserFiles().get(name);
			File f = getFile(name, contents);
			if (f==null) {
				namesToRemove.add(name);
			} else if (f.lastModified() > uf.getLastModified()) {
				namesToRemove.add(name);
			}
		}
		namesToRemove.forEach(name -> {
			ret.add(folder.getUserFiles().get(name));
			folder.getUserFiles().remove(name);
		});
		namesToRemove.clear();

		// Look for modified/removed component files
		for(String name : folder.getComponentFiles().keySet()) {
			ComponentFile cf = folder.getComponentFiles().get(name);
			File f = getFile(name, contents);
			if (f==null) {
				namesToRemove.add(name);
			} else if (f.lastModified() > cf.getLastModified()) {
				namesToRemove.add(name);
			}
		}
		namesToRemove.forEach(name -> {
			ret.add(folder.getComponentFiles().get(name));
			folder.getComponentFiles().remove(name);
		});
		namesToRemove.clear();
		
		// Look at subFolders.  For any that are not in files, remove it.  
		// For those that still are, look in them for removed files
		folder.getSubFolders().entrySet().forEach(e -> {
			File f = getFile(e.getKey(), contents);
			if (f==null) {
				// Folder is removed
				clearFolder(e.getValue(), ret);
			} else if (f.isDirectory()==false) {
				// Folder is now a file.  Remove it.
				clearFolder(e.getValue(), ret);
			} else {
				ret.addAll(findFilesRemoved(application, e.getValue()));
			}
		});

		return ret;
	}

	public List<WatchedResource> findFilesAdded(ApplicationData application, ApplicationFolderImpl folder) {
		List<WatchedResource> ret = new ArrayList<>();
		List<File> contents = new ArrayList<>();
		File folderFile = folder.getFolderFile();

		contents.addAll(Arrays.asList(folderFile.listFiles()));

		// Check for added javascribe.properties
		File f = getFile("javascribe.properties", contents);
		if (folder.getJasperPropertiesFile()==null) {
			if ((f!=null) && (!f.isDirectory())) {
				JavascribePropertiesFile propFile = new JavascribePropertiesFile(f, folder);
				folder.setJavascribeProperties(propFile);
				ret.add(propFile);
				contents.remove(f);
			}
		}
		if (f!=null) {
			contents.remove(f);
		}
		
		// In root folder, check for added systemAttributes.properties
		f = getFile("systemAttributes.properties", contents);
		if ((folder.getParent()==null) && (application.getSystemAttributesFile()==null)) {
			if ((f!=null) && (!f.isDirectory())) {
				SystemAttributesFile attrs = new SystemAttributesFile(f, folder);
				ret.add(attrs);
				application.setSystemAttributesFile(attrs);
				contents.remove(f);
			}
		}
		if (f!=null) {
			contents.remove(f);
		}
		
		// Check for added user/component files and subfolders
		List<File> subdirs = new ArrayList<>();
		contents.forEach(file -> {
			if (folder.isIgnore(file.getName())) {
				return;
			}
			if (file.isDirectory()) {
				subdirs.add(file);
				return;
			}
			if ((!folder.getUserFiles().containsKey(file.getName())) 
					&& (!folder.getComponentFiles().containsKey(file.getName()))) {
				WatchedResource r = readFile(file, folder);
				ret.add(r);
				if (r instanceof ComponentFile) {
					folder.getComponentFiles().put(r.getName(), (ComponentFile)r);
				} else if (r instanceof UserFile) {
					folder.getUserFiles().put(r.getName(), (UserFile)r);
				}
			}
		});

		// Check subdirectories for added files
		subdirs.forEach(d -> {
			String name = d.getName();
			if (folder.getSubFolders().get(name)==null) {
				ApplicationFolderImpl n = new ApplicationFolderImpl(d, folder);
				folder.getSubFolders().put(name, n);
			}
			ret.addAll(findFilesAdded(application, folder.getSubFolders().get(name)));
		});
		
		return ret;
	}

	/*
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
			if (!folder.isIgnore(f.getName())) {
				if (f.isDirectory()) {
					subdirs.add(f);
				} else {
					WatchedResource addition = readFile(f, folder);
					if (addition != null) {
						ret.add(addition);
					}
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
*/

	protected WatchedResource readFile(File f, ApplicationFolderImpl folder) {
		String name = f.getName();
		
		if (name.endsWith(".xml")) {
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

	public void trimFolders(ApplicationData application, ApplicationFolderImpl folder) {
		List<String> names = new ArrayList<>();
		
		// trim children first
		folder.getSubFolders().values().forEach(val -> {
			trimFolders(application, val);
		});

		folder.getSubFolders().entrySet().forEach(e -> {
			ApplicationFolderImpl f = e.getValue();
			if ((f.getUserFiles().isEmpty()) && (f.getComponentFiles().isEmpty()) 
					&& (f.getSubFolders().isEmpty())) {
				names.add(e.getKey());
			}
		});
		names.forEach(name -> {
			folder.getSubFolders().get(name).setJavascribeProperties(null);
			folder.getSubFolders().remove(name);
		});
		
	}

}

