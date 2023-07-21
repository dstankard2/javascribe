package net.sf.javascribe.api;

import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.resources.ApplicationResource;

/**
 * The compile-time context of a component in Jasper.
 * Can be used to add dependencies and determine where files will be written to.
 * @author DCS
 *
 */
public interface BuildContext {

	void addDependency(String name);
	
	void addDependency(BuildContext buildCtx);

	String getOutputRootPath(String fileExt) throws JavascribeException;

	String getOutputRootPath();

	String getApplicationFolderPath();

	ApplicationResource getApplicationResource(String path);

	RuntimePlatform getRuntimePlatform();

	void setRuntimePlatform(RuntimePlatform platform) throws JavascribeException;

	String getName();
	
	void addBuildCommand(String cmd);

	String getId();

}

