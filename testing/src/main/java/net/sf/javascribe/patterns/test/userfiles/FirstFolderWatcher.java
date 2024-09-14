package net.sf.javascribe.patterns.test.userfiles;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.resources.ApplicationFile;
import net.sf.javascribe.api.resources.FolderWatcher;
import net.sf.javascribe.api.types.ServiceOperation;
import net.sf.javascribe.langsupport.java.types.impl.JavaDataObjectType;
import net.sf.javascribe.langsupport.java.types.impl.JavaServiceType;

public class FirstFolderWatcher implements FolderWatcher {
	String serviceName = null;
	String dataObjectName = null;
	
	public FirstFolderWatcher(String serviceName, String dataObjectName) {
		this.serviceName = serviceName;
		this.dataObjectName = dataObjectName;
	}

	@Override
	public void process(ProcessorContext ctx, ApplicationFile changedFile) throws JavascribeException {
		JavaServiceType serviceType = null;
		String filename = changedFile.getName();
		int index = filename.indexOf('.');
		String rulename = filename.substring(0, index);
		String im = "pkg."+serviceName;

		ctx.setLanguageSupport("Java8");

		serviceType = JavascribeUtils.getType(JavaServiceType.class, serviceName, ctx);
		if (serviceType==null) {
			serviceType = new JavaServiceType(serviceName, im, ctx.getBuildContext());
			String attrib = JavascribeUtils.getLowerCamelName(serviceName);
			ctx.addSystemAttribute(attrib, serviceName);
			ctx.addVariableType(serviceType);
		} else {
			ctx.modifyVariableType(serviceType);
		}
		
		ServiceOperation op = new ServiceOperation(rulename);
		serviceType.addOperation(op);

		JavaDataObjectType objectType = JavascribeUtils.getType(JavaDataObjectType.class, dataObjectName, ctx);
		if (objectType == null) {
			im = "pkg."+dataObjectName;
			objectType = new JavaDataObjectType(dataObjectName, im, ctx.getBuildContext());
			objectType.addProperty("name", "string");
			ctx.addVariableType(objectType);
		}

		StringBuilder contents = new StringBuilder();

		try {
			InputStreamReader sreader = new InputStreamReader(changedFile.getInputStream());
			BufferedReader reader = new BufferedReader(sreader);
			while (reader.ready()) {
				contents.append(reader.readLine()).append('\n');
			}
		} catch(Exception e) {
			ctx.getLog().error("Couldn't read user file", e);
		}
		ctx.getLog().info("Reading "+contents.length()+" characters from user file "+changedFile.getPath());
	}

	@Override
	public String getName() {
		return "FirstFolderWatcher";
	}

	@Override
	public int getPriority() {
		return 100;
	}

}

