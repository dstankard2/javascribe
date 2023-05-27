package net.sf.javascribe.engine.service;

import java.io.File;
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
		
		File dir = application.getApplicationDirectory();
		ApplicationFolderImpl folder = application.getRootFolder();
		
		findRemovedFiles(dir, folder, ret);
		
		return ret;
	}

	protected void findRemovedFiles(File dir, ApplicationFolderImpl folder, List<WatchedResource> changes) {
		
	}
	
	public List<WatchedResource> findFilesAdded(ApplicationData application) {
		ApplicationFolderImpl folder = application.getRootFolder();
		return fileUtil.findFilesAdded(folder);
	}

}

