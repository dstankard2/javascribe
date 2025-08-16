package net.sf.javascribe.api.plugin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.sf.javascribe.api.logging.ProcessorLogLevel;

@AllArgsConstructor
@Getter
@Setter
public class ProcessorLogMessage {

	private String logName = null;
	private ProcessorLogLevel level = null;
	private ProcessorLogLevel targetLevel = null;
	private String message = null;
	private Throwable e = null;
	
	public ProcessorLogMessage() {
	}

	/*
	public ProcessorLogMessage(String logName, ProcessorLogLevel level, String message, Throwable e) {
		super();
		this.logName = logName;
		this.level = level;
		this.message = message;
		this.e = e;
	}
	*/

}

