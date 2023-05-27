package net.sf.javascribe.engine.data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.api.types.VariableType;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
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
	private ProcessorLog applicationLog = new ProcessorLog("APP");

	@Builder.Default
	private ProcessingData processingData = new ProcessingData();
	
	@Builder.Default
	private DependencyData dependencyData = new DependencyData();
	
}

