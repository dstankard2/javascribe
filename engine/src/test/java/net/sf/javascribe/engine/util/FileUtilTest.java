package net.sf.javascribe.engine.util;

import java.io.File;
import java.util.List;

import org.mockito.Mockito;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import net.sf.javascribe.engine.ApplicationBuilder;
import net.sf.javascribe.engine.FolderUtil;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
import net.sf.javascribe.engine.data.files.SystemAttributesFile;
import net.sf.javascribe.engine.data.files.UserFile;
import net.sf.javascribe.engine.data.files.WatchedResource;
import net.sf.javascribe.engine.service.ComponentFileService;
import static org.assertj.core.api.Assertions.*;

public class FileUtilTest {

	FileUtil fileUtil;
	ComponentFileService componentFileService;

	@BeforeTest
	public void setup() {
		fileUtil = new FileUtil();
		componentFileService = Mockito.mock(ComponentFileService.class);
		fileUtil.setComponentFileService(componentFileService);
	}
	
	@AfterTest
	public void teardown() {
		
	}

	// Simple test of shallow application directory with simple user files
	@Test
	public void testAddAndRemoveOperations() throws Exception {
		ApplicationData application = ApplicationBuilder.create()
				.build();
		ApplicationFolderImpl root = FolderUtil.createApplicationFolder(application);
  
		application.setRootFolder(root);
		FolderUtil.createFile(root, "a.txt", "abc123");
		fileUtil.initFolder(root);
		List<WatchedResource> resources = fileUtil.findFilesAdded(root);
		assertThat(resources.size()).isEqualTo(1);
		assertThat(resources.get(0).getClass()).isEqualTo(UserFile.class);
		UserFile uf = (UserFile)resources.get(0);
		assertThat(uf.getName()).isEqualTo("a.txt");
		assertThat(uf.getFolder()).isEqualTo(root);
		assertThat(uf.getFolder().getParent()).isEqualTo(null);

		// Add a user file in subfolder "folder1"
		String content = "abc123";
		File folderFile = FolderUtil.createFolder(root, "folder1");
		FolderUtil.createFile(folderFile, "moved.txt", content);

		// There should be no files removed
		resources = fileUtil.findFilesRemoved(root);
		assertThat(resources.size()).isEqualTo(0);

		// There should be a file added
		resources = fileUtil.findFilesAdded(root);
		assertThat(resources.size()).isEqualTo(1);
		assertThat(resources.get(0).getClass()).isEqualTo(UserFile.class);
		uf = (UserFile)resources.get(0);
		assertThat(uf.getName()).isEqualTo("moved.txt");
		assertThat(uf.getFolder()).isEqualTo(root.getSubFolders().get("folder1"));
		assertThat(uf.getFolder().getParent()).isEqualTo(root);
		assertThat(root.getSubFolders().get("folder1").getUserFiles().size()).isEqualTo(1);
		
		// Move moved.txt to a new folder
		File otherFolderFile = FolderUtil.createFolder(root, "folder2");
		FolderUtil.createFile(otherFolderFile, "moved.txt", content);
		FolderUtil.deleteFile(folderFile, "moved.txt");

		// There should be one user file removed
		resources = fileUtil.findFilesRemoved(root);
		assertThat(resources.size()).isEqualTo(1);
		assertThat(resources.get(0).getClass()).isEqualTo(UserFile.class);
		uf = (UserFile)resources.get(0);
		assertThat(uf.getName()).isEqualTo("moved.txt");
		// The now-empty subfolder should be removed.  Root should have no subfolders
		assertThat(root.getSubFolders().size()).isEqualTo(0);

		// There should be one user file added.
		resources = fileUtil.findFilesAdded(root);
		assertThat(resources.size()).isEqualTo(1);
		assertThat(resources.get(0).getClass()).isEqualTo(UserFile.class);
		uf = (UserFile)resources.get(0);
		assertThat(uf.getName().equals("moved.txt"));
		assertThat(root.getSubFolders().size()).isEqualTo(1);
		assertThat(root.getSubFolders().get("folder2")).isEqualTo(uf.getFolder());
	}
	
	// Test: init with systemAttributes.properties
	// Test: removing of a folder
	// Test: moving of a folder (and its files)
	// Test: read file (different types)
	// Test: adding javascribe.properties
	// Test: resetting of systemAttributes.properties and/or javascribe.properties
	@Test
	public void testMoreCases() throws Exception {
		ApplicationData application = ApplicationBuilder.create().createLog()
				.build();
		ApplicationFolderImpl root = FolderUtil.createApplicationFolder(application);

		application.setRootFolder(root);
		FolderUtil.createFile(root, "systemAttributes.properties", "userId=integer\n");
		File compFolder = FolderUtil.createFolder(root, "components");
		File fromDir = FolderUtil.createFolder(compFolder, "from");
		FolderUtil.createFile(fromDir, "test1.txt", "abc123");
		fileUtil.initFolder(root);
		
		// First attempt to find removed files should return none.
		List<WatchedResource> resources = fileUtil.findFilesRemoved(root);
		assertThat(resources.size()).isEqualTo(0);

		// Find added files, should find one user file and systemAttributes.
		resources = fileUtil.findFilesAdded(root);
		assertThat(resources.size()).isEqualTo(2);
		assertThat(resources.get(0).getClass()).isEqualTo(SystemAttributesFile.class);
		assertThat(resources.get(1).getClass()).isEqualTo(UserFile.class);
		UserFile uf = (UserFile)resources.get(1);
		assertThat(uf.getName()).isEqualTo("test1.txt");
		assertThat(uf.getFolder()).isEqualTo(root.getSubFolders().get("components").getSubFolders().get("from"));
		
		// Move test1.txt from "from" folder to new "to" folder under "components"
		File toFolder = FolderUtil.createFolder(compFolder, "to");
		FolderUtil.createFile(toFolder, "test1.txt", "abc123");
		FolderUtil.deleteFile(fromDir, "test1.txt");
		FolderUtil.deleteFile(compFolder, "from");
		
		// Check for removed files
		resources = fileUtil.findFilesRemoved(root);
		assertThat(resources.size()).isEqualTo(1);
		assertThat(resources.get(0).getName()).isEqualTo("test1.txt");
		assertThat(resources.get(0).getFolder().getFolderFile()).isEqualTo(fromDir);
		
		// Check for added files
		resources = fileUtil.findFilesAdded(root);
		assertThat(resources.size()).isEqualTo(1);
		assertThat(resources.get(0).getName()).isEqualTo("test1.txt");
		
		// Check for removed/added again. There should be no new results
		resources = fileUtil.findFilesRemoved(root);
		assertThat(resources.size()).isEqualTo(0);
		resources = fileUtil.findFilesAdded(root);
		assertThat(resources.size()).isEqualTo(0);
		
		// Rename the "to" folder as "to-again"
		File toAgainFolder = new File(compFolder, "to-again");
		toAgainFolder.deleteOnExit();
		toFolder.renameTo(toAgainFolder);

		// Check for removed files.  there should be one
		resources = fileUtil.findFilesRemoved(root);
		assertThat(resources.size()).isEqualTo(1);
		assertThat(resources.get(0).getName()).isEqualTo("test1.txt");
		
		// Check for added files
		resources = fileUtil.findFilesAdded(root);
		assertThat(resources.size()).isEqualTo(1);
		assertThat(resources.get(0).getName()).isEqualTo("test1.txt");
	}

}

