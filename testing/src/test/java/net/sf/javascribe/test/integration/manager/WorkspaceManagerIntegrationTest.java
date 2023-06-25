package net.sf.javascribe.test.integration.manager;

import java.util.ArrayList;
import java.util.Arrays;

import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.*;

import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.DependencyData;
import net.sf.javascribe.engine.data.ProcessingData;
import net.sf.javascribe.engine.data.files.ComponentFile;
import net.sf.javascribe.engine.data.files.UserFile;
import net.sf.javascribe.engine.data.files.WatchedResource;
import net.sf.javascribe.engine.manager.WorkspaceManager;
import net.sf.javascribe.engine.service.FolderScannerService;
import net.sf.javascribe.engine.service.LanguageSupportService;
import net.sf.javascribe.engine.service.OutputService;
import net.sf.javascribe.engine.service.PatternService;
import net.sf.javascribe.engine.util.FileUtil;
import net.sf.javascribe.langsupport.java.JavaLanguageSupport;
import net.sf.javascribe.patterns.test.RequireTypePattern;
import net.sf.javascribe.patterns.test.RequireTypeProcessor;
import net.sf.javascribe.patterns.test.userfiles.FolderWatchingPattern;
import net.sf.javascribe.patterns.test.userfiles.FolderWatchingProcessor;
import net.sf.javascribe.test.integration.Inject;
import net.sf.javascribe.test.integration.ManagerTest;
import net.sf.javascribe.test.integration.MockDependency;

public class WorkspaceManagerIntegrationTest extends ManagerTest {

	@Inject
	private WorkspaceManager workspaceManager;
	
	@MockDependency
	private FileUtil fileUtil;
	
	@MockDependency
	private FolderScannerService folderScannerService;
	
	@MockDependency
	private OutputService outputService;

	// We need to initialize patterns before this class runs
	@Inject
	private PatternService patternService;
	
	@Inject
	private LanguageSupportService languageSupportService;
	
	protected void resetFileChanges() {
		Mockito.reset(folderScannerService);
	}
	
	protected void filesRemoved(ApplicationData application, WatchedResource... files) {
		if (files!=null) {
			Mockito.when(folderScannerService.findFilesRemoved(application)).thenReturn(Arrays.asList(files));
		} else {
			Mockito.when(folderScannerService.findFilesRemoved(application)).thenReturn(new ArrayList<>());
		}
	}
	
	protected void filesAdded(ApplicationData application, WatchedResource... files) {
		if (files!=null) {
			Mockito.when(folderScannerService.findFilesAdded(application)).thenReturn(Arrays.asList(files));
		} else {
			Mockito.when(folderScannerService.findFilesAdded(application)).thenReturn(new ArrayList<>());
		}
	}
	
	@BeforeClass
	public void setupClass() {
		includePattern(FolderWatchingPattern.class);
		includePattern(RequireTypePattern.class);
		includeProcessor(FolderWatchingProcessor.class);
		includeProcessor(RequireTypeProcessor.class);

		includeLanguageSupport(JavaLanguageSupport.class);

		patternService.initializePatterns();
		languageSupportService.loadLanguageSupport();
	}
	
	@Test
	public void testFolderWatchingOverMultipleScans() throws Exception {
		ApplicationData application = super.createApplicationShell("test");
		DependencyData deps = application.getDependencyData();
		ProcessingData pd = application.getProcessingData();

		Component watcherComp = FolderWatchingPattern.builder().name("testwatcher").path("/files").build();
		Component requireType = RequireTypePattern.builder().lang("Java8").requiredType("TestDataObject").build();
		// Add some files to the workspace
		ComponentFile cf = super.createComponentFile("comps.xml", application, watcherComp, requireType);
		UserFile uf = super.createUserFile("/files/test.txt", "This is content of a file");
		UserFile uf2 = super.createUserFile("/files/test2.txt", "This is content of a second file");

		filesRemoved(application);
		filesAdded(application, cf, uf, uf2);

		workspaceManager.scanApplicationDir(application, true, false);

		// Remove first user file
		resetFileChanges();
		filesRemoved(application, uf);

		workspaceManager.scanApplicationDir(application, true, false);

		// TestDataObject type should still be there with 2 dependencies
		// There should be 1 userFile
		assertThat(application.getUserFiles().size()).isEqualTo(1);
		assertThat(application.getDependencyData().getTypeDependencies().get("Java8").get("TestDataObject").size()).isEqualByComparingTo(2);
		
		// Re-add first user file.
		// TestDataObject type should still be there with 3 dependencies
		resetFileChanges();
		filesAdded(application, uf);

		System.out.println("hi");
	}
}
