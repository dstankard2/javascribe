package net.sf.javascribe.engine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

