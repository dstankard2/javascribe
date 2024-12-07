package net.sf.javascribe.patterns.maven;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PluginConfig {

	@Builder.Default
	private String artifact = null;

	@Builder.Default
	private List<String> dependencies = new ArrayList<>();
	
	@Builder.Default
	private PropertySet configuration = new PropertySet("configuration");
	
	@Builder.Default
	private List<ExecutionConfig> executions = new ArrayList<>();

	public PluginConfig(String artifact) {
		this.artifact = artifact;
	}
	
}

