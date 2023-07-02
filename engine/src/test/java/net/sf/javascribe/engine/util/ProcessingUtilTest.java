package net.sf.javascribe.engine.util;

import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.sf.javascribe.engine.ApplicationBuilder;
import net.sf.javascribe.engine.FolderUtil;
import net.sf.javascribe.engine.ItemUtil;
import net.sf.javascribe.engine.TestBuildComponent;
import net.sf.javascribe.engine.TestComponent;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
import net.sf.javascribe.engine.data.processing.BuildComponentItem;
import net.sf.javascribe.engine.data.processing.ComponentItem;
import net.sf.javascribe.engine.data.processing.Item;
import net.sf.javascribe.engine.data.processing.ProcessingState;

public class ProcessingUtilTest {

	ProcessingUtil processingUtil = null;
	DependencyUtil dependencyUtil = null;
	OutputUtil outputUtil = null;
	
	@BeforeMethod
	public void setup() {
		processingUtil = new ProcessingUtil();
		dependencyUtil = Mockito.mock(DependencyUtil.class);
		outputUtil = Mockito.mock(OutputUtil.class);

		processingUtil.setOutputUtil(outputUtil);
		processingUtil.setDependencyUtil(dependencyUtil);
	}

	@Test
	public void findItemForComponent() {
		ApplicationFolderImpl folder = FolderUtil.createMockFolder("/");
		TestComponent testComp = new TestComponent();
		TestComponent testComp2 = new TestComponent();
		TestBuildComponent testBuildComp = new TestBuildComponent();
		ApplicationData application = ApplicationBuilder.create().rootFolder(folder)
				.addBuildComponentItem(testBuildComp, ProcessingState.CREATED)
				.addComponentItem(testComp, false).addComponentItem(testComp2, false)
				.build();
		ComponentItem item = processingUtil.findItemForComponent(application, testComp);
		assert item.getComponent()==testComp;
	}
	
	@Test
	public void findItemForBuildComponent() {
		ApplicationFolderImpl folder = FolderUtil.createMockFolder("/");
		TestComponent testComp = new TestComponent();
		TestBuildComponent testBuildComp = new TestBuildComponent();
		TestBuildComponent testBuildComp2 = new TestBuildComponent();
		ApplicationData application = ApplicationBuilder.create()
				.rootFolder(folder)
				.addBuildComponentItem(testBuildComp, ProcessingState.CREATED)
				.addBuildComponentItem(testBuildComp2, ProcessingState.PROCESSING).addComponentItem(testComp, false)
				.build();
		BuildComponentItem item = processingUtil.findItemForBuildComponent(application, testBuildComp);
		assert item.getBuildComponent()==testBuildComp;

		BuildComponentItem item2 = processingUtil.findItemForBuildComponent(application, testBuildComp2);
		assert item2.getBuildComponent()==testBuildComp2;
	}
	
	@Test
	public void addItem() {
		ApplicationFolderImpl folder = FolderUtil.createMockFolder("/");
		TestComponent testComp = new TestComponent();

		ApplicationData application = ApplicationBuilder.create()
				.rootFolder(folder)
				.addComponentItem(testComp, true)
				.build();
		
		Item item = ItemUtil.createComponentItem(testComp, application);
		processingUtil.addItem(item, application);
		System.out.println("hi");
	}

}

