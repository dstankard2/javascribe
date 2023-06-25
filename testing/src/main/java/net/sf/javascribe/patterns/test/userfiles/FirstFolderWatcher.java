package net.sf.javascribe.patterns.test.userfiles;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.resources.ApplicationFile;
import net.sf.javascribe.api.resources.FolderWatcher;
import net.sf.javascribe.langsupport.java.types.JavaVariableType;
import net.sf.javascribe.langsupport.java.types.impl.JavaDataObjectType;

public class FirstFolderWatcher implements FolderWatcher {

	@Override
	public void process(ProcessorContext ctx, ApplicationFile changedFile) throws JavascribeException {
		ctx.setLanguageSupport("Java8");
		JavaDataObjectType type = null;
		
		type = JavascribeUtils.getType(JavaDataObjectType.class, "TestDataObject", ctx);
		if (type == null) {
			type = new JavaDataObjectType("TestDataObject", "import", ctx.getBuildContext());
			type.addProperty("name", "string");
			ctx.addVariableType(type);
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
