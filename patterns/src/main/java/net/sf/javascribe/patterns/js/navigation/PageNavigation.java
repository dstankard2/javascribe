package net.sf.javascribe.patterns.js.navigation;

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
@XmlRootElement(name="pageNavigation")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="pageNavigation",propOrder={ "page" })
public class PageNavigation extends ComponentBase {

	public static final String PROPERTY_ANIMATION_PROVIDER = "net.sf.javascribe.patterns.js.navigation.animationProvider";
	
	@Override
	public int getPriority() { return CorePatternConstants.PRIORITY_PAGE_NAVIGATION; }
	
	@XmlElement
	private List<Page> page = new ArrayList<Page>();
	
	@XmlAttribute
	private String name = "";
	
	@XmlAttribute
	private String hide = "";
	
	@XmlAttribute
	private String show = "";

	public List<Page> getPage() {
		return page;
	}

	public void setPage(List<Page> page) {
		this.page = page;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHide() {
		return hide;
	}

	public void setHide(String hide) {
		this.hide = hide;
	}

	public String getShow() {
		return show;
	}

	public void setShow(String show) {
		this.show = show;
	}
	
}
