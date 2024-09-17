package net.sf.javascribe.test.integration;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
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

	protected ArrayList<Object> mocks = new ArrayList<>();

	Map<String,String> engineOptions = new HashMap<>();
	
	private Set<Class<?>> pluginClasses = new HashSet<>();

	protected PluginService pluginService = null;

	protected void includePattern(Class<? extends Component> cl) {
		includePlugin(cl);
	}

	@SuppressWarnings("unchecked")
	protected void includePatterns(Class<? extends Component>... classes) {
		for(Class<? extends Component> cl : classes) {
			includePattern(cl);
		}
	}

	protected void includeBuildProcessor(Class<? extends BuildComponentProcessor> cl) {
		includePlugin(cl);
	}

	protected void includeProcessor(Class<? extends ComponentProcessor> cl) {
		includePlugin(cl);
	}

	@SuppressWarnings("unchecked")
	protected void includeProcessors(Class<? extends ComponentProcessor>... classes) {
		for(Class<? extends ComponentProcessor> cl : classes) {
			includeProcessor(cl);
		}
	}

	protected void includeLanguageSupport(Class<? extends LanguageSupport> cl) {
		includePlugin(cl);
	}

	protected void includePlugin(Class<?> cl) {
		this.pluginClasses.add(cl);
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
	
	@SuppressWarnings("unchecked")
	@BeforeClass
	public void setupContainerTest() throws Exception {
		final ContainerTest that = this;
		ComponentContainer container = ComponentContainer.get();

		engineOption("test", "test");
		engineOption("debug", "false");
		ComponentContainer.get().setComponent("debug", false);

		ComponentContainer.get().setComponent("jarFiles", new File[0]);
		ComponentContainer.get().registerComponent(new EngineResources());
		
		container.registerServices();

		// Initialize plugin service to return classes we've asked for
		// We'll re-use the plugin service between multiple classes
		pluginService = Mockito.mock(PluginService.class);
		ComponentContainer.get().registerComponent("PluginService", pluginService);
		
		Answer<Set<Class<?>>> annotationPluginAnswer = new Answer<Set<Class<?>>>() {
			public Set<Class<?>> answer(InvocationOnMock i) {
				Class<? extends Annotation> arg = i.getArgument(0, Class.class);
				Set<Class<?>> ret = that.pluginClasses.stream().filter(cl -> 
					cl.isAnnotationPresent(arg)).collect(Collectors.toSet());
				return ret;
			}
		};
		Mockito.when(pluginService.findClassesWithAnnotation(Mockito.any())).thenAnswer(annotationPluginAnswer);

		Answer<Set<Class<?>>> superclassPluginAnswer = new Answer<Set<Class<?>>>() {
			public Set<Class<?>> answer(InvocationOnMock i) {
				Class<?> arg = i.getArgument(0, Class.class);
				Set<Class<?>> ret = that.pluginClasses.stream().filter(cl -> 
					arg.isAssignableFrom(cl)).collect(Collectors.toSet());
				return ret;
			}
		};
		Mockito.when(pluginService.findClassesThatExtend(Mockito.any())).thenAnswer(superclassPluginAnswer);

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

