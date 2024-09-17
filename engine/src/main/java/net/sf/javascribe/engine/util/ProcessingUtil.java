package net.sf.javascribe.engine.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.api.config.BuildComponent;
import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.engine.ComponentDependency;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.DependencyData;
import net.sf.javascribe.engine.data.ProcessingData;
import net.sf.javascribe.engine.data.files.DefaultBuildComponent;
import net.sf.javascribe.engine.data.files.UserFile;
import net.sf.javascribe.engine.data.processing.BuildComponentItem;
import net.sf.javascribe.engine.data.processing.ComponentItem;
import net.sf.javascribe.engine.data.processing.FolderWatcherEntry;
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

	public ComponentItem findItemForComponent(ApplicationData application, Component comp) {
		return application.getProcessingData().getAllItems().stream().filter(item -> {
			return ((item instanceof ComponentItem) && (((ComponentItem) item).getComponent() == comp));
		}).findAny().map(ComponentItem.class::cast).orElse(null);
	}

	public BuildComponentItem findItemForBuildComponent(ApplicationData application, BuildComponent buildComp) {
		return application.getProcessingData().getAllItems().stream().filter(item -> {
			return ((item instanceof BuildComponentItem)
					&& (((BuildComponentItem) item).getBuildComponent() == buildComp));
		}).findAny().map(BuildComponentItem.class::cast).orElse(null);
	}

	// TODO: Manage the items per build correctly
	// Add item to application's processing data.
	// Check if there is already an item with that id
	public void addItem(Item item, ApplicationData application) {
 		ProcessingData pd = application.getProcessingData();
		
		if (pd.getItem(item.getItemId()) != null) {
			// This item is already in there.  No need to add it
			return;
 		}

 		pd.getAllItems().add(item);
		if (item instanceof Processable) {
			pd.getToProcess().add((Processable) item);
		} else if (item instanceof BuildComponentItem) {
			BuildComponentItem bitem = (BuildComponentItem)item;
			// If this is a build in the root folder, and the root folder has a default build, 
			// remove the default build in the root folder
			if ((item.getFolder() == application.getRootFolder()) && (application.getRootFolder().getBuildComponent() != null)) {
				if (application.getRootFolder().getBuildComponent().getBuildComponent() instanceof DefaultBuildComponent) {
					// remove the build item in the root folder
					this.removeItem(application, application.getRootFolder().getBuildComponent().getItemId());
				}
			}
			// root build needs to be processed first
			// TODO: Builds need to be processed in directory hierarchical order to guarantee that init works
			if (bitem.getFolder()==application.getRootFolder()) {
				pd.getBuildsToInit().add(0, bitem);
			} else {
				pd.getBuildsToInit().add(bitem);
			}
		} else if (item instanceof FolderWatcherEntry) {
			checkFolderWatcherAgainstFiles((FolderWatcherEntry) item, application);
		}

		item.setState(ProcessingState.CREATED);
	}

	public void checkFolderWatcherAgainstFiles(FolderWatcherEntry watcher, ApplicationData application) {
		// Check user files against folder watcher.
		// If there is a JavascribeException, set item state and application state,
		// and record the JavascribeException
		String path = watcher.getPath();

		for (Entry<String, UserFile> e : application.getUserFiles().entrySet()) {
			String p = e.getKey();
			UserFile f = e.getValue();
			if (p.startsWith(path)) {
				application.getApplicationLog()
						.debug("Applying folder watcher '" + watcher.getName() + "' to file " + f.getPath());
				watcher.applyToUserFile(f);
			}
		}
		handleAddedItems(application);

	}

	/*
	// TODO: Build a list of items to re-add, add them and take duplicates into account.
	public void resetItem(ApplicationData application, int id) {
		ProcessingData pd = application.getProcessingData();
		Item item = pd.getItem(id);

		if (item == null)
			return;

		boolean reAdd = removeItem(application, id);
		if (reAdd) {
			application.getApplicationLog().debug("Re-adding item " + id);
			addItem(item, application);
		}
	}
*/
	
	// Reset the items for the given ids.
	// For each id, if we're resetting the originator, don't re-add this one
	public void resetItems(ApplicationData application, List<Integer> ids) {
		List<Item> toReadd = new ArrayList<>();
		ProcessingData pd = application.getProcessingData();

		// Remove each itemId
		ids.forEach(id -> {
			Item item = pd.getItem(id);
			if (item==null) return;
			
			toReadd.addAll(this.removeItem(application, id));
			// If we're resetting this item's originator, we don't re-add this one
			// TODO: This might not work for items that are added by an item with an originator
			Item orig = pd.getItem(item.getOriginatorId());
			if (!toReadd.contains(orig)) {
				toReadd.add(item);
			}
		});

		Set<Item> added = new HashSet<>();
		List<Integer> readdIds = toReadd.stream().map(Item::getItemId).toList();
		toReadd.forEach(item -> {
			// Add this item if we haven't already added it
			// If this item has an originator, and we're re-adding the originator, don't re-add
			boolean add = true;
			if (added.contains(item)) {
				add = false;
			}
			if (readdIds.contains(item.getOriginatorId())) {
				add = false;
			}
			if (application.getProcessingData().getAllItems().contains(item)) {
				add = false;
			}
			if (add) {
				this.addItem(item, application);
				application.getApplicationLog().debug("Readding item " + item.getItemId() + " - " + item.getName());
				added.add(item);
			}
		});
	}

	public void removeItems(ApplicationData application, List<Item> items) {
		List<Item> toReadd = new ArrayList<>();
		List<Integer> ids = items.stream().map(Item::getItemId).toList();

		// Remove each itemId
		items.forEach(item -> {
			toReadd.addAll(this.removeItem(application, item.getItemId()));
			toReadd.removeAll(items);
		});

		Set<Item> added = new HashSet<>();
		toReadd.forEach(item -> {
			// Add this item if we haven't already added it
			// If this item has an originator, and we're resetting the originator, don't re-add
			boolean add = true;
			if (added.contains(item)) {
				add = false;
			}
			if (ids.contains(item.getOriginatorId())) {
				add = false;
			}
			if (add) {
				this.addItem(item, application);
				added.add(item);
			}
		});
	}

	/**
	 * Removes the given item ID.  Returns a list of items that should be re-added
	 * Will call removeItem recursively on items that are deemed to be reset or removed.
	 * The items to be readded from above line are added to the return result.
	 */
	/*
	 * TODO: We might have to remove all applicable items in batch, to properly manage which ones should 
	 * be removed vs readded
	 */
	private List<Item> removeItem(ApplicationData application, int id) {
		ProcessingData pd = application.getProcessingData();
		DependencyData depData = application.getDependencyData();
		List<Item> toReadd = new ArrayList<>();
		
		Item item = pd.getItem(id);
		if (item == null) {
			return toReadd;
		}
		Set<Integer> itemsToReset = new HashSet<>();
		Set<Integer> itemsToRemove = new HashSet<>();
		if (item instanceof FolderWatcherEntry) {
			application.getApplicationLog().debug("Removing a folder watcher");
		}

		BuildComponentItem buildItem = (item instanceof BuildComponentItem) ? (BuildComponentItem) item : null;

		application.getApplicationLog().debug("Removing item " + id + " - " + item.getName());

		// Remove this item from the application's item list
		application.getProcessingData().getAllItems().remove(item);

		// Remove from toProcess and processed with that itemId or originatorId
		List<Processable> processables = application.getProcessingData().getToProcess().stream().filter(proc -> {
			return proc.getItemId() == id || proc.getOriginatorId() == id;
		}).toList();
		application.getProcessingData().getToProcess().removeAll(processables);
		processables = application.getProcessingData().getProcessed().stream().filter(proc -> {
			return proc.getItemId() == id || proc.getOriginatorId() == id;
		}).toList();
		application.getProcessingData().getProcessed().removeAll(processables);

		// Do not reset the item's originator
		/*
		 * if (item.getOriginatorId() > 0) { itemsToReset.add(item.getOriginatorId()); }
		 */

		// For a build, remove from buildsToInit, buildsToProcess and buildsProcessed
		// Remove availableBuildContext, remove items related to this build
		if (buildItem != null) {
			String buildId = buildItem.getBuildComponent().getId();
			pd.getBuildsToInit().remove(buildItem);
			pd.getBuildsToProcess().remove(buildItem);
			pd.getBuildsProcessed().remove(buildItem);
			pd.getAvailableBuildContexts().remove(buildId);
			List<Item> itemsPerBuild = pd.getItemsPerBuild().get(buildId);
			if (itemsPerBuild!=null) {
				itemsPerBuild.forEach(i -> {
					itemsToReset.add(i.getItemId());
				});
				pd.getItemsPerBuild().remove(buildId);
			}
		} else {
			// For the item being removed, remove it from the items for its build.
			String buildId = item.getFolder().getBuildContext().getId();
			List<Item> items = pd.getItemsPerBuild().get(buildId);
			if (items!=null) { // items will be null if build isn't initialized yet
				items.remove(item);
			}
		}

		// Find source files that originate from this item
		List<SourceFile> filesToRemove = dependencyUtil.getSourceFilesFromId(id, application);
		// Delete these source files, reset items that originate the same file, delete
		// the source file dependency data
		filesToRemove.forEach(sf -> {
			itemsToReset.addAll(application.getDependencyData().getSrcDependencies().get(sf.getPath()));
			if (application.getSourceFiles().values().contains(sf)) {
				application.getSourceFiles().remove(sf.getPath());
				outputUtil.deleteSourceFile(sf, application);
			}
			application.getDependencyData().getSrcDependencies().remove(sf.getPath());
		});
		// Done removing source files

		// Remove items that originate from this item
		itemsToRemove.addAll(dependencyUtil.getItemsThatOriginateFrom(id, application).stream().map(Item::getItemId)
				.collect(Collectors.toList()));

		// Find attributes that this item depends on.  Remove this item from attribute dependencies
		// If this item originates the attribute, we have to reset all items that originate or depend on the attribute
		Set<String> attributeDeps = dependencyUtil.getSystemAttributeDependencies(id, application);
		attributeDeps.forEach(name -> {
			// Remove this item from the attribute dependencies if it's there
			depData.getAttributeDependencies().get(name).remove(id);
			// If this item originates the attribute then we need to remove it and items
			// that originate it.
			// The attribute might not have any originators if it came from
			// systemAttributes.properties
			if (depData.getAttributeOriginators().get(name) != null) {
				if (depData.getAttributeOriginators().get(name).contains(id)) {
					// For originated attribute: reset its dependencies and originators, remove it, remove its
					// dependency data
					// If it has originators, then it has dependencies
					itemsToReset.addAll(depData.getAttributeDependencies().get(name));
					itemsToReset.addAll(depData.getAttributeOriginators().get(name));
					application.getSystemAttributes().remove(name);
					depData.getAttributeDependencies().get(name).clear();
					depData.getAttributeOriginators().get(name).clear();
				}
			}
			if (depData.getAttributeDependencies().get(name).size() == 0) {
				depData.getAttributeDependencies().remove(name);
			}
			if (depData.getAttributeOriginators().get(name) != null) {
				if (depData.getAttributeOriginators().get(name).size() == 0) {
					depData.getAttributeOriginators().remove(name);
				}
			}

		});

		// Find types that this item originates. Mark their dependencies for reset.
		List<Pair<String, String>> typeDependencies = dependencyUtil.getTypeDependencies(id, application);
		typeDependencies.forEach(pair -> {
			String lang = pair.getLeft();
			String name = pair.getRight();

			// For each type: reset items that depend on it, remove the type and remove its
			// dependency data
			itemsToReset.addAll(application.getDependencyData().getTypeDependencies().get(lang).get(name));
			application.getApplicationTypes().get(lang).remove(name);
			depData.getTypeDependencies().get(lang).remove(name);
		});

		// Find objects that this item depends on.
		List<String> objectNames = dependencyUtil.getObjectDependencies(id, application);
		objectNames.forEach(name -> {
			// For each object, reset items that depend on it. Then remove the object and
			// its dependency data
			itemsToReset.addAll(application.getDependencyData().getObjectDependencies().get(name));
			application.getObjects().remove(name);
			application.getDependencyData().getObjectDependencies().remove(name);
		});

		itemsToReset.remove(id);
		
		// Ensure that items being removed will not be reset.
		itemsToReset.removeAll(itemsToRemove);
		
		// For all the items that need to be reset, we need to remove them and then add them to ret if they are not to be removed
		// Items with originators should not be reset
		itemsToReset.forEach(idToReset -> {
			Item it = pd.getItem(idToReset);
			if (it!=null) {
				boolean originated = it.getOriginatorId() > 0;
				if ((it!=null) && (!originated)) {
					toReadd.add(it);
				}
				List<Item> itemsToReadd = this.removeItem(application, idToReset);
				itemsToReadd.forEach(i -> {
					if (!itemsToRemove.contains(i.getItemId())) {
						toReadd.add(i);
					}
				});
			}
		});

		// Any item that is to be removed should not be readded
		itemsToRemove.forEach(idToRemove -> {
			Item it = pd.getItem(idToRemove);
			if (it != null) {
				List<Item> itemsToReadd = this.removeItem(application, idToRemove);
				itemsToReadd.forEach(i -> {
					if (!itemsToRemove.contains(i.getItemId())) {
						toReadd.add(i);
					}
				});
			}
		});

		// Make sure the item being removed isn't in the list to re-add
		toReadd.remove(item);
		
		return toReadd;
	}

	// After a processable has been processed or a folder watcher has been handled,
	// Look at the application for added items and add them.
	public boolean handleAddedItems(ApplicationData application) {
		ProcessingData pd = application.getProcessingData();
		boolean ret = true;

		application.getAddedComponents().forEach(i -> {
			addItem(i, application);
		});
		application.getAddedComponents().clear();
		// pd.getToProcess().addAll(application.getAddedComponents());

		pd.getToProcess().addAll(application.getAddedFileProcessors());
		application.getAddedFileProcessors().clear();

		List<FolderWatcherEntry> watchers = new ArrayList<>();
		if (application.getAddedFolderWatchers().size() > 0) {
			watchers.addAll(application.getAddedFolderWatchers());
			application.getAddedFolderWatchers().clear();
		}

		for (FolderWatcherEntry e : watchers) {
			addItem(e, application);
		}

		return ret;
	}

	public boolean processItem(Processable processable, ApplicationData application) throws JavascribeException {
		return processable.process();
	}

	public boolean initBuild(BuildComponentItem buildItem, ApplicationData application) {
		boolean ret = buildItem.init();
		if (ret) {
			String buildId = buildItem.getBuildComponent().getId();
			application.getProcessingData().getAvailableBuildContexts().put(buildId,
					buildItem.getBuildContext());
			application.getProcessingData().getItemsPerBuild().put(buildId, new ArrayList<>());
		}
		return ret;
	}

	public boolean processBuild(BuildComponentItem buildItem, ApplicationData application) {
		return buildItem.process();
	}

}
