package net.sf.javascribe.patterns.build;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;
import net.sf.javascribe.api.config.BuildComponent;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name="testBuild")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="testBuild",propOrder={  })
@Builder
@XmlConfig
@Plugin
public class JavaBuild extends BuildComponent {

	@Builder.Default
	private String name = "";
	
	public String getComponentName() {
		return "JavaBuild["+name+"]";
	}
}
