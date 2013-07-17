package net.sf.javascribe.patterns.quartz;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.jsom.java5.Java5CompatibleCodeSnippet;
import net.sf.jsom.java5.Java5DeclaredMethod;
import net.sf.jsom.java5.Java5SourceFile;

@Scannable
@Processor
public class ScheduledJobFinalizer {
	
	@ProcessorMethod(componentClass=ScheduledJobDone.class)
	public void process(ProcessorContext ctx) throws JavascribeException {
		Java5SourceFile listenerFile = null;
		Java5DeclaredMethod initMethod = null;

		System.out.println("Finalizing scheduled jobs");
		listenerFile = (Java5SourceFile)ctx.getObject("ListenerFile");
		initMethod = (Java5DeclaredMethod)listenerFile.getPublicClass().getDeclaredMethod("contextInitialized");
		Java5CompatibleCodeSnippet code = initMethod.getMethodBody();
		
		code.append("_scheduler.start();\n");
		code.append("System.out.println(\"*** Quartz scheduler started ***\");\n");
		code.append("} catch(Throwable e) {\ne.printStackTrace();\n}\n");
	}

}
