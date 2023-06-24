package net.sf.javascribe.engine.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.processing.Item;

public class DependencyUtil {

	public List<SourceFile> getSourceFilesFromId(int itemId, ApplicationData application) {
		List<SourceFile> ret = new ArrayList<>();
		
		List<String> paths = application.getDependencyData().getSrcDependencies().entrySet().stream().filter(e -> {
			return e.getValue().contains(itemId);
		}).map(Map.Entry::getKey).collect(Collectors.toList());

		paths.forEach(path -> {
			SourceFile sf = application.getSourceFile(path);
			if (sf!=null) {
				ret.add(sf);
			}
		});
		
		return ret;
	}
	
	public List<Item> getItemsThatOriginateFrom(int id, ApplicationData application) {
		List<Item> ret = new ArrayList<>();
		
		application.getProcessingData().getAllItems().forEach(i -> {
			if (i.getOriginatorId()==id) {
				ret.add(i);
			}
		});
		
		return ret;
	}
	
	public List<String> getObjectDependencies(int id, ApplicationData application) {
		List<String> ret = new ArrayList<>();
		
		application.getDependencyData().getObjectDependencies().entrySet().forEach(entry -> {
			if (entry.getValue().contains(id)) {
				ret.add(entry.getKey());
			}
		});
		
		return ret;
	}

	// When an item originates an attribute it also depends on the attribute, so only check dependencies here
	public Set<String> getSystemAttributeDependencies(int id, ApplicationData application) {
		Set<String> ret = new HashSet<>();

		application.getDependencyData().getAttributeDependencies().entrySet().forEach(entry -> {
			if (entry.getValue().contains(id)) {
				ret.add(entry.getKey());
			}
		});

		return ret;
	}
	
	public List<Pair<String,String>> getTypeDependencies(int id, ApplicationData application) {
		List<Pair<String,String>> ret = new ArrayList<>();
		
		application.getDependencyData().getTypeDependencies().entrySet().forEach(langEntry -> {
			String lang = langEntry.getKey();
			langEntry.getValue().entrySet().forEach(entry -> {
				if (entry.getValue().contains(id)) {
					ret.add(Pair.of(lang, entry.getKey()));
				}
			});
		});
		
		return ret;
	}

}

