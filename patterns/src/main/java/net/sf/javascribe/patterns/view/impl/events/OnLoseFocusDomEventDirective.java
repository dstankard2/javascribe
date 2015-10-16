package net.sf.javascribe.patterns.view.impl.events;

import net.sf.javascribe.api.annotation.Scannable;

@Scannable
public class OnLoseFocusDomEventDirective extends AbstractElementDomEventDirective {

	@Override
	public String getAttributeName() {
		return "js-onlosefocus";
	}

	@Override
	protected String getDomEvent() {
		return "losefocus";
	}

}
