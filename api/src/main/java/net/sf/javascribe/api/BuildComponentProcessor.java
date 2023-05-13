package net.sf.javascribe.api;

import java.util.List;

import net.sf.javascribe.api.config.BuildComponent;
import net.sf.javascribe.api.exception.JasperException;

public interface BuildComponentProcessor<T extends BuildComponent> {

	//void setBuildProcessorContext(BuildProcessorContext ctx) throws JasperException;
	//void setBuildComponent(BuildComponent buildComponent) throws JasperException;
	void initialize(T buildComponent, BuildProcessorContext ctx) throws JasperException;
	BuildContext createBuildContext();
	void generateBuild() throws JasperException;
	List<Command> build();
	List<Command> clean();
	Class<? extends BuildComponent> getComponentClass();

}
