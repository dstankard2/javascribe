package net.sf.javascribe.engine.data;


public class AddedFile implements FileChange {

	private WatchedResource file;
	
	public AddedFile(WatchedResource file) {
		this.file = file;
	}
	
	public WatchedResource getFile() {
		return file;
	}

}
