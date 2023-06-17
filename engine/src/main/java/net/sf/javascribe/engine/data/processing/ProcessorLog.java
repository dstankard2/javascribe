package net.sf.javascribe.engine.data.processing;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.logging.Log;
import net.sf.javascribe.api.logging.ProcessorLogLevel;
import net.sf.javascribe.api.plugin.ProcessorLogMessage;
import net.sf.javascribe.engine.data.ApplicationData;

public class ProcessorLog implements Log {

	protected List<ProcessorLogMessage> messages = new ArrayList<>();
	private String name = null;
	private ApplicationData application;
	private ProcessorLogLevel targetLevel;

	public ProcessorLog(String name, ApplicationData application, ProcessorLogLevel targetLevel) {
		this.name = name;
		this.application = application;
		this.targetLevel = targetLevel;
	}

	/*
	public void outputToSystem() {
		List<ProcessorLogMessage> msgs = getMessages(true);
		for(ProcessorLogMessage msg : msgs) {
			String b = String.format("[%s] %s - %s", name, msg.getLevel().name(), msg.getMessage());
			PrintStream p = null;
			if (msg.getLevel()==ProcessorLogLevel.ERROR) {
				p = System.err;
			} else {
				p = System.out;
			}
			p.println(b);
			if (msg.getThrowable()!=null) {
				// TODO: This is only necessary when debugging the engine
				//msg.getThrowable().printStackTrace(p);
			}
		}
	}
	*/

	/*
	public List<ProcessorLogMessage> getMessages(boolean clear) {
		List<ProcessorLogMessage> ret = null;
		
		if (clear) {
			ret = new ArrayList<>();
			ret.addAll(messages);
			messages.clear();
		} else {
			ret = messages;
		}
		
		return ret;
	}
	*/

	@Override
	public boolean isVerbose() {
		return false;
	}

	@Override
	public void info(String message) {
		application.getMessages().add(new ProcessorLogMessage(name, ProcessorLogLevel.INFO, targetLevel, message, null));
	}

	@Override
	public void debug(String message) {
		application.getMessages().add(new ProcessorLogMessage(name, ProcessorLogLevel.DEBUG, targetLevel, message, null));
	}

	@Override
	public void warn(String message) {
		application.getMessages().add(new ProcessorLogMessage(name, ProcessorLogLevel.WARN, targetLevel, message, null));
	}

	@Override
	public void error(String message) {
		application.getMessages().add(new ProcessorLogMessage(name, ProcessorLogLevel.ERROR, targetLevel, message, null));
	}

	@Override
	public void warn(String message, Throwable e) {
		application.getMessages().add(new ProcessorLogMessage(name, ProcessorLogLevel.WARN, targetLevel, message, e));
	}

	@Override
	public void error(String message, Throwable e) {
		application.getMessages().add(new ProcessorLogMessage(name, ProcessorLogLevel.ERROR, targetLevel, message, e));
	}

}

