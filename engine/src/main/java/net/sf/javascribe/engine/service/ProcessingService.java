package net.sf.javascribe.engine.service;

import java.util.List;

import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.engine.ComponentDependency;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.ProcessingData;
import net.sf.javascribe.engine.data.files.UserFile;
import net.sf.javascribe.engine.data.processing.BuildComponentItem;
import net.sf.javascribe.engine.data.processing.FolderWatcherEntry;
import net.sf.javascribe.engine.data.processing.Item;
import net.sf.javascribe.engine.data.processing.ProcessingState;
import net.sf.javascribe.engine.util.ProcessingUtil;

public class ProcessingService {

	private ProcessingUtil processingUtil;
	
	@ComponentDependency
	public void setProcessingUtil(ProcessingUtil u) {
		this.processingUtil = u;
	}

	// For added user files, check folder watchers to see if user files apply to them.  If they do, reset the originator of the folder 
	// watcher.
	public void resetFolderWatchersForFiles(ApplicationData application, List<UserFile> userFiles) {
		List<FolderWatcherEntry> folderWatchers = application.getProcessingData().getFolderWatchers();

		for(UserFile file : userFiles) {
			folderWatchers.stream().filter(w -> {
				return file.getPath().startsWith(w.getPath());
			}).forEach(e -> {
				processingUtil.resetItem(application, e.getOriginatorId());
			});
		}
	}
	
	public void runProcessing(ApplicationData application) {
		ProcessingData pd = application.getProcessingData();
		boolean error = false;
		
		application.setState(ProcessingState.PROCESSING);
		
		// Init builds
		while((pd.getBuildsToInit().size()>0) && (!error)) {
			BuildComponentItem i = pd.getBuildsToInit().get(0);
			pd.getBuildsToInit().remove(0);
			try {
				processingUtil.initBuild(i, application);
				i.setState(ProcessingState.INITIALIZED);
				pd.getBuildsToProcess().add(i);
			} catch(JavascribeException e) {
				i.setState(ProcessingState.ERROR);
				pd.getBuildsToInit().add(i);
				application.setState(ProcessingState.ERROR);
				error = true;
			}
		}

		// Process items
		while((pd.getItemsToProcess().size()>0) && (!error)) {
			Item item = pd.getItemsToProcess().get(0);
			pd.getItemsToProcess().remove(0);
			try {
				item.setState(ProcessingState.PROCESSING);
				processingUtil.processItem(item, application);
				item.setState(ProcessingState.SUCCESS);
				pd.getItemsProcessed().add(item);
			} catch(JavascribeException e) {
				item.setState(ProcessingState.ERROR);
				processingUtil.removeItem(application, item.getItemId());
				pd.getItemsToProcess().add(item);
			}
		}
		
		// Process builds
		
		
		System.out.println("Time to process");
	}

}
