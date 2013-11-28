/*
 * Created on May 29, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.javascribe.patterns.servlet;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.xsd.web_app_2_5.FilterMappingType;
import net.sf.javascribe.xsd.web_app_2_5.FilterNameType;
import net.sf.javascribe.xsd.web_app_2_5.FilterType;
import net.sf.javascribe.xsd.web_app_2_5.FullyQualifiedClassType;
import net.sf.javascribe.xsd.web_app_2_5.JspConfigType;
import net.sf.javascribe.xsd.web_app_2_5.ListenerType;
import net.sf.javascribe.xsd.web_app_2_5.ObjectFactory;
import net.sf.javascribe.xsd.web_app_2_5.ParamValueType;
import net.sf.javascribe.xsd.web_app_2_5.PathType;
import net.sf.javascribe.xsd.web_app_2_5.ServletMappingType;
import net.sf.javascribe.xsd.web_app_2_5.ServletNameType;
import net.sf.javascribe.xsd.web_app_2_5.ServletType;
import net.sf.javascribe.xsd.web_app_2_5.TaglibType;
import net.sf.javascribe.xsd.web_app_2_5.UrlPatternType;
import net.sf.javascribe.xsd.web_app_2_5.WebAppType;
import net.sf.javascribe.xsd.web_app_2_5.XsdStringType;

/**
 * @author User
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WebXmlFile implements SourceFile {
	private WebAppType appType = null;
	private JAXBElement<WebAppType> app = null;
	String path = null;

	private List<JAXBElement<?>> filterList = new ArrayList<JAXBElement<?>>();
	private List<JAXBElement<?>> filterMappingList = new ArrayList<JAXBElement<?>>();
	private List<JAXBElement<?>> servletList = new ArrayList<JAXBElement<?>>();
	private List<JAXBElement<?>> servletMappingList = new ArrayList<JAXBElement<?>>();
	private List<JAXBElement<?>> contextListenerList = new ArrayList<JAXBElement<?>>();
	private JAXBElement<JspConfigType> jspConfigElt = null;
	private JspConfigType jspConfig = null;
	
	private ObjectFactory factory = null;
	private HashMap<String,ServletType> servlets = new HashMap<String,ServletType>();
//	private JspConfigType jspConfig = null;
	//    private List<String> welcomeFileList = new ArrayList<String>();
	//    private WelcomeFileListType welcomeFileListType = null;

	protected ServletType getServlet(String servletName) {
		return servlets.get(servletName);
	}

	protected void addServlet(String servletName,ServletType s) {
		servlets.put(servletName,s);
	}

	public boolean containsServlet(String servletName) {
		return servlets.containsKey(servletName);
	}

	/*
	public void addWelcomeFile(String f) {

		if (welcomeFileListType==null) {
			welcomeFileListType = factory.createWelcomeFileListType();
			itemList.add(factory.createWebAppTypeWelcomeFileList(welcomeFileListType));
		}
		if (!welcomeFileList.contains(f)) {
			welcomeFileListType.getWelcomeFile().add(f);
		}
	}
	 */

	public WebXmlFile() {
		factory = new ObjectFactory();
		appType = factory.createWebAppType();
		appType.setVersion("2.5");
		app = factory.createWebApp(appType);
	}

	public void addServlet(String displayName,String servletName,String servletClass) {
		ServletType addition = null;
		ServletNameType name = null;
		FullyQualifiedClassType cl = null;

		cl = factory.createFullyQualifiedClassType();
		cl.setValue(servletClass);
		name = factory.createServletNameType();
		name.setValue(servletName);
		addition = factory.createServletType();
		addition.setServletName(name);
		addition.setServletClass(cl);
		addition.setId(displayName);
		addition.setLoadOnStartup("1");
		servletList.add(factory.createWebAppTypeServlet(addition));
		this.addServlet(servletName,addition);
	}

	public void addFilter(String name,String className) {
		FilterType addition = null;
		FullyQualifiedClassType cl = factory.createFullyQualifiedClassType();
		FilterNameType n = factory.createFilterNameType();

		cl.setValue(className);
		n.setValue(name);
		addition = factory.createFilterType();
		addition.setFilterClass(cl);
		addition.setFilterName(n);
		filterList.add(factory.createWebAppTypeFilter(addition));
	}

	/*
	public FilterDefinition getFilter(String name) {
		FilterDefinition ret = null;
		FilterType f = null;

		f = filters.get(name);
		if (f!=null) {
			ret = new FilterDefinition(f);
		}
		return ret;
	}
	 */

	public void setDisplayName(String name) {
		appType.setId(name);
	}

	public void addFilterMapping(String filterName,String urlPattern) {
		FilterMappingType addition = null;
		FilterNameType fname = null;
		UrlPatternType p = null;

		addition = factory.createFilterMappingType();
		fname = factory.createFilterNameType();
		fname.setValue(filterName);
		addition.setFilterName(fname);
		p = factory.createUrlPatternType();
		p.setValue(urlPattern);
		addition.getUrlPatternOrServletName().add(p);
		filterMappingList.add(factory.createWebAppTypeFilterMapping(addition));
	}

	public void addServletMapping(String servletName,String urlPattern) {
		ServletMappingType addition = null;
		ServletNameType name = null;
		UrlPatternType url = null;

		addition = factory.createServletMappingType();
		name = factory.createServletNameType();
		name.setValue(servletName);
		url = factory.createUrlPatternType();
		url.setValue(urlPattern);
		addition.setServletName(name);
		addition.getUrlPattern().add(url);
		servletMappingList.add(factory.createWebAppTypeServletMapping(addition));
	}

	public void addServletInitParam(String servletName,String paramName,String paramValue) throws JavascribeException {
		ServletType s = null;
		ParamValueType v = null;
		net.sf.javascribe.xsd.web_app_2_5.String st = null;
		XsdStringType xs = null;

		s = getServlet(servletName);
		if (s==null) {
			throw new JavascribeException("No servlet named '"+servletName+"' defined");
		}
		v = factory.createParamValueType();
		st = factory.createString();
		st.setValue(paramName);
		v.setParamName(st);
		xs = factory.createXsdStringType();
		xs.setValue(paramValue);
		v.setParamValue(xs);
		s.getInitParam().add(v);
	}

	public void addErrorPage(java.lang.String exceptionType,java.lang.String location) {

	}

	public void addContextListener(java.lang.String listenerClass) {
		ListenerType listener = null;
		FullyQualifiedClassType cl = null;

		listener = factory.createListenerType();
		cl = factory.createFullyQualifiedClassType();
		cl.setValue(listenerClass);
		listener.setListenerClass(cl);
		contextListenerList.add(factory.createWebAppTypeListener(listener));
	}

	public void addContextParameter(String name,String value) {
		ParamValueType val = null;
		XsdStringType s = null;
		net.sf.javascribe.xsd.web_app_2_5.String stringName = null;

		val = factory.createParamValueType();
		stringName = factory.createString();
		stringName.setValue(name);
		val.setParamName(stringName);
		s = factory.createXsdStringType();
		s.setValue(value);
		val.setParamValue(s);
		//		itemList.add(s);
	}

	public String getContextParameter(String name) {
		return null;
	}

	public void addTaglib(String uri,String location) {
		TaglibType tld = null;
		PathType loc = null;
		net.sf.javascribe.xsd.web_app_2_5.String n = null;

		tld = factory.createTaglibType();
		n = factory.createString();
		n.setValue(uri);
		loc = factory.createPathType();
		loc.setValue(location);
		tld.setTaglibLocation(loc);
		tld.setTaglibUri(n);
		if (jspConfigElt==null) {
			jspConfig = new JspConfigType();
			jspConfigElt = factory.createWebAppTypeJspConfig(jspConfig);
		}
		jspConfig.getTaglib().add(tld);
	}

	public StringBuilder getSource() {
		StringBuffer b = new StringBuffer();
		StringWriter writer = null;
		JAXBContext ctx = null;
		Marshaller m = null;
		StringBuilder ret = new StringBuilder();

		List<JAXBElement<?>> itemList = appType.getDescriptionAndDisplayNameAndIcon();
		itemList.addAll(contextListenerList);
		itemList.addAll(filterList);
		itemList.addAll(filterMappingList);
		itemList.addAll(servletList);
		itemList.addAll(servletMappingList);
		if (jspConfigElt!=null) {
			itemList.add(jspConfigElt);
		}
		
		try {
			writer = new StringWriter();
			ctx = JAXBContext.newInstance( "net.sf.javascribe.xsd.web_app_2_5",getClass().getClassLoader() );
			m = ctx.createMarshaller();
			m.marshal(app, writer);
			b = writer.getBuffer();
			ret.append(b.toString());
		} catch(JAXBException e) {
			System.err.println("Encountered JAXB Exception while trying to create web descriptor");
			e.printStackTrace();
			return null;
		}
		return ret;
	}

	public void setSource(StringBuilder buf) {
		throw new RuntimeException("Cannot call setSource() on a WebDescriptor object");
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}

}

