package net.sf.javascribe.langsupport.javascript;

/**
 * Represents a DOM Event.
 * @author Dave
 *
 */
public class DOMEventType extends JavascriptBaseObjectType {
	public String getName() {
		return "DOMEvent";
	}
	public DOMEventType() {
		super();
		
		super.addAttribute("bubbles", "boolean");
		super.addAttribute("cancelable", "boolean");
		super.addAttribute("currentTarget", "DOMElement");
		super.addAttribute("defaultPrevented","boolean");
		super.addAttribute("eventPhase","number");
		super.addAttribute("isTrusted","boolean");
		super.addAttribute("target", "DOMElement");
		super.addAttribute("timestamp","number");
		super.addAttribute("type","string");
		// Window type required?
		super.addAttribute("view","DOMElement");

		JavascriptFunctionType fn = null;
		
	}

}

