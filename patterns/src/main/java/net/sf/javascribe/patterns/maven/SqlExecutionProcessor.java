package net.sf.javascribe.patterns.maven;

import java.util.List;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.resources.ApplicationFile;
import net.sf.javascribe.api.resources.ApplicationFolder;
import net.sf.javascribe.api.resources.ApplicationResource;
import net.sf.javascribe.patterns.xml.maven.SqlExecution;

@Plugin
public class SqlExecutionProcessor implements ComponentProcessor<SqlExecution> {

	@Override
	public void process(SqlExecution comp, ProcessorContext ctx) throws JavascribeException {
		String id = comp.getId();
		String sqlDir = comp.getResource();
		MavenBuildContext mctx = MavenUtils.getMavenBuildContext(ctx);
		PluginConfig pl = mctx.getPlugin("org.codehaus.mojo:sql-maven-plugin");
		
		if (pl==null) {
			pl = new PluginConfig("org.codehaus.mojo:sql-maven-plugin:1.5");
			mctx.addPlugin(pl);
			// TODO: Remove hard-coded dependency on MySQL
			pl.getDependencies().add("mysql:mysql-connector-java:5.1.6");
			pl.getConfiguration().addProperty("driver", "com.mysql.jdbc.Driver");
			pl.getConfiguration().addProperty("onError", "continue");
			pl.getConfiguration().addProperty("url", comp.getJdbcUrl());
			pl.getConfiguration().addProperty("username", comp.getUsername());
			pl.getConfiguration().addProperty("password", comp.getPassword());
		}
		
		if (sqlDir.trim().length()==0) {
			throw new JavascribeException("Sql pattern requires XML attribute 'resource'");
		}
		
		ExecutionConfig exec = new ExecutionConfig();
		pl.getExecutions().add(exec);
		exec.setId(id);
		exec.getGoals().add("execute");
		exec.setPhase("package");
		mctx.addBuildPhase("package");
		exec.getConfiguration().addProperty("autocommit", "true");
		PropertyWithValueList files = exec.getConfiguration().addPropertyValueList("srcFiles", "srcFile");
		ApplicationResource res = ctx.getResource(sqlDir);
		if (res instanceof ApplicationFolder) {
			// Recursively search this folder and subfolder for SQL files
			ApplicationFolder folder = (ApplicationFolder)res;
			addFolder(folder,files,mctx,ctx);
		} else if (res instanceof ApplicationFile) {
			ApplicationFile file = (ApplicationFile)res;
			if (file.getName().endsWith(".sql")) {
				addFile(file.getPath(),files,mctx,ctx);
			}
		}
	}
	
	protected void addFolder(ApplicationFolder folder,PropertyWithValueList files,MavenBuildContext bctx,ProcessorContext ctx) {
		List<String> names = folder.getContentNames();
		//ctx.dependOnResource(folder.getPath());
		for(String name : names) {
			ApplicationResource res = folder.getResource(name);
			if (res instanceof ApplicationFile) {
				ApplicationFile file = (ApplicationFile)res;
				addFile(file.getPath(), files, bctx,ctx);
			} else if (res instanceof ApplicationFolder) {
				ApplicationFolder f = (ApplicationFolder)res;
				addFolder(f, files,bctx,ctx);
			}
		}
	}
	
	protected void addFile(String path,PropertyWithValueList files, MavenBuildContext bctx,ProcessorContext ctx) {
		if (path.endsWith(".sql")) {
			//ctx.dependOnResource(path);
			String buildBase = bctx.getApplicationFolderPath();
			String resourcePath = path.substring(buildBase.length());
			files.addValue(resourcePath);
		}
	}

}
