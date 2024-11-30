package net.sf.javascribe.patterns.xml.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;
import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.patterns.PatternPriority;

@Builder
@AllArgsConstructor
@Getter
@Setter
@Plugin
@XmlConfig
@XmlRootElement(name="entityRelationships")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="entityRelationships",propOrder={ "rel" })
public class EntityRelationships extends Component {

	public EntityRelationships() {
	}

	public int getPriority() {
		return PatternPriority.ENTITY_RELATIONSHIPS;
	}

	@Builder.Default
	@XmlAttribute
	private String jpaDaoFactoryRef = "";
	
	@Builder.Default
	@XmlAttribute
	private String persistenceUnit = "";

	@XmlElement
	@Builder.Default
	private List<SingleRelationship> rel = new ArrayList<>();

}
