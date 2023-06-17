package net.sf.javascribe.api;

import java.util.List;

import net.sf.javascribe.api.config.BuildComponent;
import net.sf.javascribe.api.exception.JavascribeException;

public interface BuildComponentProcessor<T extends BuildComponent> {

	void initialize(T buildComponent, BuildProcessorContext ctx) throws JavascribeException;
	BuildContext createBuildContext();
	void generateBuild() throws JavascribeException;
	List<Command> build();
	List<Command> clean();

}
