package net.sf.javascribe.engine.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.config.BuildComponent;
import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.api.config.ComponentSet;
import net.sf.javascribe.engine.ComponentDependency;
import net.sf.javascribe.engine.EngineInitException;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
import net.sf.javascribe.engine.data.files.ComponentFile;
import net.sf.javascribe.engine.data.files.SystemAttributesFile;
import net.sf.javascribe.engine.data.files.UserFile;
import net.sf.javascribe.engine.data.files.WatchedResource;
import net.sf.javascribe.engine.data.processing.BuildComponentItem;
import net.sf.javascribe.engine.data.processing.ComponentItem;
import net.sf.javascribe.engine.data.processing.ProcessorLog;
import net.sf.javascribe.engine.service.FolderScannerService;
import net.sf.javascribe.engine.service.PatternService;
import net.sf.javascribe.engine.service.ProcessingService;

public class WorkspaceManager {

	private FolderScannerService folderScannerService = null;
	
	@ComponentDependency
	public void setFolderScannerService(FolderScannerService s) {
		this.folderScannerService = s;
	}

	private ProcessingService processingService = null;
	
	@ComponentDependency
	public void setProcessingService(ProcessingService processingService) {
		this.processingService = processingService;
	}
	
	private PatternService patternService = null;
	
	@ComponentDependency
	public void setPatternService(PatternService patternService) {
		this.patternService = patternService;
	}
	
	public List<ApplicationData> initializeApplications(String workspaceDir, boolean singleApp) {
		List<ApplicationData> ret = new ArrayList<>();
		
		File workspace = new File(workspaceDir);
		if ((!workspace.exists()) || (!workspace.isDirectory())) {
			throw new EngineInitException("Workspace directory "+workspaceDir+" isn't a directory");
		}
		if (singleApp) {
			ApplicationData app = initializeApplication(workspace);
			ret.add(app);
		} else {
			throw new EngineInitException("No support for multiple applications yet");
		}
		
		return ret;
	}

	private ApplicationData initializeApplication(File appDir) {
		ApplicationFolderImpl folder = new ApplicationFolderImpl(appDir, null);
		String appName = appDir.getName();
		ProcessorLog log = new ProcessorLog(appName);
		ApplicationData applicationData = ApplicationData.builder()
				.applicationDirectory(appDir)
				.applicationLog(log)
				.rootFolder(folder)
				.name(appName)
				.build();
		
		return applicationData;
	}

	public void scanApplicationDir(ApplicationData application) {
		//List<WatchedResource> removedFiles = new ArrayList<>();
		
		List<WatchedResource> addedFiles = folderScannerService.findFilesAdded(application);
		List<UserFile> addedUserFiles = new ArrayList<>();
		for(WatchedResource f : addedFiles) {
			if (f instanceof UserFile) {
				addedUserFiles.add((UserFile)f);
				continue;
			}
			else if (f instanceof SystemAttributesFile) {
				SystemAttributesFile a = (SystemAttributesFile)f;
				application.setGlobalSystemAttributes(a.getSystemAttributes());
			}
			else if (f instanceof ComponentFile) {
				ComponentFile compFile = (ComponentFile)f;
				ComponentSet set = compFile.getComponentSet();
				for(Component comp : set.getComponent()) {
					if (comp instanceof BuildComponent) {
						BuildComponent buildComp = (BuildComponent)comp;
						BuildComponentItem buildItem = patternService.createBuildComponentItem(buildComp, compFile, application);
						application.getProcessingData().getBuildsToInit().add(buildItem);
					} else {
						ComponentItem item = patternService.createComponentItem(0, comp, compFile, application);
						application.getProcessingData().getItemsToProcess().add(item);
					}
				}
			}
		}
		
		processingService.resetFolderWatchersForFiles(application, addedUserFiles);
		processingService.runProcessing(application);
	}

}

