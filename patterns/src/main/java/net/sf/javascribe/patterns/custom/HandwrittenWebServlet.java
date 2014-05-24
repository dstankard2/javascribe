package net.sf.javascribe.patterns.custom;

import javax.xml.bind.annotation.XmlAttribute;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.patterns.CorePatternConstants;

@Scannable
public class HandwrittenWebServlet extends ComponentBase {

	@Override
	public int getPriority() {
		return CorePatternConstants.PRIORITY_SERVLET_EVENT;
	}
	@XmlAttribute
	private String filters = "";
	
	@XmlAttribute
	private String name = "";
	
	@XmlAttribute
	private String className = "";
	
	@XmlAttribute
	private String uriPath = "";
	
	public String getFilters() {
		return filters;
	}

	public void setFilters(String filters) {
		this.filters = filters;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getUriPath() {
		return uriPath;
	}

	public void setUriPath(String uriPath) {
		this.uriPath = uriPath;
	}

}
