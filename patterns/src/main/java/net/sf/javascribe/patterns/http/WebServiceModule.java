package net.sf.javascribe.patterns.http;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a web service API that is available to a HTTP client at a particular path.
 * The contextRoot is the root path on the server host to the web service endpoints 
 * contained in this context.
 * 
 * For instance if the contextRoot is "/app1" then all web services available in this context 
 * will begin with path "protocol://<host>/app1" when invoked by a HTTP client.
 * @author DCS
 *
 */
@Getter
@Setter
public class WebServiceModule {

	private String moduleName = "";
	
	private String moduleUri = "";

	private List<EndpointOperation> operations = new ArrayList<>();

	//private List<WebServiceDefinition> webServices = new ArrayList<>();

	public WebServiceModule() {
	}

	/*
	public Map<String,EndpointOperation> getModuleOperations() {
		Map<String,EndpointOperation> ret = new HashMap<>();
		
		operations.forEach(op -> {
			ret.put(contextRoot, null)
		});
		
		return ret;
	}
	*/
	
	/*
	public WebServiceDefinition getWebServiceDefinition(String name) {
		for(WebServiceDefinition def : webServices) {
			if (def.getName().equals(name)) {
				return def;
			}
		}
		return null;
	}
*/
	
}
