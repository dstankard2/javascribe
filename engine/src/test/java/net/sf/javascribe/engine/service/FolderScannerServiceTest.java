package net.sf.javascribe.engine.service;

import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;

import net.sf.javascribe.engine.util.FileUtil;

public class FolderScannerServiceTest {

	FileUtil fileUtil;
	FolderScannerService folderScannerService;
	
	@BeforeMethod
	public void setup() {
		folderScannerService = new FolderScannerService();
		fileUtil = Mockito.mock(FileUtil.class);

		folderScannerService.setFileUtil(fileUtil);
	}

}
