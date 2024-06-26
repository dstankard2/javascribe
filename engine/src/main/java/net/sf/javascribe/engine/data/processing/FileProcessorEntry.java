package net.sf.javascribe.engine.data.processing;

import java.util.Map;

import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.resources.FileProcessor;
import net.sf.javascribe.engine.ComponentContainer;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
import net.sf.javascribe.engine.data.files.UserFile;
import net.sf.javascribe.engine.service.ProcessingContextOperations;
import net.sf.javascribe.engine.util.ConfigUtil;

public class FileProcessorEntry extends ProcessableBase {
	private ProcessorContextImpl procCtx;
	private int originatorId;
	private Map<String,String> configs;
	private FileProcessor fileProcessor;
	private ProcessorLog log;
	private ProcessingState state;
	private String name;
	
	public FileProcessorEntry(int originatorId, FileProcessor fileProcessor, UserFile file,
			ApplicationData application, Map<String,String> configs, 
			ProcessingContextOperations ops, ApplicationFolderImpl folder) {
		this.name = fileProcessor.getName();
		this.fileProcessor = fileProcessor;
		this.originatorId = originatorId;
		this.configs = configs;
		this.state = ProcessingState.CREATED;
		this.log = new ProcessorLog(name, application, folder.getLogLevel());
		procCtx = new ProcessorContextImpl(application, originatorId, configs, folder, 
				log);
		fileProcessor.init(procCtx);
		fileProcessor.setFile(file);
	}
	
	@Override
	public int getItemId() {
		return 0;
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
	public boolean process() {
		boolean success = true;

		ConfigUtil configUtil = ComponentContainer.get().getComponent(ConfigUtil.class);
		success = configUtil.populateConfigurations(fileProcessor, log, configs);
		
		if (success) {
			try {
				fileProcessor.process();
			} catch(JavascribeException e) {
				success = false;
				this.log.error(e.getMessage(), e);
			}
		}
		return success;
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
	public int getOriginatorId() {
		return originatorId;
	}

	@Override
	public String getName() {
		return name;
	}

}
