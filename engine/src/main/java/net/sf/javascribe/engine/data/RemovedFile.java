package net.sf.javascribe.engine.data;


public class RemovedFile implements FileChange {
	private WatchedResource file;
	
	public RemovedFile(WatchedResource file) {
		this.file = file;
	}
	
	public WatchedResource getFile() {
		return file;
	}


}
