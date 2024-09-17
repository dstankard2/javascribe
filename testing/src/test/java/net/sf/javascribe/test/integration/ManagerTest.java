package net.sf.javascribe.test.integration;

import java.io.File;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;

import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.api.config.ComponentSet;
import net.sf.javascribe.api.logging.ProcessorLogLevel;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
import net.sf.javascribe.engine.data.files.ComponentFile;
import net.sf.javascribe.engine.data.files.JavascribePropertiesFile;
import net.sf.javascribe.engine.data.files.UserFile;
import net.sf.javascribe.engine.data.processing.ProcessorLog;
import net.sf.javascribe.engine.util.LogUtil;

public abstract class ManagerTest extends ContainerTest {

	protected File tempDir;
	LogUtil logUtil;

	@BeforeClass
	public void setupManagerTest() throws IOException {
		Path path = Files.createTempDirectory("jstest");
		tempDir = path.toFile();
		tempDir.deleteOnExit();
		this.logUtil = new LogUtil();
	}

	protected ApplicationData createApplicationShell(String name) {
		ApplicationData application = null;
		File appDir = new File(tempDir, name);
		application = ApplicationData.builder()
				.applicationDirectory(appDir)
				.name(name)
				.build();
		appDir.mkdirs();

		ApplicationFolderImpl folder = new ApplicationFolderImpl(appDir, application);
		application.setRootFolder(folder);
		ProcessorLog log = new ProcessorLog(name, application, ProcessorLogLevel.INFO);
		application.setApplicationLog(log);
		
		return application;
	}
	
	@SuppressWarnings("deprecation")
	protected UserFile createUserFile(String name, String path, String content) throws IOException {
		StringBufferInputStream in = new StringBufferInputStream(content);
		UserFile uf = Mockito.mock(UserFile.class);
		Mockito.when(uf.getPath()).thenReturn(path);
		Mockito.when(uf.getInputStream()).thenReturn(in);
		Mockito.when(uf.getName()).thenReturn(name);
		return uf;
	}

	protected ComponentFile createComponentFileInFolder(String folder, String filename, ApplicationData application, Component... components) {
		File rootDir = application.getApplicationDirectory();
		File dir = null;
		ApplicationFolderImpl finalFolder = null;
		
		if (folder!=null) {
			ensureFolder(application, folder);
			dir = application.getRootFolder().getSubFolders().get(folder).getFolderFile();
			finalFolder = application.getRootFolder().getSubFolders().get(folder);
		} else {
			dir = rootDir;
			finalFolder = application.getRootFolder();
		}
		
		File compFile = new File(dir, filename);
		ComponentSet set = new ComponentSet();
		ComponentFile f = new ComponentFile(set, compFile, finalFolder);

		for(Component comp : components) {
			set.getComponent().add(comp);
		}

		return f;
	}
	

	protected ComponentFile createComponentFile(String filename, ApplicationData application, Component... components) {
		File dir = application.getApplicationDirectory();
		File compFile = new File(dir, filename);
		ComponentSet set = new ComponentSet();
		ComponentFile f = new ComponentFile(set, compFile, application.getRootFolder());

		for(Component comp : components) {
			set.getComponent().add(comp);
		}

		return f;
	}
	
	protected void setProperties(ApplicationFolderImpl folder, Map<String,String> properties) {
		JavascribePropertiesFile f = Mockito.mock(JavascribePropertiesFile.class);
		Mockito.when(f.getFolder()).thenReturn(folder);
		Mockito.when(f.getProperties()).thenReturn(properties);
		Mockito.when(f.getName()).thenReturn("javascribe.properties");
		Mockito.when(f.getLastModified()).thenReturn(0L);
		Mockito.when(f.getPath()).thenReturn(folder.getPath()+"javascribe.properties");
		folder.setJavascribeProperties(f);
	}
	
	protected void clearMessages(ApplicationData applicationData) {
		
	}

	protected void ensureFolder(ApplicationData applicationData, String path) {
		String[] folders = path.split("/");
		ApplicationFolderImpl folder = applicationData.getRootFolder();
		File folderFile = folder.getFolderFile();

		for(String dir : folders) {
			if (folder.getSubFolders().get(dir) == null) {
				File subFile = new File(folderFile, dir);
				subFile.mkdirs();
				ApplicationFolderImpl sub = new ApplicationFolderImpl(subFile, folder);
				folder.getSubFolders().put(dir, sub);
				folder = sub;
				folderFile = subFile;
			} else {
				folder = folder.getSubFolders().get(dir);
				folderFile = folder.getFolderFile();
			}
		}
	}
}

