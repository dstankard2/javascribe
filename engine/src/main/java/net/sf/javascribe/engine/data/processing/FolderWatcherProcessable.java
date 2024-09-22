package net.sf.javascribe.engine.data.processing;

import java.util.Map;

import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.resources.FolderWatcher;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
import net.sf.javascribe.engine.data.files.UserFile;

public class FolderWatcherProcessable extends ProcessableBase {

	private int itemId;
	private FolderWatcher watcher;
	private UserFile userFile;
	private ApplicationData application;
	private ProcessorLog log;
	private Map<String,String> configs;
	private ProcessingState state = ProcessingState.CREATED;
	private ApplicationFolderImpl folder;

	public FolderWatcherProcessable(int itemId, FolderWatcher watcher, UserFile userFile,
			ApplicationData application, ProcessorLog log, Map<String,String> configs,
			ApplicationFolderImpl folder) {
		this.itemId = itemId;
		this.watcher = watcher;
		this.userFile = userFile;
		this.application = application;
		this.log = log;
		this.configs = configs;
		this.folder = folder;
	}
	
	@Override
	public int getItemId() {
		return itemId;
	}

	@Override
	public int getOriginatorId() {
		return 0;
	}

	@Override
	public int getPriority() {
		return watcher.getPriority();
	}

	@Override
	public String getName() {
		return watcher.getName()+"["+userFile.getPath()+"]";
	}

	@Override
	public boolean process() {
		boolean ret = true;
		
		try {
			ProcessorContextImpl ctx = new ProcessorContextImpl(application, itemId, configs, folder, log);
			watcher.process(ctx, userFile);
		} catch(JavascribeException e) {
			this.state = ProcessingState.ERROR;
			this.log.error(e.getMessage());
			ret = false;
		}
		
		return ret;
	}

	@Override
	public ProcessorLog getLog() {
		return log;
	}

	@Override
	public Map<String, String> getConfigs() {
		return configs;
	}

	@Override
	public ProcessingState getState() {
		return state;
	}

	@Override
	public void setState(ProcessingState state) {
		this.state = state;
	}

}
