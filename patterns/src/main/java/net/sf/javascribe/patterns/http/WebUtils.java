package net.sf.javascribe.patterns.http;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.patterns.java.http.JavaWebUtils;

public class WebUtils {

	private static final String WEBSERVICE_DEFINITIONS = "PATTERNS_HTTP_ENDPOINTS";

	public static WebServiceModule getWebServiceDefinition(String buildId, String module,ProcessorContext ctx,boolean force) throws JavascribeException {
		WebServiceModule ret = null;

		String objectName = WEBSERVICE_DEFINITIONS+"_" + buildId+"_"+module;
		ret = (WebServiceModule)ctx.getObject(objectName);
		if ((ret==null) && (force)) {
			ret = new WebServiceModule();
			ctx.setObject(objectName, ret);
		}
		
		// We need to have a Java Webapp platform
		// TODO: It doesn't have to be Tomcat
		JavaWebUtils.ensureWebPlatform(ctx, buildId);

		return ret;
	}

}

