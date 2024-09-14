package net.sf.javascribe.patterns.test.userfiles;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.types.impl.JavaServiceType;

public class FolderWatchingProcessor implements ComponentProcessor<FolderWatchingPattern> {

	@Override
	public void process(FolderWatchingPattern component, ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Java8");
		String path = component.getPath();
		String serviceName = component.getServiceName();
		String objName = component.getDataObjectName();
		FirstFolderWatcher watcher = new FirstFolderWatcher(serviceName, objName);
		if (component.getDependsOn()!=null) {
			if (JavascribeUtils.getType(JavaServiceType.class, component.getDependsOn(), ctx)==null) {
				throw new JavascribeException("Couldn't find type dependency "+component.getDependsOn());
			}
		}
		ctx.addFolderWatcher(path, watcher);
	}

}

