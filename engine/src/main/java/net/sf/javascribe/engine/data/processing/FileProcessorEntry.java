package net.sf.javascribe.engine.data.processing;

import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.plugin.ProcessorLogMessage;
import net.sf.javascribe.api.resources.FileProcessor;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
import net.sf.javascribe.engine.data.files.UserFile;
import net.sf.javascribe.engine.service.EngineResources;
import net.sf.javascribe.engine.service.ProcessingContextOperations;

public class FileProcessorEntry extends ProcessableBase implements Item {
	private ProcessorContextImpl procCtx;
	private int id;
	private int originatorId;
	private ApplicationData application;
	private Map<String,String> configs;
	private EngineResources engineResources;
	private ApplicationFolderImpl folder;
	private FileProcessor fileProcessor;
	private ProcessorLog log;
	private ProcessingState state;
	private String name;
	
	public FileProcessorEntry(int id, int originatorId, FileProcessor fileProcessor, UserFile file,
			ApplicationData application, Map<String,String> configs, EngineResources engineResources,
			ProcessingContextOperations ops, ApplicationFolderImpl folder) {
		this.id = id;
		this.name = fileProcessor.getName();
		this.originatorId = originatorId;
		this.application = application;
		this.configs = configs;
		this.engineResources = engineResources;
		this.state = ProcessingState.CREATED;
		this.log = new ProcessorLog(name);
		this.folder = folder;
		procCtx = new ProcessorContextImpl(name, application, id, configs, folder.getBuildContext(), 
				folder, engineResources, ops, log);
		fileProcessor.init(procCtx);
		fileProcessor.setFile(file);
	}
	
	@Override
	public void setState(ProcessingState state) {
		this.state = state;
	}

	@Override
	public int getPriority() {
		return fileProcessor.getPriority();
	}

	@Override
	public List<ProcessorLogMessage> getMessages() {
		return log.getMessages(false);
	}

	@Override
	public boolean process() {
		return false;
	}

	@Override
	public void clearLogMessages() {
		log.getMessages(true);
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
	public int getItemId() {
		return id;
	}

	@Override
	public int getOriginatorId() {
		return originatorId;
	}

	@Override
	public String getName() {
		return name;
	}

}
