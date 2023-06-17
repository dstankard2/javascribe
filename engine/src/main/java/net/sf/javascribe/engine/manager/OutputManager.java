package net.sf.javascribe.engine.manager;

import java.io.File;
import java.util.List;

import net.sf.javascribe.engine.EngineInitException;
import net.sf.javascribe.engine.data.ApplicationData;

public class OutputManager {

	public void initOutputDirectory(String outputDir, List<ApplicationData> applications, boolean singleApp) throws EngineInitException {
		File f = new File(outputDir);

		if (f.exists()) {
			clearDirectory(f);
		}
		if (!f.exists()) {
			f.mkdirs();
		} else if (!f.isDirectory()) {
			throw new EngineInitException("Output Directory "+outputDir+" exists but is not a directory");
		}
		
		if (singleApp) {
			clearDirectory(f);
			f.mkdirs();
			// The outputDir is outputDir for the first application, and the list has one application
			applications.get(0).setOutputDirectory(f);
		}
	}

	private void clearDirectory(File dir) {
		if (dir.listFiles() != null) {
			for(File f : dir.listFiles()) {
				if (f.isDirectory()) {
					clearDirectory(f);
				} else {
					f.delete();
				}
			}
		}
		dir.delete();
	}
}
