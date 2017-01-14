package net.sf.javascribe.patterns.xml.view;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import net.sf.javascribe.api.annotation.Scannable;

@Scannable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="templateText",propOrder={ })
public class TemplateText {

	@XmlValue
	private String htmlText = "";

	public String getHtmlText() {
		return htmlText;
	}
	public void setHtmlText(String htmlText) {
		this.htmlText = htmlText;
	}
	
}
