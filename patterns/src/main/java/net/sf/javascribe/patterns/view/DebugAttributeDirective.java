package net.sf.javascribe.patterns.view;

import net.sf.javascribe.api.JavascribeException;

public class DebugAttributeDirective extends AttributeDirectiveBase {

	@Override
	public int getPriority() {
		return 0;
	}
	
	@Override
	public String getAttributeName() {
		// TODO Auto-generated method stub
		return "js-debug";
	}

	public static final String DEBUG_PROP = "net.sf.javascribe.patterns.view.debug";
	
	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String prop = ctx.getProcessorContext().getProperty(DEBUG_PROP);
		if ((prop!=null) && (prop.trim().length()>0)) {
			ctx.continueRenderElement();
		}
	}

}
