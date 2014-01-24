package net.sf.javascribe.patterns.domain;

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
@XmlRootElement(name="retrieveDataStrategy")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="retrieveDataStrategy",propOrder={ "operation" })
public class RetrieveDataStrategy extends ComponentBase {

	public static final String TRANSLATION_OPERATIONS = "net.sf.javascribe.patterns.domain.RetrieveDataOperations";
	public static final String TRANSLATION_STRATEGY = "net.sf.javascribe.patterns.domain.RetrieveDataStrategy.";
	
	@Override
	public int getPriority() { return CorePatternConstants.PRIORITY_DATA_TRANSLATOR-1; }

	@XmlAttribute
	private String name = "";

	@XmlElement
	private List<RetrieveDataOperation> operation = new ArrayList<RetrieveDataOperation>();

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<RetrieveDataOperation> getOperation() {
		return operation;
	}
	public void setOperation(List<RetrieveDataOperation> operation) {
		this.operation = operation;
	}

}

