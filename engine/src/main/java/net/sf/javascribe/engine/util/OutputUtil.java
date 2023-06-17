package net.sf.javascribe.engine.util;

import java.io.File;

import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.engine.data.ApplicationData;

public class OutputUtil {

	public void deleteSourceFile(SourceFile sf, ApplicationData application) {
		File rootDir = application.getOutputDirectory();
		String filePath = sf.getPath();
		File toDelete = new File(rootDir, filePath);
		
		if (toDelete.exists()) {
			application.getSourceFiles().remove(filePath);
			//application.getAddedSourceFiles().remove(filePath);
			toDelete.delete();
		} else {
			System.out.println("I tried to delete file "+filePath+" but I couldn't find it on the file system");
		}
	}

}
