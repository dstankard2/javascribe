package net.sf.javascribe.patterns;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import net.sf.javascribe.api.SourceFile;

public class XmlFile implements SourceFile {

	private Document document = null;
	public static final DocumentFactory factory = new DocumentFactory();
	private String path = null;
	
	public XmlFile(String path) {
		this.path = path;
		document = factory.createDocument();
	}
	
	public String getPath() {
		return path;
	}

	public Document getDocument() {
		return document;
	}
	
	@Override
	public StringBuilder getSource() {
		StringBuilder build = new StringBuilder();
		build.append(document.asXML());
		return build;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
