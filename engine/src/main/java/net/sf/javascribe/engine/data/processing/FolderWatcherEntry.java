package net.sf.javascribe.engine.data.processing;

import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.resources.FolderWatcher;
import net.sf.javascribe.engine.data.files.UserFile;
import net.sf.javascribe.engine.service.EngineResources;

public class FolderWatcherEntry {
	
	private FolderWatcher folderWatcher;
	private String path;
	private int originatorId;
	private Map<String,String> configs;
	private EngineResources engineResources;
	
	public FolderWatcherEntry(FolderWatcher folderWatcher, String path, int originatorId, 
			Map<String,String> configs, EngineResources engineResources) {
		this.folderWatcher = folderWatcher;
		this.path = path;
		this.originatorId = originatorId;
		this.configs = configs;
		this.engineResources = engineResources;
	}
	
	public int getOriginatorId() {
		return this.originatorId;
	}

	public String getPath() {
		return path;
	}

	public void run(List<UserFile> userFiles) {
		//ProcessorContext ctx
	}

}

