package net.sf.javascribe.patterns.tomcat;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.patterns.java.http.JavaWebUtils;
import net.sf.javascribe.patterns.web.JavaWebappRuntimePlatform;
import net.sf.javascribe.patterns.xml.tomcat.TomcatJndiDatasource;

@Plugin
public class TomcatJndiDatasourceProcessor implements ComponentProcessor<TomcatJndiDatasource> {

	@Override
	public void process(TomcatJndiDatasource comp, ProcessorContext ctx) throws JavascribeException {
		DataSourceInfo info = new DataSourceInfo();
		JavaWebappRuntimePlatform platform = JavaWebUtils.getWebPlatform(ctx);
		if (platform==null) {
			throw new JavascribeException("Cannot create a Tomcat JNDI Datasource unless the current runtime platform is Embed Tomcat");
		}
		
		if (!(platform instanceof EmbedTomcatRuntimePlatform)) {
			throw new JavascribeException("Cannot create a Tomcat JNDI Datasource unless the current runtime platform is Embed Tomcat");
		}
		
		EmbedTomcatRuntimePlatform tomcat = (EmbedTomcatRuntimePlatform)platform;
		info.setDriverClass(comp.getDriverClass());
		info.setName(comp.getName());
		info.setPassword(comp.getPassword());
		info.setUsername(comp.getUsername());
		info.setUrl(comp.getUrl());
		tomcat.getDataSources().add(info);
		ctx.getBuildContext().addDependency("tomcat-dbcp");
	}

}
