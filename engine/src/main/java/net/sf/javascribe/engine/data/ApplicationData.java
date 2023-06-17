package net.sf.javascribe.engine.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.api.plugin.ProcessorLogMessage;
import net.sf.javascribe.api.types.VariableType;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
import net.sf.javascribe.engine.data.files.UserFile;
import net.sf.javascribe.engine.data.processing.ComponentItem;
import net.sf.javascribe.engine.data.processing.FileProcessorEntry;
import net.sf.javascribe.engine.data.processing.FolderWatcherEntry;
import net.sf.javascribe.engine.data.processing.ProcessingState;
import net.sf.javascribe.engine.data.processing.ProcessorLog;

@Getter
@Setter
@Builder
public class ApplicationData {

	private File applicationDirectory;
	
	private ApplicationFolderImpl rootFolder;
	
	private File outputDirectory;
	
	private String name;

	@Builder.Default
	private List<ProcessorLogMessage> messages = new ArrayList<>();
	
	@Builder.Default
	private ProcessingState state = ProcessingState.CREATED;

	@Builder.Default
	private Map<String,String> globalSystemAttributes = new HashMap<>();

	@Builder.Default
	private Map<String,String> systemAttributes = new HashMap<>();
	
	@Builder.Default
	private Map<String,Object> objects = new HashMap<>();

	@Builder.Default
	private Map<String,Map<String,VariableType>> types = new HashMap<>();

	@Builder.Default
	private Map<String,SourceFile> sourceFiles = new HashMap<>();

	@Builder.Default
	private Map<String,UserFile> userFiles = new HashMap<>();

	// Cannot use Builder default value here as the constructor for the log will take "this"
	// as an arg
	private ProcessorLog applicationLog;

	@Builder.Default
	private ProcessingData processingData = new ProcessingData();
	
	@Builder.Default
	private DependencyData dependencyData = new DependencyData();
	
	public String getSystemAttribute(String name) {
		String ret = globalSystemAttributes.get(name);
		if (ret==null) {
			ret = systemAttributes.get(name);
		}
		return ret;
	}

	public SourceFile getSourceFile(String path) {
		SourceFile ret = null;
		Entry<String,SourceFile> entry = addedSourceFiles.entrySet().stream().filter(e -> e.getKey().equals(path)).findFirst().orElse(null);
		
		if (entry==null) {
			entry = sourceFiles.entrySet().stream().filter(e -> e.getKey().equals(path)).findFirst().orElse(null);
		}
		
		if (entry!=null) {
			ret = entry.getValue();
		}
		
		return ret;
	}

	@Builder.Default
	private Map<String,SourceFile> addedSourceFiles = new HashMap<>();
	
	@Builder.Default
	private List<FolderWatcherEntry> addedFolderWatchers = new ArrayList<>();

	@Builder.Default
	private List<FileProcessorEntry> addedFileProcessors = new ArrayList<>();

	// TODO: See if this is necessary
	@Builder.Default
	private List<ComponentItem> addedComponents = new ArrayList<>();

	@Builder.Default
	private List<SourceFile> removedSourceFiles = new ArrayList<>();
	
}

