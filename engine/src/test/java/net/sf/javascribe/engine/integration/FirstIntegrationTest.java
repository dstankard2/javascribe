package net.sf.javascribe.engine.integration;

import org.testng.annotations.Test;

import net.sf.javascribe.engine.ApplicationBuilder;
import net.sf.javascribe.engine.manager.WorkspaceManager;
import net.sf.javascribe.engine.util.FileUtil;

public class FirstIntegrationTest extends ContainerTest {

	@Inject
	private WorkspaceManager workspaceManager;
	
	@MockDependency
	private FileUtil fileUtil;

	@Test
	public void test() {
		//ApplicationBuilder.create().rootFolder(null)
		
		//workspaceManager.
		System.out.println("hi");
	}
}
