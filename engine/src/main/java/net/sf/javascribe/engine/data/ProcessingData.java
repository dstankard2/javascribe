package net.sf.javascribe.engine.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.sf.javascribe.api.BuildContext;
import net.sf.javascribe.engine.data.processing.BuildComponentItem;
import net.sf.javascribe.engine.data.processing.FolderWatcherEntry;
import net.sf.javascribe.engine.data.processing.Item;
import net.sf.javascribe.engine.data.processing.Processable;

@Getter
public class ProcessingData {

	private List<Item> allItems = new ArrayList<>();
	
	private List<Processable> toProcess = new ArrayList<>();
	
	private List<Processable> processed = new ArrayList<>();

	private List<BuildComponentItem> buildsToInit = new ArrayList<>();

	private Map<String,BuildContext> availableBuildContexts = new HashMap<>();

	private List<BuildComponentItem> buildsToProcess = new ArrayList<>();

	private List<BuildComponentItem> buildsProcessed = new ArrayList<>();

	private Map<String,List<Item>> itemsPerBuild = new HashMap<>();

	//private List<FolderWatcherEntry> folderWatchers = new ArrayList<>();
	
	public List<FolderWatcherEntry> getFolderWatchers() {
		return allItems.stream().filter(i -> i instanceof FolderWatcherEntry).map(FolderWatcherEntry.class::cast).toList();
	}

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private int nextId = 1;

	public int nextId() {
		return nextId++;
	}

	public Item getItem(int id) {
		Item ret = null;
		
		ret = allItems.stream().filter(i -> i.getItemId()==id).findFirst().orElse(null);

		return ret;
	}
	
}

