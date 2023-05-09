package net.sf.javascribe.api.resources;

import java.util.List;

public interface ApplicationFolder extends ApplicationResource {

	List<String> getContentNames();
	
	ApplicationResource getResource(String name);
	
}

