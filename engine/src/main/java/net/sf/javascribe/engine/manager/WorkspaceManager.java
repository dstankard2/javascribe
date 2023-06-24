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
import net.sf.javascribe.engine.data.processing.ProcessingState;
import net.sf.javascribe.engine.data.processing.ProcessorLog;
import net.sf.javascribe.engine.service.FolderScannerService;
import net.sf.javascribe.engine.service.OutputService;
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
	
	private OutputService outputService = null;
	
	@ComponentDependency
	public void setOutputService(OutputService outputService) {
		this.outputService = outputService;
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
		String appName = appDir.getName();
		// Circular dependency between ApplicationData and ApplicationFolderImpl
		ApplicationData applicationData = ApplicationData.builder()
				.applicationDirectory(appDir)
				.name(appName)
				.build();
		ApplicationFolderImpl folder = new ApplicationFolderImpl(appDir, applicationData);
		applicationData.setRootFolder(folder);
		ProcessorLog log = new ProcessorLog(appName, applicationData, folder.getLogLevel());
		applicationData.setApplicationLog(log);
		folderScannerService.initFolder(folder);
		return applicationData;
	}

	public void scanApplicationDir(ApplicationData application, boolean firstRun, boolean onlyRun) {
		List<WatchedResource> removedFiles = folderScannerService.findFilesRemoved(application);
		List<UserFile> removedUserFiles = new ArrayList<>();
		boolean filesChanged = false;
		
		// Remove user files
		for(WatchedResource res : removedFiles) {
			if (res instanceof UserFile) {
				removedUserFiles.add((UserFile)res);
			}
		}
		
		if ((removedFiles.size()>0) ) {
			filesChanged = true;
			// evaluate workspace files that have been removed.
			processingService.filesRemoved(removedFiles, application);

			// Application's removedSourceFiles should be populated.
			outputService.deleteRemovedFiles(removedUserFiles, application);
		}
		
		List<WatchedResource> addedFiles = folderScannerService.findFilesAdded(application);

		// Track user files that are being added so that they can be written to output.
		List<UserFile> addedUserFiles = new ArrayList<>();
		
		if (addedFiles.size()>0) {
			filesChanged = true;
		}
		
		if (filesChanged) {
			if ((!firstRun) && (!onlyRun)) {
				application.getApplicationLog().info("*** Scanned application '"+application.getName()+"' and found changes ***");
			}
		}
		
		for(WatchedResource f : addedFiles) {
			if (f instanceof UserFile) {
				addedUserFiles.add((UserFile)f);
				application.getUserFiles().put(f.getPath(), (UserFile)f);
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
						// TODO: Check if there is more than one build in the folder.  Do something
						BuildComponent buildComp = (BuildComponent)comp;
						BuildComponentItem buildItem = patternService.createBuildComponentItem(buildComp, compFile, application);
						processingService.addItem(application, buildItem);
					} else {
						ComponentItem item = patternService.createComponentItem(0, comp, compFile, application);
						processingService.addItem(application, item);
					}
				}
			}
		}
		
		if (addedUserFiles.size()>0) {
			processingService.resetFolderWatchersForFiles(application, addedUserFiles);
		}
		if (filesChanged) {
			processingService.runProcessing(application);
		}
		if ((addedUserFiles.size()>0) || (application.getAddedSourceFiles().size()>0)) {
			//application.getApplicationLog().info("Writing output files");
			outputService.writeUserFiles(application, addedUserFiles);
			outputService.writeSourceFiles(application);
		}

		// Output to log
		processingService.outputPendingLogMessages(application);

		if (filesChanged) {
			// Output a success message
			processingService.outputMessageToLog("\n*********************************");
			if (application.getState()==ProcessingState.SUCCESS) {
				processingService.outputMessageToLog("***   PROCESSING SUCCESSFUL   ***");
			} else if (application.getState()==ProcessingState.ERROR) {
				processingService.outputMessageToLog("***     PROCESSING ERROR      ***");
			}
			processingService.outputMessageToLog("*********************************\n");
		}
	}

}

