package net.sf.javascribe.patterns.view.impl.events;

import net.sf.javascribe.api.annotation.Scannable;

@Scannable
public class OnBlurDomEventDirective extends AbstractElementDomEventDirective {

	@Override
	public String getAttributeName() {
		return "js-onblur";
	}

	@Override
	protected String getDomEvent() {
		return "blur";
	}

}
