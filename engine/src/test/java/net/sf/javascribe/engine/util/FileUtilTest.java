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
		List<WatchedResource> resources = fileUtil.findFilesAdded(application, root);
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
		resources = fileUtil.findFilesRemoved(application, root);
		assertThat(resources.size()).isEqualTo(0);

		// There should be a file added
		resources = fileUtil.findFilesAdded(application, root);
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
		resources = fileUtil.findFilesRemoved(application, root);
		assertThat(resources.size()).isEqualTo(1);
		assertThat(resources.get(0).getClass()).isEqualTo(UserFile.class);
		uf = (UserFile)resources.get(0);
		assertThat(uf.getName()).isEqualTo("moved.txt");

		// There should be one user file added.
		resources = fileUtil.findFilesAdded(application, root);
		fileUtil.trimFolders(application, root);
		assertThat(resources.size()).isEqualTo(1);
		assertThat(resources.get(0).getClass()).isEqualTo(UserFile.class);
		uf = (UserFile)resources.get(0);
		assertThat(uf.getName().equals("moved.txt"));
		// There should be one subfolder under root
		assertThat(root.getSubFolders().size()).isEqualTo(1);
		// The user file is in folder2 under root
		assertThat(root.getSubFolders().get("folder2")).isEqualTo(uf.getFolder());
	}

	// Test: read component file
	// Test: add a javascribe.properties file
	// Test: modify systemAttributes.properties file
	// Test: remove systemAttributes.properties file
	// Test: modify javascribe.properties file
	// Test: remove javascribe.properties file
	// Test: modify component file
	// Test: remove component file
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
		
		// First attempt to find removed files should return none.
		List<WatchedResource> resources = fileUtil.findFilesRemoved(application, root);
		assertThat(resources.size()).isEqualTo(0);

		// Find added files, should find one user file and systemAttributes.
		resources = fileUtil.findFilesAdded(application, root);
		fileUtil.trimFolders(application, root);
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
		resources = fileUtil.findFilesRemoved(application, root);
		assertThat(resources.size()).isEqualTo(1);
		assertThat(resources.get(0).getName()).isEqualTo("test1.txt");
		assertThat(resources.get(0).getFolder().getFolderFile()).isEqualTo(fromDir);
		
		// Check for added files
		resources = fileUtil.findFilesAdded(application, root);
		fileUtil.trimFolders(application, root);
		assertThat(resources.size()).isEqualTo(1);
		assertThat(resources.get(0).getName()).isEqualTo("test1.txt");
		
		// Check for removed/added again. There should be no new results
		resources = fileUtil.findFilesRemoved(application, root);
		assertThat(resources.size()).isEqualTo(0);
		resources = fileUtil.findFilesAdded(application, root);
		fileUtil.trimFolders(application, root);
		assertThat(resources.size()).isEqualTo(0);
		
		// Rename the "to" folder as "to-again"
		File toAgainFolder = new File(compFolder, "to-again");
		toAgainFolder.deleteOnExit();
		toFolder.renameTo(toAgainFolder);

		// Check for removed files.  there should be one
		resources = fileUtil.findFilesRemoved(application, root);
		assertThat(resources.size()).isEqualTo(1);
		assertThat(resources.get(0).getName()).isEqualTo("test1.txt");
		
		// Check for added files
		resources = fileUtil.findFilesAdded(application, root);
		fileUtil.trimFolders(application, root);
		assertThat(resources.size()).isEqualTo(1);
		assertThat(resources.get(0).getName()).isEqualTo("test1.txt");
		
		// Add a javascribe.properties file in component directory. 

		// All files should be removed.
		
		// All files should be re-added.
		
		// Update systemAttributes.properties.  

		// All files should be removed.
		
		// All files should be re-added.
		
	}

}

