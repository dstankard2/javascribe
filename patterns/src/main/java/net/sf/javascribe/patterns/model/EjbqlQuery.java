package net.sf.javascribe.patterns.model;

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
@XmlRootElement(name="ejbqlQuery")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="ejbqlQuery",propOrder={ })
public class EjbqlQuery extends ComponentBase {

	public static final String EJBQL_QUERY_PU = "net.sf.javascribe.patterns.model.EjbqlQuery.pu";
	public static final String EJBQL_QUERY_QUERY_SET = "net.sf.javascribe.patterns.model.EjbqlQuery.querySet";
	public static final String EJBQL_QUERY_PKG = "net.sf.javascribe.patterns.model.EjbqlQuery.pkg";

	public int getPriority() { return CorePatternConstants.PRIORITY_EJBQL_QUERY; }
	
	@XmlElement
	private QueryString queryString = null;
	
	@XmlAttribute
	private String pageable = "false";
	
	@XmlAttribute
	private String pu = "";
	
	@XmlAttribute
	private String querySet = "";
	
	@XmlAttribute
	private String returnType = "";
	
	@XmlAttribute
	private String multiple = "";
	
	@XmlAttribute
	private String params = "";
	
	@XmlAttribute
	private String name = "";

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getMultiple() {
		return multiple;
	}

	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQuerySet() {
		return querySet;
	}

	public void setQuerySet(String querySet) {
		this.querySet = querySet;
	}

	public String getPu() {
		return pu;
	}

	public void setPu(String pu) {
		this.pu = pu;
	}

	public QueryString getQueryString() {
		return queryString;
	}

	public void setQueryString(QueryString queryString) {
		this.queryString = queryString;
	}

	public String getPageable() {
		return pageable;
	}

	public void setPageable(String pageable) {
		this.pageable = pageable;
	}
	
}
