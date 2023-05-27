package net.sf.javascribe.engine.data.processing;

import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.plugin.ProcessorLogMessage;

public interface Processable extends Comparable<Processable> {

	// The item that this processable came from
	int getItemId();
	
	// Processing priority.  Lower numbers are handled first
	int getPriority();
	
	// Log messages for this processable
	List<ProcessorLogMessage> getMessages();

	// Name of this processable
	String getName();
	
	// Run processing.  If failed, return false.  Otherwise return true
	boolean process();
	
	// Clear existing log messages
	void clearLogMessages();
	
	// Get the logger for this processable
	ProcessorLog getLog();

	Map<String,String> getConfigs();

	ProcessingState getState();
	
}
