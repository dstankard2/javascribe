package net.sf.javascribe.engine.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.files.UserFile;

public class OutputService {

	private void checkDirectoryForDelete(File rootDirectory, File dir) {
		if (dir==rootDirectory) return;
		for(File file : dir.listFiles()) {
			if (file.isDirectory()) {
				checkDirectoryForDelete(rootDirectory, file);
			}
		}
		if ((dir.listFiles()==null) || (dir.listFiles().length==0)) {
			dir.delete();
			// We won't delete the empty parent directory yet, 
			// because I don't want to accidentally delete my whole hard drive
		}
	}
	public void deleteRemovedUserFiles(List<UserFile> removedUserFiles, ApplicationData application) {
		File dir = application.getOutputDirectory();
		
		removedUserFiles.forEach(uf -> {
			File f = new File(dir, uf.getPath());
			if (f.exists()) {
				f.delete();
				checkDirectoryForDelete(dir, f.getParentFile());
			}
			application.getUserFiles().remove(uf.getPath());
		});
		
		/* TODO: Some cleanup.  Remove these here or in ProcessingUtil.removeItem?
		application.getRemovedSourceFiles().forEach(sf -> {
			File f = new File(dir, sf.getPath());
			if (f.exists()) {
				f.delete();
			}
			application.getSourceFiles().remove(sf.getPath());
		});
		application.getRemovedSourceFiles().clear();
		*/
	}

	public void writeUserFiles(ApplicationData application, List<UserFile> addedUserFiles) {
		File dir = application.getOutputDirectory();
		addedUserFiles.forEach(uf -> {
			File f = new File(dir, uf.getPath());
			f.getParentFile().mkdirs();
			try (InputStream in = uf.getInputStream(); FileOutputStream out = new FileOutputStream(f)) {
				int i = 0;
				while((i = in.read()) >= 0) {
					out.write(i);
				}
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			} catch(IOException e) {
				System.out.println("Couldn't write user file "+uf.getPath()+" - IOException ("+e.getMessage()+")");
			}
			application.getUserFiles().put(uf.getPath(), uf);
		});
	}

	public void writeSourceFiles(ApplicationData application) {
		Map<String,SourceFile> files = application.getAddedSourceFiles();
		File dir = application.getOutputDirectory();
		files.forEach((path,sf) -> {
			File f = new File(dir, sf.getPath());
			f.getParentFile().mkdirs();
			String content = sf.getSource().toString();
			try (FileWriter writer = new FileWriter(f)) {
				writer.write(content.toString());
				writer.flush();
			} catch(Exception e) {
				// TODO: Logging
				//appLog.error("Couldn't write file "+src.getPath(), e);
			}
			
			application.getSourceFiles().put(path, sf);
		});
	}
}
