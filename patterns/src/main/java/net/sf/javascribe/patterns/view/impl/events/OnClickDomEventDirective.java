package net.sf.javascribe.patterns.view.impl.events;

import net.sf.javascribe.api.annotation.Scannable;

@Scannable
public class OnClickDomEventDirective extends AbstractElementDomEventDirective {

	@Override
	public String getAttributeName() {
		return "js-onclick";
	}

	@Override
	protected String getDomEvent() {
		return "onclick";
	}

}
