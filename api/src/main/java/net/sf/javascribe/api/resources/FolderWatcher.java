package net.sf.javascribe.api.resources;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;

/**
 * <p>A folder watcher will be notified when any file in the folder (or a subfolder of the 
 * folder) is added or changed.  Folder watchers are added by component processors.  A 
 * component processor that adds a folder watcher should execute with a low priority value 
 * so that its folder watchers can add processing for applicable files at a priority level 
 * that makes sense for the file being processed.</p>
 * <p>When a folder watcher is added by a component processor, all files in the folder and 
 * subfolders are evaluated by this interface.</p>
 * @author dstan
 */
public interface FolderWatcher {

	/**
	 * Handle 
	 * @param ctx Context of processing.
	 * @param changedFile The file that has changed
	 * @throws JavascribeException If there is a problem.
	 */
	void process(ProcessorContext ctx,ApplicationFile changedFile) throws JavascribeException;

	/**
	 * @return Name of this folder watcher, which will be put in log messages.
	 */
	String getName();

}

