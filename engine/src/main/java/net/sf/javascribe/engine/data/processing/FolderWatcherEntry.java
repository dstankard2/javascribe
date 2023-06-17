package net.sf.javascribe.engine.data.processing;

import java.util.Map;

import net.sf.javascribe.api.resources.FolderWatcher;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
import net.sf.javascribe.engine.data.files.UserFile;

// TODO: When applied to a file, create a processable at the appropriate priority
public class FolderWatcherEntry implements Item {
	
	private FolderWatcher folderWatcher;
	private String path;
	private int originatorId;
	private Map<String,String> configs;
	private ProcessorLog log = null;
	private ApplicationData application;
	private ApplicationFolderImpl folder;
	private int id;
	private ProcessingState state = ProcessingState.CREATED;
	
	public FolderWatcherEntry(int id, FolderWatcher folderWatcher, String path, int originatorId, 
			Map<String,String> configs, ApplicationData application, 
			ApplicationFolderImpl folder) {
		this.id = id;
		this.folderWatcher = folderWatcher;
		this.path = path;
		this.originatorId = originatorId;
		this.configs = configs;
		this.application = application;
		this.folder = folder;
		this.log = new ProcessorLog(folderWatcher.getName(), application);
	}

	public int getOriginatorId() {
		return this.originatorId;
	}

	public String getPath() {
		return path;
	}

	public void applyToUserFile(UserFile file) {
		//boolean ret = true;
		
		//try {
			FolderWatcherProcessable proc = new FolderWatcherProcessable(
				id, folderWatcher, file, application, log, configs, folder
			);
			application.getProcessingData().getToProcess().add(proc);
			//ProcessorContextImpl ctx = new ProcessorContextImpl(application, id, configs, folder, log);
			//folderWatcher.process(ctx, file);
			/*
		} catch(JavascribeException e) {
			this.state = ProcessingState.ERROR;
			this.log.error("Error when applying file '"+file.getPath()+"'", e);
			ret = false;
		}
		*/
		//return ret;
	}

	@Override
	public int getItemId() {
		return id;
	}

	@Override
	public String getName() {
		return folderWatcher.getName();
	}

	@Override
	public void setState(ProcessingState state) {
		this.state = state;
	}

	@Override
	public ProcessingState getState() {
		return state;
	}

}

