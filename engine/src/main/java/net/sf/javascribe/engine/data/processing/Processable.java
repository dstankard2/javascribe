package net.sf.javascribe.engine.data.processing;

import java.util.Map;

public interface Processable extends Comparable<Processable> {

	// The item that this processable came from
	int getItemId();
	
	// The item that originated this processable
	int getOriginatorId();
	
	// Processing priority.  Lower numbers are handled first
	int getPriority();
	
	// Name of this processable
	String getName();
	
	// Run processing.  If failed, return false.  Otherwise return true
	boolean process();
	
	// Get the logger for this processable
	ProcessorLog getLog();

	Map<String,String> getConfigs();

	ProcessingState getState();
	
	void setState(ProcessingState state);
	
}
