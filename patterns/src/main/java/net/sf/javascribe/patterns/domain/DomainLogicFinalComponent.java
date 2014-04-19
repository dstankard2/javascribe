package net.sf.javascribe.patterns.domain;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.patterns.CorePatternConstants;

@Scannable
public class DomainLogicFinalComponent extends ComponentBase {

	@Override
	public int getPriority() {
		return CorePatternConstants.PRIORITY_DOMAIN_LOGIC_FINALIZER;
	}

}

