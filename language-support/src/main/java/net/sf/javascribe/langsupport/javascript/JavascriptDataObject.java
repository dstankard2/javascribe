package net.sf.javascribe.langsupport.javascript;

import net.sf.javascribe.api.AttributeHolder;

public interface JavascriptDataObject extends JavascriptVariableType,AttributeHolder {

	public void addAttribute(String name,String type);

}
