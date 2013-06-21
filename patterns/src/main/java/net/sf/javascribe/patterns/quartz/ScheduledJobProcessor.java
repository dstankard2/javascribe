package net.sf.javascribe.patterns.quartz;

import java.util.HashMap;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.GeneratorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.langsupport.java.JavaOperation;
import net.sf.javascribe.langsupport.java.JavaServiceObjectType;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.JavaVariableTypeImpl;
import net.sf.javascribe.langsupport.java.jsom.JavascribeJavaCodeSnippet;
import net.sf.javascribe.langsupport.java.jsom.JavascribeVariableTypeResolver;
import net.sf.javascribe.langsupport.java.jsom.JsomJava5TypeImpl;
import net.sf.javascribe.langsupport.java.jsom.JsomUtils;
import net.sf.javascribe.patterns.CorePatternConstants;
import net.sf.javascribe.patterns.servlet.WebUtils;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5CompatibleCodeSnippet;
import net.sf.jsom.java5.Java5DeclaredMethod;
import net.sf.jsom.java5.Java5SourceFile;

@Scannable
@Processor
public class ScheduledJobProcessor {

	public static final String SCHEDULED_JOB_PKG = "net.sf.javascribe.patterns.quartz.ScheduledJob.pkg";

	@ProcessorMethod(componentClass=ScheduledJob.class)
	public void process(ScheduledJob job,GeneratorContext ctx) throws JavascribeException {
		Java5SourceFile processFile = null;
		Java5SourceFile listenerFile = null;
		String pkg = null;
		Java5DeclaredMethod initMethod = null;
		
		ctx.setLanguageSupport("Java");
		
		if (job.getName()==null) {
			throw new JavascribeException("Found scheduled process without name");
		}
		try {
			System.out.println("Processing scheduled process '"+job.getName()+"'");
			addQuartzTypes(ctx);
			WebUtils.addHttpTypes(ctx);
			pkg = JavaUtils.findPackageName(ctx, ctx.getRequiredProperty(SCHEDULED_JOB_PKG));

			listenerFile = JsomUtils.getJavaFile(pkg+".ScheduledProcessContextListener", ctx);
			if (listenerFile==null) {
				listenerFile = new Java5SourceFile(new JavascribeVariableTypeResolver(ctx.getTypes()));
				listenerFile.setPackageName(pkg);
				listenerFile.getPublicClass().setClassName("ScheduledProcessContextListener");
				WebUtils.addContextListener(ctx, pkg+".ScheduledProcessContextListener");
				JsomUtils.addJavaFile(listenerFile, ctx);
				listenerFile.getPublicClass().addImplementedInterface("javax.servlet.ServletContextListener");
				initMethod = new Java5DeclaredMethod(new JavascribeVariableTypeResolver(ctx.getTypes()));
				initMethod.setMethodName("contextInitialized");
				initMethod.addArg("ServletContextEvent", "_event");
				Java5CodeSnippet code = new Java5CodeSnippet();
				initMethod.setMethodBody(code);
				code.addImport("org.quartz.SchedulerFactory");
				code.addImport("org.quartz.impl.StdSchedulerFactory");
				code.append("try {\n");
				code.append("SchedulerFactory _factory = new StdSchedulerFactory();\n");
				code.append("_scheduler = _factory.getScheduler();\n");
				listenerFile.getPublicClass().addMethod(initMethod);
				listenerFile.getPublicClass().addMemberVariable("_scheduler", "Scheduler", null);

				Java5DeclaredMethod destroyMethod = new Java5DeclaredMethod(new JavascribeVariableTypeResolver(ctx.getTypes()));
				code = new Java5CodeSnippet();
				destroyMethod.setMethodName("contextDestroyed");
				destroyMethod.addArg("ServletContextEvent", "_event");
				destroyMethod.setMethodBody(code);
				code.append("if (_scheduler!=null) {\n");
				code.append("try {\n_scheduler.shutdown(true);\n} catch(Throwable e) { e.printStackTrace(); }\n");
				code.append("}\n");
				listenerFile.getPublicClass().addMethod(destroyMethod);

				// Add component for scheduled job finalizer
				ctx.addObject("ListenerFile", listenerFile);
				ctx.addComponent(new ScheduledJobDone());
			} else {
				initMethod = (Java5DeclaredMethod)listenerFile.getPublicClass().getDeclaredMethod("contextInitialized");
			}

			// Add job to contextInitialized method of context listener
			try {
				Java5CompatibleCodeSnippet code = initMethod.getMethodBody();
				code.addImport("org.quartz.JobDetail");
				code.addImport("org.quartz.CronTrigger");
				code.append("JobDetail "+job.getName()+"_Detail = new JobDetail(\""+job.getName()+"\",\"Default\","+job.getName()+".class);\n");
				code.append("CronTrigger "+job.getName()+"_Trigger = new CronTrigger(\""+job.getName()+"\",\"Default\",\""+job.getCronString()+"\");\n");
				code.append("_scheduler.scheduleJob("+job.getName()+"_Detail,"+job.getName()+"_Trigger);\n");

				// Add type for JobExecutionContext if it's not there
				if (ctx.getTypes().getType("JobExecutionContext")==null) {
					ctx.getTypes().addType(new JavaVariableTypeImpl("JobExecutionContext","org.quartz.JobExecutionContext","JobExecutionContext"));
				}

				processFile = JsomUtils.createJavaSourceFile(ctx);
				processFile.setPackageName(pkg);
				processFile.getPublicClass().setClassName(job.getName());
				JsomUtils.addJavaFile(processFile, ctx);
				processFile.getPublicClass().addImplementedInterface("org.quartz.Job");
				Java5DeclaredMethod exec = new Java5DeclaredMethod(new JavascribeVariableTypeResolver(ctx.getTypes()));
				exec.setMethodName("execute");
				exec.addArg("JobExecutionContext", "_ctx");
				processFile.getPublicClass().addMethod(exec);
				code = new Java5CodeSnippet();
				exec.setMethodBody(code);
				code.append("System.out.println(\"Running scheduled Process '"+job.getName()+"'\");\n");
				CodeExecutionContext execCtx = new CodeExecutionContext(null,ctx.getTypes());

				String objName = JavascribeUtils.getObjectName(job.getRule());
				String ruleName = JavascribeUtils.getRuleName(job.getRule());
				String objInst = JavascribeUtils.getLowerCamelName(objName);

				JavaServiceObjectType obj = (JavaServiceObjectType)ctx.getType(objName);
				if (obj==null) {
					throw new CodeGenerationException("Couldn't find business object type '"+objName+"'");
				}
				JavaOperation op = obj.getMethod(ruleName);
				if (op==null) {
					throw new CodeGenerationException("Couldn't find business rule '"+objName+"."+ruleName+"'");
				}
				code.merge(new JavascribeJavaCodeSnippet(obj.declare(objInst)));
				code.merge(new JavascribeJavaCodeSnippet(obj.instantiate(objInst, null)));
				HashMap<String,String> params = new HashMap<String,String>();
				if ((job.getParams()!=null) && (job.getParams().trim().length()>0)) {
					params = JavascribeUtils.readParameters(ctx, job.getParams());
				}
				code.append(JavaUtils.callJavaOperation(null, objInst, op, execCtx, params));
			} catch(CodeGenerationException e) {
				throw new JavascribeException("JSOM Exception while processing scheduled job",e);
			}
		} catch(CodeGenerationException e) {
			throw new JavascribeException("JSOM exception in processing",e);
		}
	}

	private void addQuartzTypes(GeneratorContext ctx) throws JavascribeException {
		if (ctx.getTypes().getType("Scheduler")==null) {
			ctx.getTypes().addType(new JsomJava5TypeImpl("Scheduler","org.quartz.Scheduler","Scheduler"));
		}

	}

}

class ScheduledJobDone extends ComponentBase {

	public int getPriority() {
		return CorePatternConstants.PRIORITY_SCHEDULED_JOB+1;
	}

}

