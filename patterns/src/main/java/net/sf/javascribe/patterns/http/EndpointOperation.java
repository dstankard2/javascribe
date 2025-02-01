package net.sf.javascribe.patterns.http;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EndpointOperation {

	//private String context = null;
	private String module = null;
	
	private String operationName = "";
	private HttpMethod method = null;
	private String path = "";
	private String requestBody = null;
	private String requestBodyFormat = null;
	private String responseBodyType = null;
	private String responseBodyFormat = null;
	private List<String> requestParameters = new ArrayList<>();
	private List<String> pathVariables = new ArrayList<>();

	public EndpointOperation(/*String context, */String module) {
		//this.context = context;
		this.module = module;
	}

}

