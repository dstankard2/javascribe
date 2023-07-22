package net.sf.javascribe.engine.service;

import java.util.List;

import net.sf.javascribe.engine.ComponentDependency;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
import net.sf.javascribe.engine.data.files.WatchedResource;
import net.sf.javascribe.engine.util.FileUtil;

public class FolderScannerService {

	private FileUtil fileUtil;
	
	@ComponentDependency
	public void setFileUtil(FileUtil fileUtil) {
		this.fileUtil = fileUtil;
	}

	// Find any file that has been removed or modified
	public List<WatchedResource> findFilesRemoved(ApplicationData application) {
		ApplicationFolderImpl folder = application.getRootFolder();
		return fileUtil.findFilesRemoved(application, folder);
	}

	public List<WatchedResource> findFilesAdded(ApplicationData application) {
		ApplicationFolderImpl folder = application.getRootFolder();
		return fileUtil.findFilesAdded(application, folder);
	}

	public void trimFolders(ApplicationData application) {
		ApplicationFolderImpl folder = application.getRootFolder();
		fileUtil.trimFolders(application, folder);
	}
}

