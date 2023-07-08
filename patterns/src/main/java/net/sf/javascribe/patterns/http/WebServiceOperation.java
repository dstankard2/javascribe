package net.sf.javascribe.patterns.http;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebServiceOperation {

	private String operationName = null;
	private HttpMethod method = null;
	private String path = null;
	private String requestBody = null;
	private String requestBodyFormat = null;
	private String responseBody = null;
	private String responseBodyFormat = null;
	private List<String> requestParameters = new ArrayList<>();

	public WebServiceOperation() {
	}

	// The path string has already been validated
	public List<String> getPathVariables() {
		List<String> ret = new ArrayList<>();
		String val = this.path;
		
		int i = val.indexOf('{');
		while(i>=0) {
			int end = val.indexOf('}',i+1);
			ret.add(val.substring(i+1, end));
			val = val.substring(0, i) + val.substring(end+1);
			i = val.indexOf('{');
		}
		return ret;
	}
	
}
