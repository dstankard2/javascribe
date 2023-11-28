package net.sf.javascribe.engine.data.files;

import net.sf.javascribe.api.BuildContext;
import net.sf.javascribe.api.BuildProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.logging.Log;
import net.sf.javascribe.api.resources.ApplicationResource;

/**
 * A build context that will be used when there is no build component.
 * It is always in the root folder
 */
public class DefaultBuildContext implements BuildContext {

	private Log log = null;
	private BuildProcessorContext ctx = null;
	
	public DefaultBuildContext(BuildProcessorContext ctx) {
		this.log = ctx.getLog();
		this.ctx = ctx;
	}

	@Override
	public String getId() {
		return "root";
	}
	
	@Override
	public void addDependency(String name) {
		log.info("Dependency '"+name+"' is added to the build context");
	}

	@Override
	public void addDependency(BuildContext buildCtx) {
		
	}

	@Override
	public String getOutputRootPath(String fileExt) throws JavascribeException {
		String cfgName = "outputPath."+fileExt;
		String value = this.ctx.getProperty(cfgName);
		if (value==null) {
			//this.log.error("Default Build Context requires configuration property '"+cfgName+"' to determine output path for file extension '"+fileExt+"'");
			throw new JavascribeException("Default Build Context requires configuration property '"+cfgName+"' to determine output path for file extension '"+fileExt+"'");
		}
		return value;
	}

	@Override
	public String getOutputRootPath() {
		return null;
	}

	@Override
	public String getApplicationFolderPath() {
		return ctx.getFolder().getPath();
	}

	@Override
	public ApplicationResource getApplicationResource(String path) {
		return ctx.getFolder().getResource(path);
	}

	@Override
	public String getName() {
		return "Default";
	}

	@Override
	public void addBuildCommand(String cmd) {
		log.error("Default build context cannot add build command '"+cmd+"'");
	}

	
	
}
