package net.sf.javascribe.engine;

import org.mockito.Mockito;

import net.sf.javascribe.api.logging.ProcessorLogLevel;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;

public class FolderUtil {

	public static ApplicationFolderImpl createMockFolder(String path) {
		ApplicationFolderImpl ret = Mockito.mock(ApplicationFolderImpl.class);
		Mockito.when(ret.getLogLevel()).thenReturn(ProcessorLogLevel.INFO);
		Mockito.when(ret.getPath()).thenReturn(path);
		return ret;
	}

}

