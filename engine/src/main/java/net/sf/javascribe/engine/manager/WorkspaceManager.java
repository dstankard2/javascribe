package net.sf.javascribe.engine.manager;

import net.sf.javascribe.engine.ComponentDependency;
import net.sf.javascribe.engine.service.ComponentFileService;
import net.sf.javascribe.engine.service.FolderScannerService;

public class WorkspaceManager {

	private String workspaceDir = null;
	private boolean singleApp = false;
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

	public void setSingleApp(boolean singleApp) {
		this.singleApp = singleApp;
	}
	
	public void setWorkspaceDir(String workspaceDir) {
		this.workspaceDir = workspaceDir;
	}
	
}
