package net.sf.javascribe.engine.data.changes;

import net.sf.javascribe.api.resources.ApplicationResource;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;

/**
 * Represents an application folder/file that is being watched by the engine.
 * @author DCS
 *
 */
public interface WatchedResource extends ApplicationResource {

	/**
	 * Date that this file was last modified.  Used to determine if 
	 * the components dependent on this file should be reloaded.
	 * @return
	 */
	public long getLastModified();
	
	public ApplicationFolderImpl getFolder();

}

