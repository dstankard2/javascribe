package net.sf.javascribe.engine.data.processing;

public abstract class ProcessableBase implements Processable {

	@Override
	public int compareTo(Processable o) {
		return getPriority() - o.getPriority();
	}

}
