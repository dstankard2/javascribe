package net.sf.javascribe.patterns.test.userfiles;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.resources.ApplicationFile;
import net.sf.javascribe.api.resources.FolderWatcher;
import net.sf.javascribe.api.types.ServiceOperation;
import net.sf.javascribe.langsupport.java.types.impl.JavaServiceType;

public class FirstFolderWatcher implements FolderWatcher {
	String typeName = null;
	public FirstFolderWatcher(String name) {
		this.typeName = name;
	}

	@Override
	public void process(ProcessorContext ctx, ApplicationFile changedFile) throws JavascribeException {
		JavaServiceType type = null;
		String filename = changedFile.getName();
		int index = filename.indexOf('.');
		String rulename = filename.substring(0, index);
		String im = "pkg."+typeName;

		ctx.setLanguageSupport("Java8");

		type = JavascribeUtils.getType(JavaServiceType.class, typeName, ctx);
		if (type==null) {
			type = new JavaServiceType(typeName, im, ctx.getBuildContext());
			String attrib = JavascribeUtils.getLowerCamelName(typeName);
			ctx.addSystemAttribute(attrib, typeName);
			ctx.addVariableType(type);
		}

		ServiceOperation op = new ServiceOperation(rulename);
		type.addOperation(op);
		ctx.modifyVariableType(type);

		/*
		type = JavascribeUtils.getType(JavaDataObjectType.class, name, ctx);
		if (type == null) {
			type = new JavaDataObjectType(name, "import", ctx.getBuildContext());
			type.addProperty("name", "string");
			ctx.addVariableType(type);
		}
		*/

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

