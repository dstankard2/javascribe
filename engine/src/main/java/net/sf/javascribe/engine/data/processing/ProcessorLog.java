package net.sf.javascribe.engine.data.processing;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.logging.Log;
import net.sf.javascribe.api.logging.ProcessorLogLevel;
import net.sf.javascribe.api.plugin.ProcessorLogMessage;

public class ProcessorLog implements Log {

	protected List<ProcessorLogMessage> messages = new ArrayList<>();
	private String name = null;

	public ProcessorLog(String name) {
		this.name = name;
	}

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

	@Override
	public boolean isVerbose() {
		return false;
	}

	@Override
	public void info(String message) {
		this.messages.add(new ProcessorLogMessage(ProcessorLogLevel.INFO, message, null));
	}

	@Override
	public void debug(String message) {
		this.messages.add(new ProcessorLogMessage(ProcessorLogLevel.DEBUG, message, null));
	}

	@Override
	public void warn(String message) {
		this.messages.add(new ProcessorLogMessage(ProcessorLogLevel.WARN, message, null));
	}

	@Override
	public void error(String message) {
		this.messages.add(new ProcessorLogMessage(ProcessorLogLevel.ERROR, message, null));
	}

	@Override
	public void warn(String message, Throwable e) {
		this.messages.add(new ProcessorLogMessage(ProcessorLogLevel.WARN, message, e));
	}

	@Override
	public void error(String message, Throwable e) {
		this.messages.add(new ProcessorLogMessage(ProcessorLogLevel.ERROR, message, e));
	}

}

