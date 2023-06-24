package net.sf.javascribe.engine.integration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mockito.Mockito;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;

import net.sf.javascribe.api.BuildComponentProcessor;
import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.engine.ComponentContainer;
import net.sf.javascribe.engine.EngineProperties;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
import net.sf.javascribe.engine.service.EngineResources;
import net.sf.javascribe.engine.service.PluginService;

@SuppressWarnings({ "rawtypes" })
public abstract class ContainerTest {

	File tempDir;
	
	private List<Object> mocks = new ArrayList<>();

	Map<String,String> engineOptions = new HashMap<>();
	
	private Set<Class<Component>> componentClasses = new HashSet<>();
	private Set<Class<ComponentProcessor>> componentProcessorClasses = new HashSet<>();
	private Set<Class<BuildComponentProcessor>> buildComponentProcessorClasses = new HashSet<>();

	protected PluginService pluginService = null;

	protected void includePattern(Class<Component> cl) {
		componentClasses.add(cl);
	}

	protected void includeBuildProcessor(Class<BuildComponentProcessor> cl) {
		buildComponentProcessorClasses.add(cl);
	}

	protected void includeProcessor(Class<ComponentProcessor> cl) {
		componentProcessorClasses.add(cl);
	}

	@AfterClass
	public void teardown() {
	}
	
	@BeforeTest
	public void setupTest() {
		mocks.forEach(mock -> {
			Mockito.reset(mock);
		});
	}
	
	@BeforeClass
	public void setupClass() throws Exception {
		ComponentContainer container = ComponentContainer.get();

		Path path = Files.createTempDirectory("jstest");
		tempDir = path.toFile();
		tempDir.deleteOnExit();
		
		engineOption("debug", "true");
		ComponentContainer.get().setComponent("debug", true);
		ComponentContainer.get().setComponent("jarFiles", new File[0]);
		ComponentContainer.get().registerComponent(new EngineResources());
		
		container.registerServices();

		// Initialize plugin service to return classes we've asked for
		pluginService = Mockito.mock(PluginService.class);
		Mockito.when(pluginService.findClassesThatExtend(Component.class)).thenReturn(componentClasses);
		Mockito.when(pluginService.findClassesThatExtend(ComponentProcessor.class))
				.thenReturn(componentProcessorClasses);
		Mockito.when(pluginService.findClassesThatExtend(BuildComponentProcessor.class))
				.thenReturn(buildComponentProcessorClasses);
		ComponentContainer.get().registerComponent("PluginService", pluginService);

		Field[] fields = this.getClass().getDeclaredFields();

		for (Field field : fields) {
			if (field.isAnnotationPresent(MockDependency.class)) {
				Class<?> fieldType = field.getType();
				Object obj = Mockito.mock(fieldType);
				ComponentContainer.get().registerComponent(fieldType.getSimpleName(), obj);
				mocks.add(obj);
				field.setAccessible(true);
				field.set(this, obj);
			} else if (field.isAnnotationPresent(Inject.class)) {
				Class<?> fieldType = field.getType();
				Object obj = ComponentContainer.get().getComponent(fieldType);
				field.setAccessible(true);
				field.set(this, obj);
			}
		}
	}

	protected void engineOption(String name, String value) {
		engineOptions.put(name, value);
		EngineProperties properties = new EngineProperties(engineOptions);
		ComponentContainer.get().setComponent("EngineProperties", properties);
	}

	protected ApplicationFolderImpl createRootFolder(String appName, ApplicationData application) throws IOException {
		ApplicationFolderImpl ret = null;
		File appDir = new File(tempDir, appName);

		ret = new ApplicationFolderImpl(appDir, application);
		
		return ret;
	}
}

