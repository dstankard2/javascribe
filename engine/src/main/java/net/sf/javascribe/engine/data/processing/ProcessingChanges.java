package net.sf.javascribe.engine.data.processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import lombok.Getter;
import lombok.Setter;
import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.api.resources.FileProcessor;
import net.sf.javascribe.api.resources.FolderWatcher;
import net.sf.javascribe.api.types.VariableType;

// TODO: Do we really want to do this?  or just put changes right into the application?
// When processing, we can check an item's status before doing anything with it, and unload/reload it?
@Getter
@Setter
public class ProcessingChanges {

	protected Set<SourceFile> sourceFiles = new HashSet<>();
	protected List<Pair<String,VariableType>> types = new ArrayList<>();
	protected Map<String,Object> objects = new HashMap<>();

	protected Map<String,String> attributesAdded = new HashMap<>();
	protected List<String> attributesOriginated = new ArrayList<>();
	protected List<String> attributeDependencies = new ArrayList<>();

	protected List<Pair<String,FolderWatcher>> folderWatchersAdded = new ArrayList<>();
	protected List<Pair<String,FileProcessor>> fileProcessorsAdded = new ArrayList<>();
	
	protected List<Component> componentsAdded = new ArrayList<>();

}
