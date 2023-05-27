package net.sf.javascribe.engine.data;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.sf.javascribe.engine.data.processing.BuildComponentItem;
import net.sf.javascribe.engine.data.processing.FolderWatcherEntry;
import net.sf.javascribe.engine.data.processing.Item;

@Getter
public class ProcessingData {

	private List<Item> itemsToProcess = new ArrayList<>();
	
	private List<Item> itemsProcessed = new ArrayList<>();

	private List<BuildComponentItem> buildsToInit = new ArrayList<>();

	private List<BuildComponentItem> buildsToProcess = new ArrayList<>();

	private List<BuildComponentItem> buildsProcessed = new ArrayList<>();

	private List<FolderWatcherEntry> folderWatchers = new ArrayList<>();

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private int nextId = 1;

	public int nextId() {
		return nextId++;
	}

	public Item getItem(int id) {
		Item ret = null;
		
		ret = itemsProcessed.stream().filter(i -> i.getItemId()==id).findFirst().orElse(null);
		if (ret==null) {
			ret = itemsToProcess.stream().filter(i -> i.getItemId()==id).findFirst().orElse(null);
		}
		if (ret==null) {
			ret = buildsToInit.stream().filter(i -> i.getItemId()==id).findFirst().orElse(null);
		}
		if (ret==null) {
			ret = buildsToProcess.stream().filter(i -> i.getItemId()==id).findFirst().orElse(null);
		}
		if (ret==null) {
			ret = buildsProcessed.stream().filter(i -> i.getItemId()==id).findFirst().orElse(null);
		}

		return ret;
	}
	
}

