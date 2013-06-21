package net.sf.javascribe.patterns.js.navigation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentConfigElement;

@Scannable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="page",propOrder={ })
public class Page implements ComponentConfigElement {

	@XmlAttribute
	private String name = "";
	
	@XmlAttribute
	private String div = "";
	
	@XmlAttribute
	private String onShow = "";
	
	@XmlAttribute
	private String onHide = "";
	
	@XmlAttribute
	private String onRefresh = "";
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDiv() {
		return div;
	}

	public void setDiv(String div) {
		this.div = div;
	}

	public String getOnShow() {
		return onShow;
	}

	public void setOnShow(String onShow) {
		this.onShow = onShow;
	}

	public String getOnHide() {
		return onHide;
	}

	public void setOnHide(String onHide) {
		this.onHide = onHide;
	}

	public String getOnRefresh() {
		return onRefresh;
	}

	public void setOnRefresh(String onRefresh) {
		this.onRefresh = onRefresh;
	}

}
