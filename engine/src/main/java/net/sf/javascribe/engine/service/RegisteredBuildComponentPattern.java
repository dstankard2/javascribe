package net.sf.javascribe.engine.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.sf.javascribe.api.BuildComponentProcessor;
import net.sf.javascribe.api.config.BuildComponent;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RegisteredBuildComponentPattern {

	private Class<? extends BuildComponent> componentClass;
	
	@SuppressWarnings("rawtypes")
	private Class<BuildComponentProcessor> processorClass;
	
}

