package net.sf.javascribe.test.integration;

import java.io.File;
import java.lang.reflect.Field;
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
import net.sf.javascribe.api.langsupport.LanguageSupport;
import net.sf.javascribe.engine.ComponentContainer;
import net.sf.javascribe.engine.EngineProperties;
import net.sf.javascribe.engine.service.EngineResources;
import net.sf.javascribe.engine.service.PluginService;

/**
 * An abstract test class which will interact with the Javascribe component container and enable the use 
 * of @Inject and @MockDependency.
 */
@SuppressWarnings({ "rawtypes" })
public abstract class ContainerTest {

	private List<Object> mocks = new ArrayList<>();

	Map<String,String> engineOptions = new HashMap<>();
	
	private Set<Class<Component>> componentClasses = new HashSet<>();
	private Set<Class<ComponentProcessor>> componentProcessorClasses = new HashSet<>();
	private Set<Class<BuildComponentProcessor>> buildComponentProcessorClasses = new HashSet<>();
	private Set<Class<LanguageSupport>> languageSupportClasses = new HashSet<>();

	protected PluginService pluginService = null;

	protected void includePattern(Class<? extends Component> cl) {
		componentClasses.add((Class<Component>)cl);
	}

	protected void includeBuildProcessor(Class<BuildComponentProcessor> cl) {
		buildComponentProcessorClasses.add(cl);
	}

	protected void includeProcessor(Class<? extends ComponentProcessor> cl) {
		componentProcessorClasses.add((Class<ComponentProcessor>)cl);
	}

	protected void includeLanguageSupport(Class<? extends LanguageSupport> cl) {
		languageSupportClasses.add((Class<LanguageSupport>)cl);
	}

	@AfterClass
	public void afterContainerTest() {
	}
	
	@BeforeTest
	public void setupContainerTestMethod() {
		mocks.forEach(mock -> {
			Mockito.reset(mock);
		});
	}
	
	@BeforeClass
	public void setupContainerTest() throws Exception {
		ComponentContainer container = ComponentContainer.get();

		engineOption("debug", "true");
		ComponentContainer.get().setComponent("debug", true);
		ComponentContainer.get().setComponent("jarFiles", new File[0]);
		ComponentContainer.get().registerComponent(new EngineResources());
		
		container.registerServices();

		// Initialize plugin service to return classes we've asked for
		// We'll re-use the plugin service between multiple classes
		pluginService = Mockito.mock(PluginService.class);
		Mockito.when(pluginService.findClassesThatExtend(Component.class)).thenReturn(componentClasses);
		Mockito.when(pluginService.findClassesThatExtend(ComponentProcessor.class))
				.thenReturn(componentProcessorClasses);
		Mockito.when(pluginService.findClassesThatExtend(BuildComponentProcessor.class))
				.thenReturn(buildComponentProcessorClasses);
		Mockito.when(pluginService.findClassesThatExtend(LanguageSupport.class)).thenReturn(languageSupportClasses);
		ComponentContainer.get().registerComponent("PluginService", pluginService);

		// Look at fields in this test.  Find the mock dependencies and set them in the 
		// componentContainer.  Record all the injected dependencies.
		Field[] fields = this.getClass().getDeclaredFields();
		List<Field> injected = new ArrayList<>();
		for (Field field : fields) {
			if (field.isAnnotationPresent(MockDependency.class)) {
				Class<?> fieldType = field.getType();
				Object obj = Mockito.mock(fieldType);
				ComponentContainer.get().registerComponent(fieldType.getSimpleName(), obj);
				mocks.add(obj);
				field.setAccessible(true);
				field.set(this, obj);
			} else if (field.isAnnotationPresent(Inject.class)) {
				injected.add(field);
			}
		}
		
		for(Field field : injected) {
			Class<?> fieldType = field.getType();
			Object obj = ComponentContainer.get().getComponent(fieldType);
			field.setAccessible(true);
			field.set(this, obj);
		}
	}

	protected void engineOption(String name, String value) {
		engineOptions.put(name, value);
		EngineProperties properties = new EngineProperties(engineOptions);
		ComponentContainer.get().setComponent("EngineProperties", properties);
	}

}

