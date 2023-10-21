package net.sf.javascribe.api.snapshot;

import java.util.ArrayList;
import java.util.List;

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

	private String name;

}

