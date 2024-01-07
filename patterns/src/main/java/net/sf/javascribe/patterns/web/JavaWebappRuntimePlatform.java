package net.sf.javascribe.patterns.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.Setter;

public abstract class JavaWebappRuntimePlatform {
	
	@Getter
	@Setter
	protected String contextRoot = "";

	protected Map<String,String> servlets = new HashMap<>();
	protected Map<String,String> mappings = new HashMap<>();
	
	@Getter
	protected List<String> contextListeners = new ArrayList<>();

	@Getter
	protected Map<String,String> filters = new HashMap<>();
	
	@Getter
	protected Map<String,List<String>> filterMappings = new HashMap<>();
	
	@Getter
	protected Map<String,String> websocketEndpoints = new HashMap<>();

	@Getter
	protected List<String> dependencies = new ArrayList<>();

	public void addDependency(String name) {
		dependencies.add(name);
	}
	public List<String> getServletNames() {
		List<String> ret = new ArrayList<>();
		
		for(Entry<String,String> entry : servlets.entrySet()) {
			if (!ret.contains(entry.getKey())) {
				ret.add(entry.getKey());
			}
		}
		
		return ret;
	}
	
	public String getServletClass(String name) {
		return servlets.get(name);
	}
	
	public List<String> getMappings(String servletName) {
		List<String> ret = new ArrayList<>();
		
		for(Entry<String,String> entry : mappings.entrySet()) {
			if (entry.getValue().equals(servletName)) {
				ret.add(entry.getKey());
			}
		}
		
		return ret;
	}
	
	public void addServlet(String servletName, String servletClass) {
		servlets.put(servletName, servletClass);
	}

	public void addServletMapping(String uri, String servletName) {
		mappings.put(uri, servletName);
	}

	public void addServletContextListener(String className) {
		contextListeners.add(className);
	}

	public void addFilter(String filterName,String className) {
		filters.put(filterName, className);
	}
	
	public List<String> getFiltersForUri(String uri) {
		return filterMappings.get(uri);
	}
	
	public void addFilterMapping(String uri,String filter) {
		List<String> filters = filterMappings.get(uri);
		if (filters==null) {
			filters = new ArrayList<>();
			filterMappings.put(uri, filters);
		}
		filters.add(filter);
	}

	public void addWebsocketEndpoint(String url,String className) {
		websocketEndpoints.put(url, className);
	}

}

