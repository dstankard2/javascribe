package net.sf.javascribe.test.integration.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.*;

import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.api.plugin.ProcessorLogMessage;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.DependencyData;
import net.sf.javascribe.engine.data.ProcessingData;
import net.sf.javascribe.engine.data.files.ComponentFile;
import net.sf.javascribe.engine.data.files.UserFile;
import net.sf.javascribe.engine.data.files.WatchedResource;
import net.sf.javascribe.engine.data.processing.ProcessingState;
import net.sf.javascribe.engine.manager.WorkspaceManager;
import net.sf.javascribe.engine.service.FolderScannerService;
import net.sf.javascribe.engine.service.LanguageSupportService;
import net.sf.javascribe.engine.service.OutputService;
import net.sf.javascribe.engine.service.PatternService;
import net.sf.javascribe.engine.util.FileUtil;
import net.sf.javascribe.engine.util.OutputUtil;
import net.sf.javascribe.langsupport.java.JavaLanguageSupport;
import net.sf.javascribe.patterns.http.HttpMethod;
import net.sf.javascribe.patterns.java.dataobject.DataObjectProcessor;
import net.sf.javascribe.patterns.java.dataobject.JsonObjectProcessor;
import net.sf.javascribe.patterns.java.http.EndpointProcessor;
import net.sf.javascribe.patterns.java.http.WebServiceModuleProcessor;
import net.sf.javascribe.patterns.java.service.ServiceProcessor;
import net.sf.javascribe.patterns.test.RequireAttributePattern;
import net.sf.javascribe.patterns.test.RequireAttributeProcessor;
import net.sf.javascribe.patterns.test.RequireTypePattern;
import net.sf.javascribe.patterns.test.RequireTypeProcessor;
import net.sf.javascribe.patterns.test.userfiles.FolderWatchingPattern;
import net.sf.javascribe.patterns.test.userfiles.FolderWatchingProcessor;
import net.sf.javascribe.patterns.tomcat.EmbedTomcatFinalizer;
import net.sf.javascribe.patterns.tomcat.EmbedTomcatJarProcessor;
import net.sf.javascribe.patterns.tomcat.EmbedTomcatMain;
import net.sf.javascribe.patterns.tomcat.TomcatJndiDatasourceProcessor;
import net.sf.javascribe.patterns.xml.java.dataobject.DataObject;
import net.sf.javascribe.patterns.xml.java.dataobject.JsonObject;
import net.sf.javascribe.patterns.xml.java.http.Endpoint;
import net.sf.javascribe.patterns.xml.java.http.WebServiceModuleComponent;
import net.sf.javascribe.patterns.xml.java.service.Service;
import net.sf.javascribe.patterns.xml.tomcat.EmbedTomcatJar;
import net.sf.javascribe.patterns.xml.tomcat.TomcatJndiDatasource;
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

	@MockDependency
	private OutputUtil outputUtil;
	
	// We need to initialize patterns before this class runs
	@Inject
	private PatternService patternService;
	
	@Inject
	private LanguageSupportService languageSupportService;
	
	protected void resetFileChanges(ApplicationData application) {
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
	
	@SuppressWarnings("unchecked")
	@BeforeClass
	public void setupClass() {
		includePatterns(FolderWatchingPattern.class, RequireTypePattern.class, JsonObject.class, WebServiceModuleComponent.class);
		includePattern(RequireAttributePattern.class);
		includePattern(TomcatJndiDatasource.class);
		includePattern(EmbedTomcatJar.class);
		includePattern(EmbedTomcatFinalizer.class);
		includePattern(Endpoint.class);
		includePattern(Service.class);
		
		includeProcessors(FolderWatchingProcessor.class, RequireTypeProcessor.class, WebServiceModuleProcessor.class);
		includeProcessor(RequireAttributeProcessor.class);
		includeProcessor(JsonObjectProcessor.class);
		includeProcessor(TomcatJndiDatasourceProcessor.class);
		includeProcessor(EmbedTomcatJarProcessor.class);
		includeProcessor(EmbedTomcatMain.class);
		includeProcessor(EndpointProcessor.class);
		includeProcessor(ServiceProcessor.class);

		// Including some standard patterns
		includePattern(DataObject.class);
		includeProcessor(DataObjectProcessor.class);

		includeLanguageSupport(JavaLanguageSupport.class);

		patternService.initializePatterns();
		languageSupportService.loadLanguageSupport();
	}
	
	@Test
	public void testFolderWatchingOverMultipleScans() throws Exception {
		ApplicationData application = super.createApplicationShell("test");
		// DependencyData deps = application.getDependencyData();
		// ProcessingData pd = application.getProcessingData();

		Component watcherComp = FolderWatchingPattern.builder().name("TestService").path("/files").build();
		Component requireType = RequireTypePattern.builder().lang("Java8").requiredType("TestService").build();
		Component requireAttrib = RequireAttributePattern.builder().requiredAttribute("testService").build();
		// Add some files to the workspace
		ComponentFile cf2 = super.createComponentFile("comps-2.xml", application, requireType, requireAttrib);
		ComponentFile cf = super.createComponentFile("comps.xml", application, watcherComp);
		UserFile uf = super.createUserFile("test.txt", "/files/test.txt", "This is content of a file");
		UserFile uf2 = super.createUserFile("test2.txt", "/files/test2.txt", "This is content of a second file");

		filesRemoved(application);
		filesAdded(application, cf, cf2, uf, uf2);
		// The workspace should build the first time
		workspaceManager.scanApplicationDir(application, true, false);

		// Remove first user file
		resetFileChanges(application);
		filesRemoved(application, uf);
		workspaceManager.scanApplicationDir(application, false, false);
		// Processing should be successful
		// TestDataObject type should still be there with 2 dependencies
		// There should be 1 userFile
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		assertThat(application.getUserFiles().size()).isEqualTo(1);
		assertThat(application.getDependencyData().getTypeDependencies().get("Java8").get("TestService").size()).isEqualByComparingTo(2);

		// Re-add first user file.  Processing should now be successful
		resetFileChanges(application);
		filesAdded(application, uf, uf2);
		workspaceManager.scanApplicationDir(application, false, false);
		
		// Remove second user file.
		resetFileChanges(application);
		filesRemoved(application, uf2);
		workspaceManager.scanApplicationDir(application, false, false);
		// TestDataObject should not be there, nor the system attribute.

		// Re-add both user files.  Processing should now be successful
		resetFileChanges(application);
		filesAdded(application, uf, uf2);
		workspaceManager.scanApplicationDir(application, false, false);
		System.out.println("hi");
	}
	
	// TODO: Check build handling
	// TODO: Check adding and removing of root folder build, that default build is added/removed appropriately

	// Test data object pattern in cases where one data object includes another
	@Test
	public void testComponentDependencies() throws Exception {
		ApplicationData application = super.createApplicationShell("test");
		DependencyData deps = application.getDependencyData();
		ProcessingData pd = application.getProcessingData();
		Map<String,String> properties = new HashMap<>();

		DataObject containerObj = DataObject.builder().name("TestContainer").properties("testObjList,name:string").build();
		DataObject testObj = DataObject.builder().name("TestObj").properties("amount:integer").build();
		
		properties.put("java.dataObject.package", "test");
		properties.put("java.rootPackage", "com.test");
		properties.put("outputPath.java", "/");
		setProperties(application.getRootFolder(), properties);
		ComponentFile badCf = super.createComponentFile("dataObjects.xml", application, containerObj, testObj);

		filesRemoved(application);
		filesAdded(application, badCf);
		// The workspace should not build the first time.  There should be an error message that a property type couldn't be found
		workspaceManager.scanApplicationDir(application, true, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.ERROR);
		ProcessorLogMessage msg = application.getMessages().get(application.getMessages().size()-1);
		assertThat(application.getAddedSourceFiles().size()).isEqualByComparingTo(0);
		//Mockito.verifyNoMoreInteractions(mocks.toArray());

		// Fix the ordering of components.  Processing should be successful
		ComponentFile goodCf = super.createComponentFile("dataObjects.xml", application, testObj, containerObj);
		filesRemoved(application, badCf);
		filesAdded(application, goodCf);
		workspaceManager.scanApplicationDir(application, false, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		assertThat(application.getApplicationTypes().get("Java8").get("TestContainer")).isNotNull();
		assertThat(application.getApplicationTypes().get("Java8").get("TestObj")).isNotNull();

		// Add a separate file that depends on the components in this one at a lesser priority.
		// Processing should still be successful

		DataObject testCont = DataObject.builder().name("ObjContainer").properties("testContainer").build();
		//JsonObject jsonObject = JsonObject.builder().name("TestContainer").build();

		ComponentFile otherFile = super.createComponentFile("jsonObjects.xml", application, testCont);
		filesRemoved(application);
		filesAdded(application, otherFile);
		workspaceManager.scanApplicationDir(application, false, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);

		// Touch the other file.  processing should still be successful
		filesRemoved(application, otherFile);
		filesAdded(application, otherFile);
		workspaceManager.scanApplicationDir(application, false, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		
	}

	// Test object handling using a Tomcat platform object and an added servlet
	@Test
	public void testTomcatPlatformApp() throws Exception {
		ApplicationData application = super.createApplicationShell("tomcat");
		DependencyData deps = application.getDependencyData();
		ProcessingData pd = application.getProcessingData();
		Map<String,String> properties = new HashMap<>();
		
		EmbedTomcatJar jarComp = EmbedTomcatJar.builder().jarName("test.jar").port(123).pkg("test").context("/").build();
		TomcatJndiDatasource dsComp = TomcatJndiDatasource.builder().username("usr").password("pwd").url("url")
				.build();

		properties.put("java.rootPackage", "com.test");
		properties.put("tomcat.jndi.datasource.driverClass", "driver");
		properties.put("outputPath.java", "/");
		properties.put("java.webservice.pkg", "com.test");
		properties.put("java.httpendpoint.operationResult", "result");
		properties.put("java.service.pkg", "service");

		setProperties(application.getRootFolder(), properties);

		// Create a component file with the jar and datasource
		ComponentFile cf = super.createComponentFile("test.xml", application, jarComp, dsComp);

		filesRemoved(application);
		filesAdded(application, cf);
		workspaceManager.scanApplicationDir(application, true, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		
		WebServiceModuleComponent ws = WebServiceModuleComponent.builder().name("mod1").uri("/mod1").build();
		ComponentFile cf2 = super.createComponentFile("test2.xml", application, ws);
		
		filesRemoved(application);
		filesAdded(application, cf2);
		workspaceManager.scanApplicationDir(application, true, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		
		// Add an endpoint to the module
		Endpoint ep = Endpoint.builder().functionName("noop").method(HttpMethod.POST).module("mod1").operation("testService.noop").path("/noop").build();
		
		Service service = Service.builder().module("Test").name("noop").serviceOperation(new ArrayList<>()).build();
		
		ComponentFile serviceFile = super.createComponentFile("service.xml", application, ep, service);
		filesRemoved(application);
		filesAdded(application, serviceFile);
		workspaceManager.scanApplicationDir(application, true, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
	}
	
}

