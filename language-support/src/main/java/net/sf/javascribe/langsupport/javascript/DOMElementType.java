package net.sf.javascribe.langsupport.javascript;

public class DOMElementType extends JavascriptBaseObjectType {

	public String getName() {
		return "DOMElement";
	}
	
	public DOMElementType() {
		addAttribute("style", "DOMStyle");
		addAttribute("classList", "DOMClassList");
		addAttribute("min","integer");
		addAttribute("max","integer");

		addAttribute("className","string");
		addAttribute("contentEditable","boolean");
		addAttribute("dir","string");
		addAttribute("id","string");
		addAttribute("innerHTML","string");
		addAttribute("nodeName","string");
		addAttribute("offsetHeight","integer");
		addAttribute("offsetLeft","integer");
		addAttribute("offsetTop","integer");
		addAttribute("offsetWidth","integer");
		addAttribute("parentNode","DOMElement");
		addAttribute("tabIndex","integer");
		addAttribute("tagName","string");
		addAttribute("value","object");
		
	}

}

