package net.sf.javascribe.engine;

import lombok.Getter;

/**
 * If application data is being modified, and it wasn't added in the current run, 
 * the items that affected it might have to be reset.  This exception should be thrown
 * if that's the case.  This exception is for internal engine use only.
 * The item modifying the stale data should be marked in dependencies, and then this 
 * exception thrown with the ID.  The ID will be reset.
 * @author dstan
 */
@SuppressWarnings("serial")
@Getter
public class StaleDependencyException extends EngineException {
	private int itemId = 0;

	public StaleDependencyException(int itemId) {
		this.itemId = itemId;
	}

}
