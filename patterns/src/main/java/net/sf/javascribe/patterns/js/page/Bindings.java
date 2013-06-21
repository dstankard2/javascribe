package net.sf.javascribe.patterns.js.page;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.patterns.CorePatternConstants;

@Scannable
@XmlRootElement(name="bindings")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="bindings",propOrder={ "binding" })
public class Bindings extends ComponentBase {

	@XmlElement
	private List<Binding> binding = new ArrayList<Binding>();
	
	@XmlAttribute
	private String pageName = null;
	
	@Override
	public int getPriority() { return CorePatternConstants.PRIORITY_PAGE_BINDING; }
	
	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public List<Binding> getBinding() {
		return binding;
	}

	public void setBinding(List<Binding> binding) {
		this.binding = binding;
	}
	
}
