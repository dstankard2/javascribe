package net.sf.javascribe.engine.service;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.config.Component;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RegisteredComponentPattern {

	private Class<? extends Component> componentClass;
	
	@Builder.Default
	private Set<Class<ComponentProcessor>> processorClasses = new HashSet<>();
	
}

