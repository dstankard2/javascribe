package net.sf.javascribe.patterns.http;

import net.sf.javascribe.api.ProcessorContext;

public class WebUtils {

	private static final String WEBSERVICE_DEFINITIONS = "PATTERNS_HTTP_ENDPOINTS";

	public static WebServiceModule getWebServiceDefinition(String buildId, String module,ProcessorContext ctx,boolean force) {
		WebServiceModule ret = null;

		String objectName = WEBSERVICE_DEFINITIONS+"_" + buildId+"_"+module;
		ret = (WebServiceModule)ctx.getObject(objectName);
		if ((ret==null) && (force)) {
			ret = new WebServiceModule();
			ctx.setObject(objectName, ret);
		}

		return ret;
	}

}

