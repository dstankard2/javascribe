package net.sf.javascribe.patterns.test.userfiles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.javascribe.api.config.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FolderWatchingPattern extends Component {

	private String name;
	
	@Builder.Default
	private int priority = 50;
	
	private String path;
	
}
