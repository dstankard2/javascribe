package net.sf.javascribe.engine.util;

import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.ProcessingData;
import net.sf.javascribe.engine.data.processing.BuildComponentItem;
import net.sf.javascribe.engine.data.processing.Item;

// Contains atomic processing operations
public class ProcessingUtil {

	// TODO: Implement
	public boolean originatesAnything(int id, ApplicationData application) {
		return true;
	}

	public void resetItem(ApplicationData application, int id) {
		ProcessingData pd = application.getProcessingData();
		Item item = pd.getItem(id);
		
		if (item==null) return;

		removeItem(application, id);
		pd.getItemsToProcess().add(item);
	}
	
	public void removeItem(ApplicationData application, int id) {
		ProcessingData pd = application.getProcessingData();
		Item item = pd.getItem(id);
		if (item==null) return;
		
		pd.getItemsProcessed().remove(item);
		pd.getItemsToProcess().remove(item);
		
		// Remove source files that originate from this item
		
		// Remove items that originate from this item
		
		// Remove folder watchers that originate from this item
		
		
	}
	
	public void processItem(Item item, ApplicationData application) throws JavascribeException {
		System.out.println("hi");
	}
	
	public void initBuild(BuildComponentItem buildItem, ApplicationData application) throws JavascribeException {
		
	}
	
	public void processBuild(BuildComponentItem buildItem, ApplicationData application) {
		
	}
	
}
