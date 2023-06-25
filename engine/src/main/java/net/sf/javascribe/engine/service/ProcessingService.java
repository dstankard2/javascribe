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
import net.sf.javascribe.engine.data.files.UserFile;
import net.sf.javascribe.engine.data.files.WatchedResource;
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

	// Modify processing data for files that have been removed.
	// When source files are removed, add them to the application's removed source files.
	public void filesRemoved(List<WatchedResource> removedFiles, ApplicationData application) {
		List<UserFile> userFilesRemoved = new ArrayList<>();
		
		removedFiles.forEach(r -> {
			if (r instanceof UserFile) {
				userFilesRemoved.add((UserFile)r);
				application.getUserFiles().remove(r.getPath());
			} else if (r instanceof ComponentFile) {
				ComponentFile c = (ComponentFile)r;
				c.getComponentSet().getComponent().forEach(comp -> {
					if (comp instanceof BuildComponent) {
						BuildComponentItem item = processingUtil.findItemForBuildComponent(application, (BuildComponent)comp);
						processingUtil.removeItem(application, item.getItemId());
					} else {
						ComponentItem item = processingUtil.findItemForComponent(application, comp);
						processingUtil.removeItem(application, item.getItemId());
					}
				});
			} else {
				// nothing to do for javascribe.properties or systemAttributes.properties
			}
		});
		
		resetFolderWatchersForFiles(application, userFilesRemoved);
	}

	// For added/removed user files, check folder watchers to see if user files apply to
	// them.
	// If they do, reset the originator of the folder watcher.
	public void resetFolderWatchersForFiles(ApplicationData application, List<UserFile> userFiles) {
		List<FolderWatcherEntry> watchers = application.getProcessingData().getFolderWatchers();

		for(FolderWatcherEntry w : watchers) {
			for (UserFile file : userFiles) {
				if (file.getPath().startsWith(w.getPath())) {
					processingUtil.removeItem(application, w.getItemId());
					this.addFolderWatcher(w, application);
				}
			}
		}
	}

	public void runProcessing(ApplicationData application) {
		ProcessingData pd = application.getProcessingData();
		boolean error = false;

		application.setState(ProcessingState.PROCESSING);

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
				processingUtil.resetItem(application, i.getItemId());
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
					proc.setState(ProcessingState.ERROR);
					application.setState(ProcessingState.ERROR);
					processingUtil.resetItem(application, proc.getItemId());
					clearAddedItems(application);
				}
			} catch(StaleDependencyException e) {
				int id = e.getItemId();
				processingUtil.resetItem(application, id);
				clearAddedItems(application);
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
				processingUtil.resetItem(application, i.getItemId());
				pd.getBuildsToInit().add(i);
				application.setState(ProcessingState.ERROR);
			} else {
				i.setState(ProcessingState.INITIALIZED);
				pd.getBuildsProcessed().add(i);
				error = !handleAddedItems(application);
			}
		}

		// Everything is successful, mark the application state
		if (application.getState() == ProcessingState.PROCESSING) {
			application.setState(ProcessingState.SUCCESS);
		}
	}
	
	public void outputPendingLogMessages(ApplicationData application) {
		// Output to log
		if (application.getMessages().size()>0) {
			logUtil.outputPendingLogMessages(application, true);
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
	}

	public boolean handleAddedItems(ApplicationData application) {
		return processingUtil.handleAddedItems(application);
	}

	public void addFolderWatcher(FolderWatcherEntry watcher, ApplicationData application) {
		processingUtil.addItem(watcher, application);
	}

	public void addItem(ApplicationData application, Item item) {
		processingUtil.addItem(item, application);
	}

	// Implementation of ProcessingContextOperations
	// TODO: Determine if this is necessary.  ItemId will have to be a parameter if so.
	@Override
	public void checkVariableTypeStale(String lang, String name, ApplicationData application) throws StaleDependencyException {
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
		ComponentItem item = new ComponentItem(id, component, configs, pattern, originatorId, folder, application);
		application.getAddedComponents().add(item);
	}

}

