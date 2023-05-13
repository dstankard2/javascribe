package net.sf.javascribe.engine.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.sf.javascribe.engine.data.changes.AddedFile;
import net.sf.javascribe.engine.data.processing.ProcessorLog;

@Getter
@Setter
@Builder
public class ApplicationData {

	private File applicationDirectory;
	
	private File outputDirectory;
	
	private String name;

	@Builder.Default
	private List<AddedFile> filesAdded = new ArrayList<>();

	@Builder.Default
	private List<AddedFile> filesRemoved = new ArrayList<>();
	
	@Builder.Default
	private ProcessorLog applicationLog = new ProcessorLog("APP");

}
