package net.sf.javascribe.patterns.view.impl.events;

import net.sf.javascribe.api.annotation.Scannable;

@Scannable
public class OnKeyUpDomEventDirective extends AbstractElementDomEventDirective {

	@Override
	public String getAttributeName() {
		return "js-keyup";
	}

	@Override
	protected String getDomEvent() {
		return "keyup";
	}

}

