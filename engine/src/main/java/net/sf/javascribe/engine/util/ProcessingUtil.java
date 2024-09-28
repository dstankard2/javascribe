package net.sf.javascribe.engine.util;

import java.util.ArrayList;
import java.util.Collection;
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
					this.removeItems(application, Set.of(application.getRootFolder().getBuildComponent()));
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
	}

	// For any item, if it has a non-build originator, we should not reset it.  Just remove it and reset the originator
	// For folder watchers, we do not reset the folder watcher.  Instead, reset its originator
	public void resetItems(ApplicationData application, Set<Item> items) {
		items.remove(null); // It's possible to have a null entry in items.  See cases around stale dependency exception
		Set<Item> toAdd = new HashSet<>();
		ProcessingData pd = application.getProcessingData();
		Set<Item> toRemove = new HashSet<>();
		
		items.forEach(item -> {
			if (item.getOriginatorId()==0) {
				toAdd.add(item);
			} else {
				Item originator = pd.getItem(item.getOriginatorId());
				if (!(item instanceof BuildComponentItem)) {
					toAdd.add(originator);
					toRemove.add(originator);
				}
			}
		});
		toRemove.addAll(items);
		this.removeItems(application, toRemove);
		toAdd.forEach(item -> {
			this.addItem(item, application);
		});
	}

	public void removeItems(ApplicationData application, Set<Item> items) {
		items.remove(null); // It's possible to have a null entry in items.  See cases around stale dependency exception
		if (items.size()>0) {
			Set<Item> toReadd = removeItemsWithoutReset(application, items);
			toReadd.forEach(item -> {
				if (item!=null) {
					this.addItem(item, application);
				}
			});
		}
	}
	
	protected Set<Item> removeItemsWithoutReset(ApplicationData application, Set<Item> items) {
		// Set<Integer> ids = items.stream().map(Item::getItemId).collect(Collectors.toSet());
		Set<Item> removed = new HashSet<>();
		Set<Item> itemsToReset = new HashSet<>();
		Set<Item> othersToRemove = new HashSet<>();
		ProcessingData pd = application.getProcessingData();
		DependencyData depData = application.getDependencyData();
		
		items.forEach(item -> {
			if (item==null) return;
			BuildComponentItem build = (item instanceof BuildComponentItem) ? (BuildComponentItem)item : null;
			FolderWatcherEntry watcher = (item instanceof FolderWatcherEntry) ? (FolderWatcherEntry)item : null;
			int id = item.getItemId();

			if (application.getProcessingData().getItem(id)==null) {
				// This item has already been removed
				return;
			}
			removed.add(item);

			// Remove the item now
			application.getApplicationLog().debug("Removing item " + id + " - " + item.getName());

			// Remove this item from the application's item list
			application.getProcessingData().getAllItems().remove(item);

			// Remove from toProcess and processed with that itemId or originatorId
			Set<Processable> processables = application.getProcessingData().getToProcess().stream().filter(proc -> {
				return isOfId(proc, id);
			}).collect(Collectors.toSet());
			application.getProcessingData().getToProcess().removeAll(processables);
			processables = application.getProcessingData().getProcessed().stream().filter(proc -> {
				return isOfId(proc, id);
			}).collect(Collectors.toSet());
			application.getProcessingData().getProcessed().removeAll(processables);

			// For a build, remove from buildsToInit, buildsToProcess and buildsProcessed
			// Remove availableBuildContext, remove/reAdd items related to this build
			if (build != null) {
				String buildId = build.getBuildComponent().getId();
				pd.getBuildsToInit().remove(build);
				pd.getBuildsToProcess().remove(build);
				pd.getBuildsProcessed().remove(build);
				pd.getAvailableBuildContexts().remove(buildId);
				if (build.getFolder().getBuildComponent() == build) {
					build.getFolder().setBuildComponent(null);
				}

				List<Item> itemsPerBuild = pd.getItemsPerBuild().get(buildId);

				if (itemsPerBuild!=null) {
					itemsPerBuild.forEach(i -> {
						itemsToReset.add(i);
					});
					pd.getItemsPerBuild().remove(buildId);
				}
			} else {
				// For the item being removed, remove it from the items for its build.
				String buildId = item.getFolder().getBuildContext().getId();
				List<Item> itemsForBuild = pd.getItemsPerBuild().get(buildId);
				if (itemsForBuild!=null) { // items will be null if build isn't initialized yet
					itemsForBuild.remove(item);
				}
			}
			
			// When removing a folder watcher, reset its originator
			if (watcher != null) {
				int i = watcher.getOriginatorId();
				itemsToReset.add(pd.getItem(i));
			}

			// Find source files that originate from this item
			List<SourceFile> filesToRemove = dependencyUtil.getSourceFilesFromId(id, application);
			// Delete these source files, reset items that originate the same file, delete
			// the source file dependency data
			filesToRemove.forEach(sf -> {
				String path = sf.getPath();
				itemsToReset.addAll(getItems(application, application.getDependencyData().getSrcDependencies().get(path)));
				if (application.getSourceFiles().get(path)!=null) {
					application.getSourceFiles().remove(path);
					outputUtil.deleteSourceFile(sf, application);
				}
				application.getAddedSourceFiles().remove(path);
				application.getDependencyData().getSrcDependencies().remove(sf.getPath());
			});
			// Done removing source files

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
				if (depData.getAttributeDependencies().get(name).size()==0) {
					depData.getAttributeDependencies().remove(name);
				}
				if (depData.originatesAttribute(name, id)) {
					itemsToReset.addAll(getItems(application, depData.getAttributeOriginators().get(name)));
					itemsToReset.addAll(getItems(application, depData.getAttributeDependencies().get(name)));
					application.getSystemAttributes().remove(name);
					depData.getAttributeDependencies().remove(name);
					depData.getAttributeOriginators().remove(name);
				}

			});
			// End of handling of system attributes

			// Remove items that originate from this item
			Set<Item> toRemove = dependencyUtil.getItemsThatOriginateFrom(id, application);
			othersToRemove.addAll(toRemove);
			
			// Find types that this item originates. Mark their dependencies for reset.  Remove the type and its dependency data
			List<Pair<String, String>> typeDependencies = dependencyUtil.getTypeDependencies(id, application);
			typeDependencies.forEach(pair -> {
				String lang = pair.getLeft();
				String name = pair.getRight();

				// For each type: reset items that depend on it, remove the type and remove its
				// dependency data
				itemsToReset.addAll(getItems(application, application.getDependencyData().getTypeDependencies().get(lang).get(name)));
				application.getDependencyData().getTypeDependencies().get(lang).remove(name);
				application.getApplicationTypes().get(lang).remove(name);
			});

			// Find objects that this item depends on.
			List<String> objectNames = dependencyUtil.getObjectDependencies(id, application);
			objectNames.forEach(name -> {
				// For each object, reset items that depend on it. Then remove the object and
				// its dependency data
				itemsToReset.addAll(getItems(application, application.getDependencyData().getObjectDependencies().get(name)));
				application.getObjects().remove(name);
				application.getAddedObjects().remove(name);
				application.getDependencyData().getObjectDependencies().remove(name);
			});

			// Find items that originate from this item.  They will be removed.
			Set<Item> derivedItems = pd.getItemsThatOriginateFrom(id);
			othersToRemove.addAll(derivedItems);
		});

		// Items to re-add after we finish removing appropriate ones.
		Set<Item> toReadd = new HashSet<>();
		toReadd.removeAll(removed);
		toReadd.removeAll(othersToRemove);
		itemsToReset.removeAll(removed);
		itemsToReset.removeAll(othersToRemove);

		// Remove others to be removed, along with items to reset
		othersToRemove.addAll(itemsToReset);

		// Do not reset items that have originators.
		// Instead, remove the item and reset the originator.
		// TODO: We cannot reset a build because it will need to be initialized.  We also may just not want to reset it and reset the child instead.
		// We may want to introduce a stale build exception that triggers re-initialization of builds, or perhaps in some cases.
		// For now, try resetting the child and not the build
		Set<Item> removal = new HashSet<>();
		Set<Item> addition = new HashSet<>();
		itemsToReset.forEach(item -> {
			if (item==null) return;
			if (item.getOriginatorId()>0) {
				Item originator = pd.getItem(item.getOriginatorId());
				if (originator instanceof BuildComponentItem) {
					// Reset the item, leave the build alone
				} else {
					// remove the item and reset the originator.
					removal.add(item);
					addition.add(originator);
				}
			}
		});
		itemsToReset.removeAll(removal);
		itemsToReset.addAll(addition);
		
		if (othersToRemove.size()>0) {
			itemsToReset.addAll(this.removeItemsWithoutReset(application, othersToRemove));
		}
		toReadd.addAll(itemsToReset);
		toReadd.removeAll(removed);
		
		// toReadd.removeAll(othersToRemove);
		
		return toReadd;
	}

	private Set<Item> getItems(ApplicationData application, Collection<Integer> itemIds) {
		Set<Item> ret = new HashSet<>();
		
		if (itemIds!=null) {
			itemIds.forEach(itemId -> {
				Item i = application.getProcessingData().getItem(itemId);
				if (i!=null) {
					ret.add(i);
				}
			});
		}
		
		return ret;
	}

	private boolean isOfId(Processable proc, int id) {
		return (proc.getItemId() == id) || ((proc.getItemId() == 0) && (proc.getOriginatorId() == id));
	}
	
	// After a round of processing, look at the application for added items and add them.  Then clear added items.
	public void handleAddedItems(ApplicationData application) {
		ProcessingData pd = application.getProcessingData();

		// Added source files will be taken care of when they are written, in the workspaceManager.

		// Objects
		application.getAddedObjects().entrySet().forEach(e -> {
			application.getObjects().put(e.getKey(), e.getValue());
		});
		application.getAddedObjects().clear();
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
