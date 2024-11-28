package net.sf.javascribe.patterns.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Plugin
@XmlConfig
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="rel",propOrder={  })
public class SingleRelationship {

	@XmlValue
	private String value = "";
	
}
