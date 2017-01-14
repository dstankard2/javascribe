package net.sf.javascribe.patterns.xml.translator;

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
@XmlRootElement(name="translationStrategy")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="translationStrategy",propOrder={ "operation" })
public class TranslationStrategy extends ComponentBase {

	public static final String TRANSLATION_OPERATIONS = "net.sf.javascribe.patterns.translator.TranslationOperations";
	public static final String TRANSLATION_STRATEGY = "net.sf.javascribe.patterns.translator.TranslationStrategy.";
	
	@Override
	public int getPriority() { return CorePatternConstants.PRIORITY_DATA_TRANSLATOR-1; }

	@XmlAttribute
	private String name = "";

	@XmlElement
	private List<TranslationOperation> operation = new ArrayList<TranslationOperation>();

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<TranslationOperation> getOperation() {
		return operation;
	}
	public void setOperation(List<TranslationOperation> operation) {
		this.operation = operation;
	}

}

