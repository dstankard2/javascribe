package net.sf.javascribe.api.snapshot;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SourceFileSnapshot {

	private String path;
	
	@Builder.Default
	private List<Integer> originators = new ArrayList<>();

}
