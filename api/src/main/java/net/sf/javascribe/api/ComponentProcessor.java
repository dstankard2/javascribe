package net.sf.javascribe.api;

import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.api.exception.JasperException;

public interface ComponentProcessor {

	/**
	 * First method to be called in the component lifecycle.  Can be used to initialize state.
	 * @param component Component to process
	 * @param ctx ProcessorContext for accessing engine resources.
	 */
	void init(Component component,ProcessorContext ctx);

	/**
	 * Processes the component using engine-level capabilities.
	 * @throws JasperException If there is a problem.
	 */
	void process() throws JasperException;

}
