package net.sf.javascribe.engine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import org.mockito.Mockito;

import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.api.config.ComponentSet;
import net.sf.javascribe.api.logging.ProcessorLogLevel;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;

public class FolderUtil {

	public static ApplicationFolderImpl createMockFolder(String path) {
		ApplicationFolderImpl ret = Mockito.mock(ApplicationFolderImpl.class);
		Mockito.when(ret.getLogLevel()).thenReturn(ProcessorLogLevel.INFO);
		Mockito.when(ret.getPath()).thenReturn(path);
		return ret;
	}

	public static File createFolder(ApplicationFolderImpl parentFolder, String folderPath) throws IOException {
		File parentFile = parentFolder.getFolderFile();
		File file = new File(parentFile, folderPath);
		
		file.mkdirs();
		file.deleteOnExit();
		
		return file;
	}

	public static File createFolder(File parentDir, String folderPath) throws IOException {
		File file = new File(parentDir, folderPath);
		
		file.mkdirs();
		file.deleteOnExit();
		
		return file;
	}

	/*
	public static ApplicationFolderImpl createFolder(ApplicationFolderImpl parentFolder, String folderPath) throws IOException {
		File parentFile = parentFolder.getFolderFile();
		ApplicationFolderImpl ret = null;
		File file = new File(parentFile, folderPath);
		
		file.mkdirs();
		ret = new ApplicationFolderImpl(file, parentFolder);
		
		return ret;
	}
	*/

	public static ApplicationFolderImpl createApplicationFolder(ApplicationData application) throws IOException {
		File file = null;
		Path path = Files.createTempDirectory("jstest");
		file = path.toFile();
		file.deleteOnExit();
		ApplicationFolderImpl ret = null;
		
		ret = new ApplicationFolderImpl(file, application);
		
		return ret;
	}
	
	public static void createFile(File dir, String filename, String content) throws IOException {
		File file = new File(dir, filename);
		try (FileWriter writer = new FileWriter(file)) {
			writer.write(content);
			writer.flush();
		}
		file.deleteOnExit();
	}

	public static void createFile(ApplicationFolderImpl folder, String filename, String content) throws IOException {
		File folderFile = folder.getFolderFile();

		File file = new File(folderFile, filename);
		try (FileWriter writer = new FileWriter(file)) {
			writer.write(content);
			writer.flush();
		}
		file.deleteOnExit();
	}

	public static void deleteFile(File dir, String name) {
		File target = new File(dir, name);
		if (!target.exists()) {
			throw new RuntimeException("Couldn't delete file "+name+" in folder "+dir.getPath()+" because it doesn't exist");
		}
		if (!target.delete()) {
			throw new RuntimeException("Couldn't delete file "+name+" in folder "+dir.getPath());
		}
	}

	private static Class<?>[] classes = new Class<?>[] {
		TestComponent.class, TestBuildComponent.class
	};
	private static JAXBContext ctx = null;
	private static Marshaller m = null;

	public static void createComponentFile(File dir, String name, Component... components) throws Exception {
		ComponentSet comps = new ComponentSet();
		File output = new File(dir, name);
		
		comps.setComponent(Arrays.asList(components));
		
		if (ctx==null) {
			ctx = JAXBContext.newInstance(classes);
			m = ctx.createMarshaller();
		}
		
		m.marshal(comps, output);
	}
	
}

