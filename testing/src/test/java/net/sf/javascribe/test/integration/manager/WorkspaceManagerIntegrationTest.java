package net.sf.javascribe.test.integration.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.*;

import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.DependencyData;
import net.sf.javascribe.engine.data.ProcessingData;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
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
import net.sf.javascribe.langsupport.java.types.impl.JavaServiceType;
import net.sf.javascribe.langsupport.javascript.JavascriptLanguageSupport;
import net.sf.javascribe.patterns.http.HttpMethod;
import net.sf.javascribe.patterns.java.dataobject.DataObjectProcessor;
import net.sf.javascribe.patterns.java.dataobject.JsonObjectProcessor;
import net.sf.javascribe.patterns.java.handwritten.HandwrittenCodeProcessor;
import net.sf.javascribe.patterns.java.http.EndpointProcessor;
import net.sf.javascribe.patterns.java.http.WebServiceModuleProcessor;
import net.sf.javascribe.patterns.java.service.ServiceProcessor;
import net.sf.javascribe.patterns.js.WsClientsProcessor;
import net.sf.javascribe.patterns.js.ajax.XMLHttpRequestProvider;
import net.sf.javascribe.patterns.js.page.PageBuilderComponent;
import net.sf.javascribe.patterns.js.page.PageBuilderProcessor;
import net.sf.javascribe.patterns.js.page.PageFnProcessor;
import net.sf.javascribe.patterns.js.page.PageModelProcessor;
import net.sf.javascribe.patterns.js.page.PageProcessor;
import net.sf.javascribe.patterns.maven.MavenBuildComponentProcessor;
import net.sf.javascribe.patterns.test.FirstPattern;
import net.sf.javascribe.patterns.test.FirstProcessor;
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
import net.sf.javascribe.patterns.xml.java.handwritten.HandwrittenCode;
import net.sf.javascribe.patterns.xml.java.http.Endpoint;
import net.sf.javascribe.patterns.xml.java.http.Response;
import net.sf.javascribe.patterns.xml.java.http.WebServiceModuleComponent;
import net.sf.javascribe.patterns.xml.java.service.CallRuleOperation;
import net.sf.javascribe.patterns.xml.java.service.Service;
import net.sf.javascribe.patterns.xml.js.ModuleClient;
import net.sf.javascribe.patterns.xml.js.WsClients;
import net.sf.javascribe.patterns.xml.js.page.Page;
import net.sf.javascribe.patterns.xml.js.page.PageFn;
import net.sf.javascribe.patterns.xml.js.page.PageModel;
import net.sf.javascribe.patterns.xml.maven.MavenBuild;
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
		// Put the files in their folders
		for(WatchedResource file : files) {
			String filename = file.getName();
			String path = file.getPath();
			String[] dirs = path.split("/");
			ApplicationFolderImpl folder = application.getRootFolder();
			for(int i=0;i<dirs.length;i++) {
				String dir = dirs[i];
				if (i == dirs.length-1) {
					if (file instanceof UserFile) {
						folder.getUserFiles().put(filename, (UserFile)file);
					} else if (file instanceof ComponentFile) {
						folder.getComponentFiles().put(filename, (ComponentFile)file);
					}
				} else if (dir.length() > 0) {
					ApplicationFolderImpl sub = folder.getSubFolders().get(dir);
					if (sub==null) {
						File subFile = new File(folder.getFolderFile(), dir);
						sub = new ApplicationFolderImpl(subFile, folder);
						folder.getSubFolders().put(filename, sub);
					}
					folder = sub;
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@BeforeClass
	public void setupClass() {

		// Test patterns for integration test only.
		includePatterns(FolderWatchingPattern.class, RequireTypePattern.class, 
				RequireAttributePattern.class, FirstPattern.class);
		includeProcessors(FolderWatchingProcessor.class, RequireTypeProcessor.class, 
				RequireAttributeProcessor.class, FirstProcessor.class);
		
		// Included for all test.
		includePattern(DataObject.class);
		includeProcessor(DataObjectProcessor.class);
		
		// Patterns for web application
		includePattern(MavenBuild.class);
		includePatterns(EmbedTomcatJar.class, EmbedTomcatFinalizer.class, Endpoint.class, 
				Service.class, TomcatJndiDatasource.class, HandwrittenCode.class, 
				WebServiceModuleComponent.class, JsonObject.class, WsClients.class, Page.class, PageModel.class, 
				PageBuilderComponent.class, PageFn.class);
		includeBuildProcessor(MavenBuildComponentProcessor.class);
		includeProcessors(EmbedTomcatJarProcessor.class, EmbedTomcatMain.class, EndpointProcessor.class,
				ServiceProcessor.class, TomcatJndiDatasourceProcessor.class, HandwrittenCodeProcessor.class,
				WebServiceModuleProcessor.class, JsonObjectProcessor.class, WsClientsProcessor.class, PageProcessor.class, PageModelProcessor.class,
				PageBuilderProcessor.class, PageFnProcessor.class);

		includeLanguageSupport(JavaLanguageSupport.class);
		includeLanguageSupport(JavascriptLanguageSupport.class);
		
		includePlugin(XMLHttpRequestProvider.class);

		patternService.initializePatterns();
		languageSupportService.loadLanguageSupport();
	}
	
	@Test
	public void testFolderWatchingOverMultipleScans() throws Exception {
		ApplicationData application = super.createApplicationShell("test");
		// DependencyData deps = application.getDependencyData();
		// ProcessingData pd = application.getProcessingData();

		// Create application properties
		Map<String,String> properties = new HashMap<>();
		properties.put("java.dataObject.package", "test");
		properties.put("java.rootPackage", "com.test");
		properties.put("outputPath.java", "/");
		properties.put("logLevel", "info");
		setProperties(application.getRootFolder(), properties);

		Component watcherComp = FolderWatchingPattern.builder().dependsOn("FirstService").serviceName("TestService").dataObjectName("TestDataObject").path("/files").build();
		Component requireType = RequireTypePattern.builder().lang("Java8").requiredType("TestService").build();
		Component requireAttrib = RequireAttributePattern.builder().requiredAttribute("testService").build();
		// Add some files to the workspace
		ComponentFile cf2 = super.createComponentFile("comps-2.xml", application, requireType, requireAttrib);
		ComponentFile cf = super.createComponentFile("comps.xml", application, watcherComp);
		UserFile uf = super.createUserFile("op.txt", "/files/op.txt", "This is content of a file");
		UserFile uf2 = super.createUserFile("otherOp.txt", "/files/otherOp.txt", "This is content of a second file");

		// First scan should be unsuccessful because there is a dependency on FirstService which doesn't exist yet
		// There should be 4 items total: 3 comps from file, default build
		// The folder watcher is never added because processsing fails first.
		filesRemoved(application);
		filesAdded(application, cf, cf2, uf, uf2);
		workspaceManager.scanApplicationDir(application, true, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.ERROR);
		assertThat(application.getProcessingData().getAllItems().size()).isEqualTo(4);

		// Add FirstService.  Now the scan will be successful with 2 user files
		// There will be 6 items: 4 comps from files, default build, folder watcher
		Component firstComp = FirstPattern.builder().serviceName("FirstService").build();
		ComponentFile firstCf = super.createComponentFile("first-comp.xml", application, firstComp);
		filesRemoved(application);
		filesAdded(application, firstCf);
		workspaceManager.scanApplicationDir(application, true, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		assertThat(application.getUserFiles().size()).isEqualTo(2);
		assertThat(application.getProcessingData().getAllItems().size()).isEqualTo(6);

		// Touch FirstService component file.  The scan should still complete
		// There should still be 6 items: 4 comps from file, default build, folder watcher
		filesRemoved(application, firstCf);
		filesAdded(application, firstCf);
		workspaceManager.scanApplicationDir(application, true, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		assertThat(application.getUserFiles().size()).isEqualTo(2);
		assertThat(application.getProcessingData().getAllItems().size()).isEqualTo(6);
		
		// Remove first user file
		filesRemoved(application, uf);
		filesAdded(application);
		workspaceManager.scanApplicationDir(application, false, false);
		// Processing should be successful
		// TestDataObject type should still be there with 2 dependencies
		// There should be 1 userFile
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		assertThat(application.getUserFiles().size()).isEqualTo(1);
		assertThat(application.getDependencyData().getTypeDependencies().get("Java8").get("TestService").size()).isEqualByComparingTo(2);
		assertThat(application.getProcessingData().getAllItems().size()).isEqualTo(6);

		// Touch first user file and add the second.  Processing should be successful
		// resetFileChanges(application);
		filesRemoved(application, uf);
		filesAdded(application, uf, uf2);
		workspaceManager.scanApplicationDir(application, false, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		assertThat(application.getUserFiles().size()).isEqualTo(2);
		assertThat(application.getProcessingData().getAllItems().size()).isEqualTo(6);
		
		// Remove second user file.
		resetFileChanges(application);
		filesRemoved(application, uf2);
		filesAdded(application);
		workspaceManager.scanApplicationDir(application, false, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		assertThat(application.getUserFiles().size()).isEqualTo(1);
		assertThat(application.getProcessingData().getAllItems().size()).isEqualTo(6);

		// Remove both userfiles (1 to remove).  Processing should fail.
		filesRemoved(application, uf);
		filesAdded(application);
		workspaceManager.scanApplicationDir(application, false, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.ERROR);
		assertThat(application.getUserFiles().size()).isEqualTo(0);
		assertThat(application.getProcessingData().getAllItems().size()).isEqualTo(6);
		
		// Re-add both user files.  Processing should now be successful
		filesRemoved(application);
		filesAdded(application, uf, uf2);
		workspaceManager.scanApplicationDir(application, false, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		assertThat(application.getUserFiles().size()).isEqualTo(2);
		assertThat(application.getProcessingData().getAllItems().size()).isEqualTo(6);
		
		// Add a component that depends on a type added by the folder watcher
		DataObject dependingObj = DataObject.builder().name("TestContainer").properties("obj:TestDataObject").build();
		ComponentFile objCf = super.createComponentFile("dataObjects.xml", application, dependingObj);
		filesRemoved(application);
		filesAdded(application, objCf);
		workspaceManager.scanApplicationDir(application, true, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		assertThat(application.getProcessingData().getAllItems().size()).isEqualTo(7);

		// Touch the data object file.  The scan should still be successful.
		filesRemoved(application, objCf);
		filesAdded(application, objCf);
		workspaceManager.scanApplicationDir(application, false, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		assertThat(application.getProcessingData().getAllItems().size()).isEqualTo(7);
		
		// Touch one of the user files.  The scan should still be successful
		filesRemoved(application, uf);
		filesAdded(application, uf);
		workspaceManager.scanApplicationDir(application, false, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		assertThat(application.getProcessingData().getAllItems().size()).isEqualTo(7);
		
		// Add a third user file.  The scan should still be successful
		UserFile uf3 = super.createUserFile("yetAnotherOp.txt", "/files/yetAnotherOp.txt", "This is content of a third file");
		filesRemoved(application);
		filesAdded(application, uf3);
		workspaceManager.scanApplicationDir(application, false, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		// The service type should have 3 operations
		JavaServiceType serviceType = (JavaServiceType)application.getType("Java8","TestService");
		assertThat(serviceType.getOperations().size()).isEqualTo(3);
		assertThat(application.getProcessingData().getAllItems().size()).isEqualTo(7);

		// Touch the data object file.  The scan should still be successful.
		filesRemoved(application, objCf);
		filesAdded(application, objCf);
		workspaceManager.scanApplicationDir(application, false, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		assertThat(application.getProcessingData().getAllItems().size()).isEqualTo(7);
		
		// Touch a comps file.  The scan should still be successful
		filesRemoved(application, cf2);
		filesAdded(application, cf2);
		workspaceManager.scanApplicationDir(application, false, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		serviceType = (JavaServiceType)application.getType("Java8","TestService");
		assertThat(serviceType.getOperations().size()).isEqualTo(3);
		assertThat(application.getProcessingData().getAllItems().size()).isEqualTo(7);
		
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
		// ProcessorLogMessage msg = application.getMessages().get(application.getMessages().size()-1);
		assertThat(application.getAddedSourceFiles().size()).isEqualByComparingTo(0);

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

		// DataObject testCont = DataObject.builder().name("ObjContainer").properties("testContainer").build();
		JsonObject jsonObject = JsonObject.builder().name("TestContainer").build();

		ComponentFile otherFile = super.createComponentFile("jsonObjects.xml", application, jsonObject);
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

	/**
	 * The purpose of this test is to build a somewhat realistic SPA application with a Tomcat server module and a Javascript front end.
	 * @throws Exception
	 */
	// Test object handling using a Tomcat platform object and an added servlet
	@Test
	public void testTomcatPlatformApp() throws Exception {
		ApplicationData application = super.createApplicationShell("tomcat");
		DependencyData deps = application.getDependencyData();
		ProcessingData pd = application.getProcessingData();
		Map<String,String> properties = new HashMap<>();

		properties.put("maven.build.phases", "compile");
		properties.put("maven.java.version", "17");
		properties.put("maven.deploy.phases", "package");
		properties.put("java.rootPackage", "com.test");
		properties.put("tomcat.jndi.datasource.driverClass", "driver");
		properties.put("java.webservice.pkg", "ws");
		properties.put("java.service.pkg", "service");
		properties.put("java.httpendpoint.operationResult", "result");
		properties.put("javascript.module.source", "app.js");
		properties.put("javascript.modules.srcRoot", "/content");
		properties.put("handwritten.servicelocator.package", "locator");

		setProperties(application.getRootFolder(), properties);

		// Ensure that there is a folder server/src/main/java/pkg
		super.ensureFolder(application, "server/src/main/java/pkg");
		
		// Add a directory for server and one for front end.  Both should have Maven builds.
		// There should be 4 items: 3 build contexts, 1 folder watcher component and 1 folder watcher
		MavenBuild serverBuild = MavenBuild.builder().artifact("testwebapp:server:1.0").packaging("jar")
				.description("Description of a test server").build();
		serverBuild.setId("server");
		ComponentFile serverBuildFile = super.createComponentFileInFolder("server", "build.xml", application, serverBuild);

		MavenBuild contentBuild = MavenBuild.builder().artifact("testwebapp:content:1.0").packaging("pom")
				.description("Test application UI content").build();
		contentBuild.setId("content");
		ComponentFile contentBuildFile = super.createComponentFileInFolder("content", "build.xml", application, contentBuild);

		filesRemoved(application);
		filesAdded(application, serverBuildFile, contentBuildFile);
		workspaceManager.scanApplicationDir(application, true, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		assertThat(pd.getAllItems().size()).isEqualTo(5);
		
		// Add a service userFile in server/src/main/java/pkg/TestService.java
		// Number of items should stay the same (5)
		final String testServiceContent = """
				package pkg;
				@net.sf.javascribe.patterns.java.handwritten.BusinessService(group = "Service", ref = "testService", priority = 20000)
				public class TestService {
				@net.sf.javascribe.patterns.java.handwritten.BusinessRule
				public String createSession() {
				return "abc123";
				}
				}
				""";
		UserFile userFile = super.createUserFile("TestService.java", "/server/src/main/java/pkg/TestService.java", testServiceContent);
		filesRemoved(application);
		filesAdded(application, userFile);
		workspaceManager.scanApplicationDir(application, true, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		assertThat(pd.getAllItems().size()).isEqualTo(5);

		// Add Tomcat platform, with data source
		// There should be 8 items: 3 builds, folder watcher comp, folder watcher, tomcat jar, finalizer, datasource
		EmbedTomcatJar jarComp = EmbedTomcatJar.builder().jarName("test").port(123).pkg("test").context("/").build();
		TomcatJndiDatasource dsComp = TomcatJndiDatasource.builder().username("usr").password("pwd").url("url")
				.build();
		ComponentFile platformCf = super.createComponentFileInFolder("server", "test.xml", application, jarComp, dsComp);

		filesRemoved(application);
		filesAdded(application, platformCf);
		workspaceManager.scanApplicationDir(application, true, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		assertThat(pd.getAllItems().size()).isEqualTo(8);
		
		// Add a web service module component
		// There should be 8+1 items now
		WebServiceModuleComponent wsModule = WebServiceModuleComponent.builder().name("FirstModule").uri("/first").build();
		ComponentFile moduleFile = super.createComponentFileInFolder("server", "wsModule.xml", application, wsModule);
		filesRemoved(application);
		filesAdded(application, moduleFile);
		workspaceManager.scanApplicationDir(application, true, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		assertThat(pd.getAllItems().size()).isEqualTo(9);
		
		// Add an endpoint, a service that functions as a no-op
		// There should be 9+2 items now
		Endpoint noopEndpoint = Endpoint.builder().functionName("noop").method(HttpMethod.GET).module("FirstModule").
				operation("firstService.noop").path("/noop").response(
						Arrays.asList(Response.builder().httpStatus(200).build())
				)
				.build();
		Service service = Service.builder().module("First").name("noop").serviceOperation(new ArrayList<>()).build();
		ComponentFile noopServiceFile = super.createComponentFileInFolder("server", "service.xml", application, noopEndpoint, service);

		filesRemoved(application);
		filesAdded(application, noopServiceFile);
		workspaceManager.scanApplicationDir(application, true, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		assertThat(pd.getAllItems().size()).isEqualTo(11);

		// Add a web service client component for the server module
		// There should be 11+1 items total
		WsClients clientsComp = WsClients.builder().urlPrefix("/path").buildId("server").moduleClient(Arrays.asList(
				ModuleClient.builder().module("FirstModule").name("FirstClient").ref("firstClient").build())).build();
		ComponentFile clientFile = super.createComponentFileInFolder("content", "client.xml", application, clientsComp);
		filesRemoved(application);
		filesAdded(application, clientFile);
		workspaceManager.scanApplicationDir(application, true, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		assertThat(pd.getAllItems().size()).isEqualTo(12);
		
		// Add a page and a model with a single property.
		// There should be 12+3 (with finalizer) items
		Page page = Page.builder().name("FirstPage").build();
		PageModel pageModel = PageModel.builder().pageName("FirstPage").properties("name:string").build();
		ComponentFile pageFile = super.createComponentFileInFolder("content", "firstPage.xml", application, page, pageModel);
		filesRemoved(application);
		filesAdded(application, pageFile);
		workspaceManager.scanApplicationDir(application, true, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		assertThat(pd.getAllItems().size()).isEqualTo(15);
		
		// Add a page client on the page to call the noop endpoint.
		// There should be 15+1 items
		PageFn fnComp = PageFn.builder().name("noop").pageName("FirstPage").service("firstClient.noop").build();
		ComponentFile fnFile = super.createComponentFileInFolder("content", "wsClients.xml", application, fnComp);
		filesRemoved(application);
		filesAdded(application, fnFile);
		workspaceManager.scanApplicationDir(application, true, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		assertThat(pd.getAllItems().size()).isEqualTo(16);
		
		// Touch the endpoint file.  Processing should still be successful.  Same number of items as previous run
		filesRemoved(application, noopServiceFile);
		filesAdded(application, noopServiceFile);
		workspaceManager.scanApplicationDir(application, true, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		assertThat(pd.getAllItems().size()).isEqualTo(16);

		// Add an endpoint that will call have a service that calls TestService.createSession();
		// There should be 16+2 items now
		Endpoint sessionEp = Endpoint.builder().functionName("init").method(HttpMethod.GET).module("FirstModule").
				operation("firstService.createSession").path("/init").response(
						Arrays.asList(Response.builder().httpStatus(200).build())
				)
				.build();
		Service sessionService = Service.builder().module("First").name("createSession").serviceOperation(Arrays.asList(
				CallRuleOperation.builder().rule("testService.createSession").result("returnValue.sessionId").build()
		)).build();
		ComponentFile initServiceFile = super.createComponentFileInFolder("server", "createSessionService.xml", application, sessionEp, sessionService);
		filesRemoved(application);
		filesAdded(application, initServiceFile);
		workspaceManager.scanApplicationDir(application, true, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		assertThat(pd.getAllItems().size()).isEqualTo(18);

		// Touch a WsClient file.  Still successful.  Same number of items as previous run.
		filesRemoved(application, clientFile);
		filesAdded(application, clientFile);
		workspaceManager.scanApplicationDir(application, true, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		assertThat(pd.getAllItems().size()).isEqualTo(18);
	}
	
	// Debug a case where a type from handwritten code disappears when I touch a content javascribe file
	@Test
	public void testHandwrittenType() throws Exception {
		ApplicationData application = super.createApplicationShell("tomcat");
		DependencyData deps = application.getDependencyData();
		ProcessingData pd = application.getProcessingData();
		Map<String,String> properties = new HashMap<>();

		properties.put("maven.build.phases", "compile");
		properties.put("maven.java.version", "17");
		properties.put("maven.deploy.phases", "package");
		properties.put("java.rootPackage", "com.test");
		properties.put("tomcat.jndi.datasource.driverClass", "driver");
		properties.put("java.webservice.pkg", "ws");
		properties.put("java.service.pkg", "service");
		properties.put("java.httpendpoint.operationResult", "result");
		properties.put("javascript.module.source", "app.js");
		properties.put("javascript.modules.srcRoot", "/content");
		properties.put("handwritten.servicelocator.package", "locator");
		properties.put("maven.dependency.tomcat-embed-core", "x:y:1.0");
		properties.put("maven.dependency.tomcat-embed-jasper", "x:y:1.0");
		properties.put("maven.dependency.tomcat-jasper", "x:y:1.0");
		properties.put("maven.dependency.tomcat-dbcp", "x:y:1.0");
		properties.put("maven.dependency.jackson-databind", "x:y:1.0");

		setProperties(application.getRootFolder(), properties);
		
		// Ensure that there is a folder server/src/main/java/pkg
		super.ensureFolder(application, "server/src/main/java/pkg");

		MavenBuild serverBuild = MavenBuild.builder().artifact("testwebapp:server:1.0").packaging("jar")
				.description("Description of a test server").build();
		serverBuild.setId("server");
		ComponentFile serverBuildFile = super.createComponentFileInFolder("server", "build.xml", application, serverBuild);

		MavenBuild contentBuild = MavenBuild.builder().artifact("testwebapp:content:1.0").packaging("pom")
				.description("Test application UI content").build();
		contentBuild.setId("content");
		ComponentFile contentBuildFile = super.createComponentFileInFolder("content", "build.xml", application, contentBuild);

		// Add a service userFile in server/src/main/java/pkg/TestService.java
		// Number of items should stay the same (5)
		final String testServiceContent = """
				package pkg;
				@net.sf.javascribe.patterns.java.handwritten.BusinessService(group = "Service", ref = "testService", priority = 20000)
				public class TestService {
				@net.sf.javascribe.patterns.java.handwritten.BusinessRule
				public String createSession() {
				return "abc123";
				}
				}
				""";
		UserFile userFile = super.createUserFile("TestService.java", "/server/src/main/java/pkg/TestService.java", testServiceContent);

		EmbedTomcatJar jarComp = EmbedTomcatJar.builder().jarName("test").port(123).pkg("test").context("/").build();
		TomcatJndiDatasource dsComp = TomcatJndiDatasource.builder().username("usr").password("pwd").url("url")
				.build();
		ComponentFile platformCf = super.createComponentFileInFolder("server", "test.xml", application, jarComp, dsComp);

		WebServiceModuleComponent wsModule = WebServiceModuleComponent.builder().name("FirstModule").uri("/first").build();
		ComponentFile moduleFile = super.createComponentFileInFolder("server", "wsModule.xml", application, wsModule);

		Endpoint noopEndpoint = Endpoint.builder().functionName("noop").method(HttpMethod.GET).module("FirstModule").
				operation("firstService.noop").path("/noop").response(
						Arrays.asList(Response.builder().httpStatus(200).build())
				)
				.build();
		Service service = Service.builder().module("First").name("noop").serviceOperation(new ArrayList<>()).build();
		ComponentFile noopServiceFile = super.createComponentFileInFolder("server", "service.xml", application, noopEndpoint, service);

		WsClients clientsComp = WsClients.builder().urlPrefix("/path").buildId("server").moduleClient(Arrays.asList(
				ModuleClient.builder().module("FirstModule").name("FirstClient").ref("firstClient").build())).build();
		ComponentFile clientFile = super.createComponentFileInFolder("content", "client.xml", application, clientsComp);

		Page page = Page.builder().name("FirstPage").build();
		PageModel pageModel = PageModel.builder().pageName("FirstPage").properties("name:string").build();
		ComponentFile pageFile = super.createComponentFileInFolder("content", "firstPage.xml", application, page, pageModel);

		PageFn fnComp = PageFn.builder().name("noop").pageName("FirstPage").service("firstClient.noop").build();
		ComponentFile fnFile = super.createComponentFileInFolder("content", "wsClients.xml", application, fnComp);
		Endpoint sessionEp = Endpoint.builder().functionName("init").method(HttpMethod.GET).module("FirstModule").
				operation("firstService.createSession").path("/init").response(
						Arrays.asList(Response.builder().httpStatus(200).build())
				)
				.build();
		Service sessionService = Service.builder().module("First").name("createSession").serviceOperation(Arrays.asList(
				CallRuleOperation.builder().rule("testService.createSession").result("returnValue.sessionId").build()
		)).build();
		ComponentFile initServiceFile = super.createComponentFileInFolder("server", "createSessionService.xml", application, sessionEp, sessionService);

		filesRemoved(application);
		filesAdded(application, serverBuildFile, contentBuildFile, userFile, platformCf, moduleFile, noopServiceFile, clientFile, pageFile, 
				fnFile, initServiceFile);
		workspaceManager.scanApplicationDir(application, true, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		assertThat(pd.getAllItems().size()).isEqualTo(18);
		
		// Reset the input stream of the user file since it will be parsed again
		userFile.getInputStream().reset();

		// Touch a WsClient file.  Still successful.  Same number of items as previous run.
		filesRemoved(application, clientFile);
		filesAdded(application, clientFile);
		workspaceManager.scanApplicationDir(application, true, false);
		assertThat(application.getState()).isEqualTo(ProcessingState.SUCCESS);
		assertThat(pd.getAllItems().size()).isEqualTo(18);
	}
	
}

