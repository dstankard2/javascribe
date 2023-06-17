package net.sf.javascribe.engine.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.api.config.BuildComponent;
import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.engine.ComponentDependency;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.ProcessingData;
import net.sf.javascribe.engine.data.processing.BuildComponentItem;
import net.sf.javascribe.engine.data.processing.ComponentItem;
import net.sf.javascribe.engine.data.processing.Item;
import net.sf.javascribe.engine.data.processing.Processable;
import net.sf.javascribe.engine.data.processing.ProcessingState;

// Contains atomic processing operations
public class ProcessingUtil {

	private DependencyUtil dependencyUtil = null;
	@ComponentDependency
	public void setDependencyUtil(DependencyUtil dependencyUtil) {
		this.dependencyUtil = dependencyUtil;
	}
	
	private OutputUtil outputUtil = null;
	@ComponentDependency
	public void setOutputUtil(OutputUtil outputUtil) {
		this.outputUtil = outputUtil;
	}
	
	// TODO: Implement
	public boolean originatesAnything(int id, ApplicationData application) {
		return true;
	}

	public ComponentItem findItemForComponent(ApplicationData application, Component comp) {
		return application.getProcessingData().getAllItems().stream().filter(item -> {
			return ((item instanceof ComponentItem) && (((ComponentItem)item).getComponent()==comp));
		}).findAny().map(ComponentItem.class::cast).orElse(null);
	}

	public BuildComponentItem findItemForBuildComponent(ApplicationData application, BuildComponent buildComp) {
		return application.getProcessingData().getAllItems().stream().filter(item -> {
			return ((item instanceof BuildComponentItem) && (((BuildComponentItem)item).getBuildComponent()==buildComp));
		}).findAny().map(BuildComponentItem.class::cast).orElse(null);
	}

	// Add item to application's processing data
	public void addItem(Item item, ApplicationData application) {
		ProcessingData pd = application.getProcessingData();

		pd.getAllItems().add(item);
		if (item instanceof Processable) {
			pd.getToProcess().add((Processable)item);
		}
		else if (item instanceof BuildComponentItem) {
			pd.getBuildsToInit().add((BuildComponentItem)item);
		}

		item.setState(ProcessingState.CREATED);
	}
	
	// TODO: When a folder watcher is reset, we need to apply user files to it.
	public void resetItem(ApplicationData application, int id) {
		ProcessingData pd = application.getProcessingData();
		Item item = pd.getItem(id);
		
		if (item==null) return;

		removeItem(application, id);
		application.getApplicationLog().debug("Re-adding item "+id);
		addItem(item, application);
	}
	
