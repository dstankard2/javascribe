package net.sf.javascribe.patterns.xml.javascript;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;

@Scannable
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="webServiceClient",propOrder={  })
public class WebServiceClient extends ComponentBase {

	@XmlAttribute
	private String webServiceName = "";

	@XmlAttribute
	private String path = "";

	@XmlAttribute
	private String obj = "";
	
	@XmlAttribute
	private String serviceName = "";
	
	public String getWebServiceName() {
		return webServiceName;
	}

	public void setWebServiceName(String webServiceName) {
		this.webServiceName = webServiceName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getObj() {
		return obj;
	}

	public void setObj(String obj) {
		this.obj = obj;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
}

