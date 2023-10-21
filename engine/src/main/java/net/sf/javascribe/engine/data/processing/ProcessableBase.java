package net.sf.javascribe.engine.data.processing;

public abstract class ProcessableBase implements Processable {

	@Override
	public int compareTo(Processable o) {
		if (getPriority() > o.getPriority()) return 1;
		else if (getPriority() < o.getPriority()) return -1;
		return 0;
	}

}
