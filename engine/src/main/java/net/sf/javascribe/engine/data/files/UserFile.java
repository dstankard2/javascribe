package net.sf.javascribe.engine.data.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.sf.javascribe.api.resources.ApplicationFile;

/**
 * Implements watched resource because it is watched by the engine.
 * Implements ApplicationFile for client API.
 * @author DCS
 */
public class UserFile implements WatchedResource,ApplicationFile {
	private File file = null;
	private ApplicationFolderImpl folder = null;
	private long lastModified = 0;
	private String path;

	public UserFile(File file,ApplicationFolderImpl folder) {
		this.file = file;
		this.folder = folder;
		this.lastModified = file.lastModified();
		this.path = folder.getPath()+getName();
	}

	@Override
	public ApplicationFolderImpl getFolder() {
		return folder;
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public long getLastModified() {
		return lastModified;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		FileInputStream fin = new FileInputStream(file);
		return fin;
	}

}

