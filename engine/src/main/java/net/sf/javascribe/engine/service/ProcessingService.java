package net.sf.javascribe.engine.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.api.config.BuildComponent;
import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.api.resources.FileProcessor;
import net.sf.javascribe.api.resources.FolderWatcher;
import net.sf.javascribe.engine.ComponentDependency;
import net.sf.javascribe.engine.StaleDependencyException;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.ProcessingData;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
import net.sf.javascribe.engine.data.files.ComponentFile;
import net.sf.javascribe.engine.data.files.DefaultBuildComponent;
import net.sf.javascribe.engine.data.files.UserFile;
import net.sf.javascribe.engine.data.processing.BuildComponentItem;
import net.sf.javascribe.engine.data.processing.ComponentItem;
import net.sf.javascribe.engine.data.processing.FileProcessorEntry;
import net.sf.javascribe.engine.data.processing.FolderWatcherEntry;
import net.sf.javascribe.engine.data.processing.Item;
import net.sf.javascribe.engine.data.processing.Processable;
import net.sf.javascribe.engine.data.processing.ProcessingState;
import net.sf.javascribe.engine.util.LogUtil;
import net.sf.javascribe.engine.util.ProcessingUtil;

public class ProcessingService implements ProcessingContextOperations {

	private PatternService patternService;
	@ComponentDependency
	public void setPatternService(PatternService srv) {
		this.patternService = srv;
	}

	private ProcessingUtil processingUtil;
	@ComponentDependency
	public void setProcessingUtil(ProcessingUtil u) {
		this.processingUtil = u;
	}

	private LogUtil logUtil;
	@ComponentDependency
	public void setLogUtil(LogUtil u) {
		this.logUtil = u;
	}

	public void removeComponentFiles(List<ComponentFile> componentFiles, ApplicationData application) {
		List<Item> itemsToRemove = new ArrayList<>();
		componentFiles.forEach(cf -> {
			cf.getComponentSet().getComponent().forEach(comp -> {
				if (comp instanceof BuildComponent) {
					BuildComponentItem item = processingUtil.findItemForBuildComponent(application, (BuildComponent)comp);
					itemsToRemove.add(item);
					//processingUtil.removeItem(application, item.getItemId());
				} else {
					ComponentItem item = processingUtil.findItemForComponent(application, comp);
					itemsToRemove.add(item);
					//processingUtil.removeItem(application, item.getItemId());
				}
			});
		});
		processingUtil.removeItems(application, itemsToRemove);
	}

	public void removeUserFiles(List<UserFile> userFiles, ApplicationData application) {
		userFiles.forEach(uf -> {
			application.getUserFiles().remove(uf.getPath());
		});
		resetFolderWatchersForFiles(application, userFiles);
	}

	// For added/removed user files, check folder watchers to see if user files apply to
	// them.
	// If they do, reset the folder watcher
	// If they do, reset the originator of the folder watcher?
	public void resetFolderWatchersForFiles(ApplicationData application, List<UserFile> userFiles) {
		List<FolderWatcherEntry> watchers = application.getProcessingData().getFolderWatchers();
		List<Integer> idsToRemove = new ArrayList<>();

		for(FolderWatcherEntry w : watchers) {
			for (UserFile file : userFiles) {
				if (file.getPath().startsWith(w.getPath())) {
					idsToRemove.add(w.getItemId());
					// processingUtil.resetItems(application, Collections.singletonList(w.getItemId()));
				}
			}
		}
		
		processingUtil.resetItems(application, idsToRemove);
	}

