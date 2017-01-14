package net.sf.javascribe.patterns.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.patterns.CorePatternConstants;

@Scannable
@XmlRootElement(name="txLocatorThreadLocal")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="txLocatorThreadLocal",propOrder={  })
public class ThreadLocalTxLocator extends ComponentBase {

	@XmlAttribute
	private String name = null;
	
	@XmlAttribute
	private String pu = null;
	
	@Override
	public int getPriority() { return CorePatternConstants.PRIORITY_THREAD_LOCAL_TX_LOCATOR; }

	public String getPu() {
		return pu;
	}

	public void setPu(String pu) {
		this.pu = pu;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}

