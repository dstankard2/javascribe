package net.sf.javascribe.api.snapshot;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.javascribe.api.resources.ApplicationFolder;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BuildContextSnapshot {

	@Builder.Default
	private Map<String,String> javascribeProperties = new HashMap<>();
	private ApplicationFolder folder;

}
