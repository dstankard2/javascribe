package net.sf.javascribe.engine.manager;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.engine.ComponentDependency;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.service.ComponentFileService;
import net.sf.javascribe.engine.service.FolderScannerService;

public class WorkspaceManager {

	private ComponentFileService componentFileService = null;
	private FolderScannerService folderScannerService = null;
	
	@ComponentDependency
	public void setComponentFileService(ComponentFileService s) {
		this.componentFileService = s;
	}
	@ComponentDependency
	public void setFolderScannerService(FolderScannerService s) {
		this.folderScannerService = s;
	}

	public List<ApplicationData> initializeApplications(String workspaceDir, boolean singleApp) {
		List<ApplicationData> ret = new ArrayList<>();
		
		
		return ret;
	}

}
