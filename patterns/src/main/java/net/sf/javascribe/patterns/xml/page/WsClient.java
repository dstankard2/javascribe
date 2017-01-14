package net.sf.javascribe.patterns.xml.page;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.patterns.CorePatternConstants;

@Scannable
@XmlRootElement(name="wsClient")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="wsClient",propOrder={ })
public class WsClient extends ComponentBase {

	@Override
	public int getPriority() { return CorePatternConstants.PRIORITY_PAGE_WS_CLIENT; }
	
	@XmlAttribute
	private String pageName = null;
	
	@XmlAttribute
	private String fn = null;
	
	@XmlAttribute
	private String module = null;
	
	@XmlAttribute
	private String service = null;
	
	@XmlAttribute
	private String completeEvent = null;

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getCompleteEvent() {
		return completeEvent;
	}

	public void setCompleteEvent(String completeEvent) {
		this.completeEvent = completeEvent;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getFn() {
		return fn;
	}

	public void setFn(String fn) {
		this.fn = fn;
	}

}
