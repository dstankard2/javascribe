package net.sf.javascribe.patterns.test.userfiles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.javascribe.api.config.Component;

/**
 * Test pattern for integration testing.  This will create a service named as "name".  Each file watched in 
 * the given directory will add a rule to the service.
 */
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
