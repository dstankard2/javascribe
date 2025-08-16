package net.sf.javascribe.engine.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.plugin.ProcessorLogMessage;
import net.sf.javascribe.api.types.ListType;
import net.sf.javascribe.api.types.VariableType;
import net.sf.javascribe.engine.ComponentContainer;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
import net.sf.javascribe.engine.data.files.SystemAttributesFile;
import net.sf.javascribe.engine.data.files.UserFile;
import net.sf.javascribe.engine.data.processing.LogContext;
import net.sf.javascribe.engine.data.processing.ProcessingState;
import net.sf.javascribe.engine.data.processing.ProcessorLog;
import net.sf.javascribe.engine.service.LanguageSupportService;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ApplicationData implements LogContext {

	private File applicationDirectory;
	
	private ApplicationFolderImpl rootFolder;
	
	private File outputDirectory;
	
	private String name;

	@Builder.Default
	private List<ProcessorLogMessage> messages = new ArrayList<>();
	
	@Builder.Default
	private ProcessingState state = ProcessingState.CREATED;

	@Builder.Default
	private SystemAttributesFile systemAttributesFile = null;
	
	public Map<String,String> getGlobalSystemAttributes() {
		if (systemAttributesFile!=null) {
			return systemAttributesFile.getSystemAttributes();
		}
		return new HashMap<>();
	}

	@Builder.Default
	private Map<String,String> systemAttributes = new HashMap<>();
	
	@Builder.Default
	private Map<String,Object> objects = new HashMap<>();

	@Builder.Default
	private Map<String,Map<String,VariableType>> applicationTypes = new HashMap<>();
	
	@Builder.Default
	private Map<String,Map<String,VariableType>> baseTypes = new HashMap<>();

	// TODO: Throw stale dependency exception?
	public VariableType getType(String lang, String name) throws JavascribeException {
		VariableType ret = null;
		
		Map<String,VariableType> baseTypes = this.baseTypes.get(lang);
		if (baseTypes==null) {
			LanguageSupportService srv = ComponentContainer.get().getComponent(LanguageSupportService.class);
			baseTypes = srv.getBaseTypes(lang);
			if (baseTypes==null) {
				throw new JavascribeException("The programming language '"+lang+"' is not supported");
			}
			this.baseTypes.put(lang, baseTypes);
		}
		if (name.startsWith("list/")) {
			ListType listType = (ListType)baseTypes.get("list");
			if (listType==null) {
				throw new JavascribeException("Couldn't find a list type for language "+lang);
			}
			String eltTypeName = name.substring(5);
			VariableType eltType = getType(lang, eltTypeName);
			if (eltType==null) {
				throw new JavascribeException("Couldn't find list element type "+eltTypeName);
			}
			return listType.getListTypeWithElementTypName(eltType);
		}
		ret = baseTypes.get(name);
		if (ret==null) {
			Map<String,VariableType> appTypes = applicationTypes.get(lang);
			if (appTypes==null) {
				appTypes = new HashMap<>();
				applicationTypes.put(lang, appTypes);
			}
			ret = appTypes.get(name);
		}
		
		return ret;
	}
	
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
		String ret = getGlobalSystemAttributes().get(name);
		if (ret==null) {
			ret = systemAttributes.get(name);
		}
		return ret;
	}

	public SourceFile getSourceFile(String path) {
		SourceFile ret = null;
		ret = addedSourceFiles.get(path);
		if (ret==null) {
			ret = sourceFiles.get(path);
		}
		
		return ret;
	}

	@Builder.Default
	private Map<String,Object> addedObjects = new HashMap<>();
	
	@Builder.Default
	private Map<String,SourceFile> addedSourceFiles = new HashMap<>();
	
	@Override
	public void appendMessage(ProcessorLogMessage message) {
		this.getMessages().add(message);
	}

}

