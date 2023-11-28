package net.sf.javascribe.api.snapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApplicationSnapshot {

	@Builder.Default
	private List<BuildContextSnapshot> buildContexts = new ArrayList<>();

	@Builder.Default
	private Map<String,SystemAttributeSnapshot> allSystemAttributes = new HashMap<>();

	@Builder.Default
	private List<ItemSnapshot> allItems = new ArrayList<>();
	
	@Builder.Default
	private Map<String,SourceFileSnapshot> sourceFiles = new HashMap<>();
	
	private String name;
	
	private String status;

}

