package net.sf.javascribe.engine.data.processing;

import java.util.List;

import net.sf.javascribe.api.plugin.ProcessorLogMessage;

// Contains messages that a logger can append to
public interface LogContext {

	void appendMessage(ProcessorLogMessage message);

	List<ProcessorLogMessage> getMessages();
	
}

