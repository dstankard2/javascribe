package net.sf.javascribe.patterns.view.impl.events;

public class OnMouseEnterDomEventDirective extends AbstractElementDomEventDirective {

	@Override
	public String getAttributeName() {
		return "js-mouseenter";
	}

	@Override
	protected String getDomEvent() {
		return "mouseenter";
	}

	/*
	@Override
	protected String getEventCode(DirectiveContext ctx) {
		return "// TODO: Implement\n";
	}
	*/

	/*
	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String elt = ctx.getElementVarName();
		CodeExecutionContext execCtx = ctx.getExecCtx();
		StringBuilder code = ctx.getCode();
		String eventCode = ctx.getTemplateAttributes().get("");
		
		String event = "mouseenter";
		StringBuilder fn = new StringBuilder();
		
		fn.append("function($event) {\n");
		fn.append("}.bind("+elt+");\n");
		super.addEventListener(code, elt, event, fn.toString());
		
	}
	*/

}