	public void removeItem(ApplicationData application, int id) {
		ProcessingData pd = application.getProcessingData();
		Item item = pd.getItem(id);
		if (item==null) return;
		Set<Integer> itemsToReset = new HashSet<>();
		Set<Integer> itemsToRemove = new HashSet<>();
		
		BuildComponentItem buildItem = (item instanceof BuildComponentItem) 
				? (BuildComponentItem)item : null;
		
		application.getApplicationLog().debug("Removing item "+id+" - "+item.getName());

		// Remove this item from the application's item list
		application.getProcessingData().getAllItems().remove(item);

		// Remove from toProcess and processed with that itemId
		List<Processable> processables = application.getProcessingData().getToProcess().stream()
				.filter(proc -> {
					return proc.getItemId()==id;
				}).toList();
		application.getProcessingData().getToProcess().removeAll(processables);
		processables = application.getProcessingData().getProcessed().stream()
				.filter(proc -> {
					return proc.getItemId()==id;
				}).toList();
		application.getProcessingData().getProcessed().removeAll(processables);

		// Do not reset the item's originator
		/*
		if (item.getOriginatorId() > 0) {
			itemsToReset.add(item.getOriginatorId());
		}
		*/
		
		// For a build, remove from buildsToInit, buildsToProcess and buildsProcessed
		if (buildItem!=null) {
			application.getProcessingData().getBuildsToInit().remove(buildItem);
			application.getProcessingData().getBuildsToProcess().remove(buildItem);
			application.getProcessingData().getBuildsProcessed().remove(buildItem);
		}

		// Find source files that originate from this item
		List<SourceFile> filesToRemove = dependencyUtil.getSourceFilesFromId(id, application);
		// Delete these source files, reset items that originate the same file, delete the source file dependency data
		filesToRemove.forEach(sf -> {
			itemsToReset.addAll(application.getDependencyData().getSrcDependencies().get(sf.getPath()));
			if (!application.getSourceFiles().values().contains(sf)) {
				outputUtil.deleteSourceFile(sf, application);
			}
			application.getDependencyData().getSrcDependencies().remove(sf.getPath());
		});
		// Done removing source files
		
		// Remove items that originate from this item
		itemsToRemove.addAll(dependencyUtil.getItemsThatOriginateFrom(id, application).stream().map(Item::getItemId).collect(Collectors.toList()));

		// Find attributes that this item depends on.
		Set<String> attributeDeps = dependencyUtil.getSystemAttributeDependencies(id, application);
		attributeDeps.forEach(name -> {
			// If this item originates the attribute then we need to remove it.
			// We don't need to do this for dependency only
			if (application.getDependencyData().getAttributeOriginators().get(name)!=null) {
				if (application.getDependencyData().getAttributeOriginators().get(name).contains(id)) {
					// For originated attribute: reset its dependencies, remove it, remove its dependency data
					itemsToReset.addAll(application.getDependencyData().getAttributeDependencies().get(name));
					itemsToReset.addAll(application.getDependencyData().getAttributeOriginators().get(name));
					application.getSystemAttributes().remove(name);
					application.getDependencyData().getAttributeDependencies().remove(name);
					application.getDependencyData().getAttributeOriginators().remove(name);
				} else {
					// Remove the dependency on this attribute for this item
					int i = application.getDependencyData().getAttributeDependencies().get(name).indexOf(id);
					application.getDependencyData().getAttributeDependencies().get(name).remove(i);
				}
			} else {
				// Remove the dependency on this attribute for this item
				int i = application.getDependencyData().getAttributeDependencies().get(name).indexOf(id);
				application.getDependencyData().getAttributeDependencies().get(name).remove(i);
			}
		});
		
		// Find types that this item originates.  Mark their dependencies for reset.
		List<Pair<String,String>> typeDependencies = dependencyUtil.getTypeDependencies(id, application);
		typeDependencies.forEach(pair -> {
			String lang = pair.getLeft();
			String name = pair.getRight();
			
			// For each type: reset items that depend on it, remove the type and remove its dependency data
			itemsToReset.addAll(application.getDependencyData().getTypeDependencies().get(lang).get(name));
			application.getTypes().get(lang).remove(name);
			application.getDependencyData().getTypeDependencies().get(lang).remove(name);
		});
		
		// Find objects that this item depends on.
		List<String> objectNames = dependencyUtil.getObjectDependencies(id, application);
		objectNames.forEach(name -> {
			// For each object, reset items that depend on it.  Then remove the object and its dependency data
			itemsToReset.addAll(application.getDependencyData().getObjectDependencies().get(name));
			application.getObjects().remove(name);
			application.getDependencyData().getObjectDependencies().remove(name);
		});

		// Remove all items in itemsToRemove
		itemsToRemove.forEach(i -> {
			removeItem(application, i);
		});
		// Reset all items in itemsToReset
		itemsToReset.forEach(i -> {
			resetItem(application, i);
		});
		
	}
	
	public boolean processItem(Processable processable, ApplicationData application) throws JavascribeException {
		return processable.process();
	}
	
	public boolean initBuild(BuildComponentItem buildItem, ApplicationData application) {
		return buildItem.init();
	}
	
	public void processBuild(BuildComponentItem buildItem, ApplicationData application) {
		
	}
	
}

