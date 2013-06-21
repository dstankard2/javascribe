package net.sf.javascribe.patterns.service;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;

@Scannable
@XmlRootElement(name="jpaTx")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="jpaTx",propOrder={ "operation" })
public class JpaTxOperation extends ServiceOperation implements NestingOperation {

	public ServiceOperationRenderer getRenderer() { return new JpaTxRenderer(this); }

	@XmlElementRef
	private List<ServiceOperation> operation = new ArrayList<ServiceOperation>();
	
	@XmlAttribute
	private String locator = null;
	
	@XmlAttribute
	private String commit = null;
	
	@XmlAttribute
	private String ref = null;
	
	public List<ServiceOperation> getOperation() {
		return operation;
	}

	public void setOperation(List<ServiceOperation> operation) {
		this.operation = operation;
	}

	public String getLocator() {
		return locator;
	}

	public void setLocator(String locator) {
		this.locator = locator;
	}

	public String getCommit() {
		return commit;
	}

	public void setCommit(String commit) {
		this.commit = commit;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

}

