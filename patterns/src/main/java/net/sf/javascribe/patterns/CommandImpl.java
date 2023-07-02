package net.sf.javascribe.patterns;

import net.sf.javascribe.api.Command;

/**
 * Represents a build command that a build component processor can send to Javascribe to run.
 * @author dstan
 */
public class CommandImpl implements Command {
	String commandString = null;
	boolean asynch = false;

	public CommandImpl(String commandString,boolean asynch) {
		this.commandString = commandString;
		this.asynch = asynch;
	}

	@Override
	public String getCommandString() {
		return commandString;
	}

	@Override
	public boolean asynch() {
		return asynch;
	}

}