	public void runProcessing(ApplicationData application) {
		ProcessingData pd = application.getProcessingData();
		ApplicationFolderImpl rootFolder = application.getRootFolder();
		boolean error = false;

		application.setState(ProcessingState.PROCESSING);

		// Before processing, check to see if there is a build in the root directory.
		// If there isn't, and there isn't a build to init in the root directory, create a 
		// default build.
		
		boolean buildToInitInRootFolder = pd.getBuildsToInit().stream().anyMatch(b -> {
			return b.getFolder()==rootFolder;
		});
		
		if ((application.getRootFolder().getBuildComponent()==null) && (!buildToInitInRootFolder)) {
			BuildComponent buildComp = new DefaultBuildComponent();
			BuildComponentItem item = new BuildComponentItem(pd.nextId(), buildComp, application.getRootFolder(), null, rootFolder.getProperties(), application);
			processingUtil.addItem(item, application);
		} else {
			// If there is a build component in the root folder, and there is a build to init in the root folder, 
			// remove the build in the root folder
			if ((application.getRootFolder().getBuildComponent()!=null) && (buildToInitInRootFolder)) {
				processingUtil.removeItems(application, Collections.singletonList((Item)application.getRootFolder().getBuildComponent()));
			}
		}
		
		// Init builds
		while ((pd.getBuildsToInit().size() > 0) && (!error)) {
			BuildComponentItem i = pd.getBuildsToInit().get(0);
			pd.getBuildsToInit().remove(0);

			application.getApplicationLog().info("Initializing Build - "+i.getName());
			error = !processingUtil.initBuild(i, application);
			if (!error) {
				i.setState(ProcessingState.INITIALIZED);
				pd.getBuildsToProcess().add(i);
				error = !handleAddedItems(application);
			} else {
				i.setState(ProcessingState.ERROR);
				clearAddedItems(application);
				processingUtil.resetItems(application, Collections.singletonList(i.getItemId()));
				pd.getBuildsToInit().add(i);
				application.setState(ProcessingState.ERROR);
			}
		}
		
		// Process items
		while ((pd.getToProcess().size() > 0) && (!error)) {
			Collections.sort(pd.getToProcess());
			Processable proc = pd.getToProcess().get(0);
			pd.getToProcess().remove(0);
			try {
				application.getApplicationLog().info("Processing item '"+proc.getName()+"'");
				proc.setState(ProcessingState.PROCESSING);
				error = !processingUtil.processItem(proc, application);
				
				if (!error) {
					proc.setState(ProcessingState.SUCCESS);
					pd.getProcessed().add(proc);
					handleAddedItems(application);
				} else {
					application.setState(ProcessingState.ERROR);
					processingUtil.resetItems(application, Collections.singletonList(proc.getItemId()));
					clearAddedItems(application);
					proc.setState(ProcessingState.ERROR);
				}
			} catch(StaleDependencyException e) {
				int id = e.getItemId();
				processingUtil.resetItems(application, Collections.singletonList(id));
				// TODO: Not certain we want to clear added items.  Resetting the stale id might be enough.
				// clearAddedItems(application);
			} catch (Throwable e) {
				// This is an internal engine error.
				application.getApplicationLog().error("Internal engine error - turn on engine debugging to see the cause");
				application.getApplicationLog().debug(e.getMessage(), e);
				error = true;
			}
		}

		// Process builds
		while ((pd.getBuildsToProcess().size() > 0) && (!error)) {
			BuildComponentItem i = pd.getBuildsToProcess().get(0);
			pd.getBuildsToProcess().remove(0);

			application.getApplicationLog().info("Processing Build - "+i.getName());
			error = !processingUtil.processBuild(i, application);

			if (error) {
				i.setState(ProcessingState.ERROR);
				clearAddedItems(application);
				processingUtil.resetItems(application, Collections.singletonList(i.getItemId()));
				pd.getBuildsToInit().add(i);
				application.setState(ProcessingState.ERROR);
			} else {
				i.setState(ProcessingState.SUCCESS);
				pd.getBuildsProcessed().add(i);
				error = !handleAddedItems(application);
			}
		}

		if (error) {
			application.setState(ProcessingState.ERROR);
		} else {
			application.setState(ProcessingState.SUCCESS);
		}
	}
	
	public void outputPendingLogMessages(ApplicationData application) {
		// Output to log
		if (application.getMessages().size()>0) {
			logUtil.outputPendingLogMessages(application, false);
		}
	}
	
	public void outputMessageToLog(String message) {
		logUtil.outputMessageToLog(message);
	}

	protected void clearAddedItems(ApplicationData application) {
		application.getAddedComponents().clear();
		application.getAddedFileProcessors().clear();
		application.getAddedFolderWatchers().clear();
		application.getAddedSourceFiles().clear();
		application.getAddedObjects().clear();
	}

	public boolean handleAddedItems(ApplicationData application) {
		return processingUtil.handleAddedItems(application);
	}

	public void addItem(ApplicationData application, Item item) {
		processingUtil.addItem(item, application);
	}

	// Implementation of ProcessingContextOperations
	// TODO: Determine if this is necessary.
	@Override
	public void checkVariableTypeStale(int id, String lang, String name, ApplicationData application) throws StaleDependencyException {
		// TODO Auto-generated method stub
	}

	@Override
	public void addSourceFile(SourceFile sourceFile, ApplicationData application) {
		application.getAddedSourceFiles().put(sourceFile.getPath(), sourceFile);
	}

	@Override
	public void addFolderWatcher(int originatorId, String path, FolderWatcher watcher, 
			Map<String,String> configs, ApplicationFolderImpl folder, ApplicationData application) {
		int id = application.getProcessingData().nextId();
		FolderWatcherEntry entry = new FolderWatcherEntry(id, watcher, path, originatorId, configs, application, folder);
		application.getAddedFolderWatchers().add(entry);
	}

	@Override
	public void addFileProcessor(int originatorId, UserFile userFile, FileProcessor processor, Map<String,String> configs, ApplicationFolderImpl folder, ApplicationData application) {
		FileProcessorEntry e = new FileProcessorEntry(originatorId, processor, userFile, application, configs, this, folder);
		application.getAddedFileProcessors().add(e);
	}

	@Override
	public void addComponent(int originatorId, Component component, Map<String, String> configs,
			ApplicationFolderImpl folder, ApplicationData application) {
		int id = application.getProcessingData().nextId();
		RegisteredComponentPattern pattern = patternService.getPattern(component);
		ComponentItem item = new ComponentItem(id, component, configs, pattern, originatorId, folder, application, 0);
		application.getAddedComponents().add(item);
	}

}

