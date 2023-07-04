package net.sf.javascribe.engine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.javascribe.engine.manager.OutputManager;
import net.sf.javascribe.engine.manager.PluginManager;
import net.sf.javascribe.engine.manager.WorkspaceManager;
import net.sf.javascribe.engine.service.ComponentFileService;
import net.sf.javascribe.engine.service.FolderScannerService;
import net.sf.javascribe.engine.service.LanguageSupportService;
import net.sf.javascribe.engine.service.OutputService;
import net.sf.javascribe.engine.service.PatternService;
import net.sf.javascribe.engine.service.PluginService;
import net.sf.javascribe.engine.service.ProcessingService;
import net.sf.javascribe.engine.util.ConfigUtil;
import net.sf.javascribe.engine.util.DependencyUtil;
import net.sf.javascribe.engine.util.FileUtil;
import net.sf.javascribe.engine.util.LogUtil;
import net.sf.javascribe.engine.util.OutputUtil;
import net.sf.javascribe.engine.util.ProcessingUtil;

public class ComponentContainer {

	// Instance variables
	Map<String,Object> components = new HashMap<>();
	List<String> initialized = new ArrayList<>();

	// Singleton methods.
	private static ComponentContainer instance = new ComponentContainer();
	private ComponentContainer() {
	}
	public static ComponentContainer get() {
		return instance;
	}

	public void registerServices() {
		ProcessingService processingService = new ProcessingService();

		// Register utility classes with engine container
		ComponentContainer.get().registerComponent(new FileUtil());
		ComponentContainer.get().registerComponent(new ProcessingUtil());
		ComponentContainer.get().registerComponent(new DependencyUtil());
		ComponentContainer.get().registerComponent(new OutputUtil());
		ComponentContainer.get().registerComponent(new LogUtil());
		ComponentContainer.get().registerComponent(new ConfigUtil());
		
		// Register services with engine container
		ComponentContainer.get().registerComponent(new FolderScannerService());
		ComponentContainer.get().registerComponent(new ComponentFileService());
		ComponentContainer.get().registerComponent(new PluginService());
		ComponentContainer.get().registerComponent(new LanguageSupportService());
		ComponentContainer.get().registerComponent(new PatternService());
		ComponentContainer.get().registerComponent(processingService);
		ComponentContainer.get().registerComponent(new OutputService());
		ComponentContainer.get().registerComponent(new FolderScannerService());
	
		// Register manager classes with engine container
		ComponentContainer.get().registerComponent(new PluginManager());
		ComponentContainer.get().registerComponent(new WorkspaceManager());
		ComponentContainer.get().registerComponent(new OutputManager());

		ComponentContainer.get().setComponent("ProcessingContextOperations", processingService);
	}

	public void registerComponent(String identifier, Object obj) {
		components.put(identifier, obj);
	}

	public void registerComponent(Object obj) {
		Class<?> cl = obj.getClass();
		String identifier = cl.getSimpleName();
		components.put(identifier, obj);
	}

	// For use only in tests.
	// This explicitly sets the component specified and marks it initialized.
	public void setComponent(String identifier, Object ob) {
		components.put(identifier, ob);
		initialized.add(identifier);
	}
	// For use only in unit tests.
	// Clears out the registered components
	public void reset() {
		components.clear();
		initialized.clear();
	}

	public <T extends Object> T getComponent(Class<T> clazz) throws EngineInitException {
		return getComponent(clazz.getSimpleName(), clazz);
	}

	@SuppressWarnings("unchecked")
	public <T extends Object> T getComponent(String identifier, Class<T> clazz) throws EngineInitException {
		Object obj = components.get(identifier);

		if (obj==null) {
			throw new EngineInitException("Couldn't find dependency "+identifier);
		}
		if (!initialized.contains(identifier)) {
			// do dependency injection
			initialized.add(identifier);
			init(obj);
		}

		return (T)obj;
	}

	private void init(Object ob) throws EngineInitException {
		Class<?> cl = ob.getClass();
		
		for(Method method : cl.getMethods()) {
			if (method.getAnnotation(ComponentDependency.class)!=null) {
				if (method.getParameterCount() == 1) {
					ComponentDependency dep = method.getAnnotation(ComponentDependency.class);
					String identifier = dep.name();
					Class<?> arg = method.getParameters()[0].getType();
					if (identifier.trim().length()==0) {
						identifier = arg.getSimpleName();
					}
					try {
						Object dependency = getComponent(identifier, arg);
						if (dependency!=null) {
							method.invoke(ob, dependency);
						} else {
							throw new EngineInitException("Couldn't load required dependency "+identifier);
						}
					} catch(InvocationTargetException | IllegalAccessException e) {
						throw new EngineInitException("Couldn't initialize dependencies for "+ob.getClass().getCanonicalName(), e);
					}
				}
			}
		}
	}

}

