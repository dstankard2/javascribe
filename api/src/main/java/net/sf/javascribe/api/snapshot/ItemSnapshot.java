package net.sf.javascribe.api.snapshot;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ItemSnapshot {

	private int id;
	private String name;
	private int originatorId;
	private String state;
	@Builder.Default
	private List<LogMessageSnapshot> logs = new ArrayList<>();

}

