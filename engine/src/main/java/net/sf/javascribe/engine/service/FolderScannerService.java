package net.sf.javascribe.engine.service;

import java.util.ArrayList;
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
		List<WatchedResource> ret = new ArrayList<>();
		ApplicationFolderImpl folder = application.getRootFolder();
		
		ret = fileUtil.findFilesRemoved(folder);
		
		return ret;
	}

	public List<WatchedResource> findFilesAdded(ApplicationData application) {
		ApplicationFolderImpl folder = application.getRootFolder();
		return fileUtil.findFilesAdded(folder);
	}
	
	public void initFolder(ApplicationFolderImpl folder) {
		fileUtil.initFolder(folder);
	}

}

