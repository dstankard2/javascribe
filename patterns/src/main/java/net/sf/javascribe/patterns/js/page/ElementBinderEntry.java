package net.sf.javascribe.patterns.js.page;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ElementBinderEntry {
	private Class<?> cl = null;
	private Method bindToPage = null;
	private Map<String,Method> bindings = new HashMap<String,Method>();

	public ElementBinderEntry(Class<?> cl) {
		this.cl = cl;
	}
	
	public Class<?> getCl() {
		return cl;
	}
	public void setCl(Class<?> cl) {
		this.cl = cl;
	}

	public Map<String, Method> getBindings() {
		return bindings;
	}

	public void setBindings(Map<String, Method> bindings) {
		this.bindings = bindings;
	}

	public Method getBindToPage() {
		return bindToPage;
	}

	public void setBindToPage(Method bindToPage) {
		this.bindToPage = bindToPage;
	}
}
