package net.sf.javascribe.patterns;

/**
 * Numbering convention: Component priority determines the order in which components will be 
 * processed.  Components that need to be processed first should have lower priority.  At a high 
 * level, the numbering is 10000 for each logical tier (where the data model is the first priority) 
 * such that data model is 10000, business logic is 20000, controller is 30000, view implementation is 
 * 40000.  Any other tier that relies on business logic can be considered 30000 as well (such as 
 * web services, scheduled processes, etc).  Within this tiering number scheme, a component should 
 * be given a priority that puts it a step behind the component(s).  In the case that this next 
 * priority gives it the same priority as a component that is dependant on it, the component should 
 * be given the priority in between those two.  The spacing of the priorities allows for several 
 * levels of nesting.
 * @author DCS
 *
 */
public class CorePatternConstants {

	/** Priority ratings for core component processors **/
	
	// No dependencies.  Other processors may depend on it.
	public static final int PRIORITY_GENERIC_JAVA_CLASSES = 0;
	
	// No dependencies
	public static final int PRIORITY_ENTITY_MANAGER = 10000;

	// No dependency
	public static final int PRIORITY_LOOKUPS = 10000;
	
	// Dependency of data object.  Depends on lookup, entity manager
	public static final int PRIORITY_CLASSIFICATION = 12500;

	// Depends on entity manager
	public static final int PRIORITY_THREAD_LOCAL_TX_LOCATOR = 12500;
	
	// Depends on entity manager
	public static final int PRIORITY_JPA_DAO_FACTORY = 13750;
	
	// depends on entity manager
	public static final int PRIORITY_EJBQL_QUERY = 15000;
	
	// depends on entity manager
	public static final int PRIORITY_JPA_NATIVE_QUERY = 15000;
	
	// depends on entity manager
	public static final int PRIORITY_DATA_OBJECT = 15000;
	
	// Comes after Data Object
	public static final int PRIORITY_CLASSIFICATION_FINALIZER = PRIORITY_DATA_OBJECT+1;

	// Depends on DAO Factory
	public static final int PRIORITY_UPDATE_ENTITY_RULE = 16250;

	// Depends on lookups.
	public static final int PRIORITY_OPTION_LIST = 17500;

	// Depends on JPA DAO Factory
	public static final int PRIORITY_JPA_ENTITY_INDEX = 17500;
	
	// Depends on EntityManager and Data Object
	public static final int PRIORITY_DATA_TRANSLATOR = 17500;

	// Depends on entity manager and JPA DAO Factory.  Process after entity indices
	public static final int PRIORITY_ENTITY_RELATIONSHIPS = 18750;
	
	// Depends on Custom Logic and Data Object
	public static final int PRIORITY_RETRIEVE_DATA_STRATEGY = 21250;

	// Domain logic pattern.  Depends on Retrieve Data Strategy
	public static final int PRIORITY_RETRIEVE_DATA_RULE = 22500;

	// Domain logic pattern.
	public static final int PRIORITY_DOMAIN_LOGIC_RULE = 23750;

	// Depends on Domain Logic patterns.  Last of the domain logic patterns.
	public static final int PRIORITY_DOMAIN_LOGIC_FINALIZER = 24375;

	// Depends on domain logic, JPA dao factory, entity manager, domain data
	public static final int PRIORITY_SERVICE = 25000;
	
	// No dependancy
	public static final int PRIORITY_WEB_APPLICATION = 25000;
	
	// No Dependancy
	public static final int PRIORITY_VIEW_SET = 25000;
	
	// Depends on web application, service
	public static final int PRIORITY_SCHEDULED_JOB = 30000;
	
	// Depends on service
	public static final int PRIORITY_JAX_RS_WEBSERVICE = 30000;
	
	// Depends on web application and service
	public static final int PRIORITY_SERVLET_FILTER = 27500;
	
	// Depends on service and servlet filter, view set
	public static final int PRIORITY_SERVLET_EVENT = 30000;
	
	// Depends on service and servlet filter
	public static final int PRIORITY_SERVLET_WEB_SERVICE = 30000;

	/** Client-side (Javascript) patterns **/
	
	// Depends on web service
	public static final int PRIORITY_SERVLET_WEB_SERVICE_CLIENT = 35000;

	// Dependant on server-side components.  It goes with the front end.
	public static final int PRIORITY_VIEW_TEMPLATE = 40000;

	// No dependency but it goes with the front end.
	public static final int PRIORITY_PAGE = 40000;
	
	// Dependency on Page
	public static final int PRIORITY_PAGE_VIEW_ELEMENTS = 45000;
	
	// Dependency on Page
	public static final int PRIORITY_PAGE_MODEL = 45000;

	// Dependency on page and page model
	public static final int PRIORITY_PAGE_WS_CLIENT = 46750;
	
	// Depends on Page, page model
	public static final int PRIORITY_PAGE_FUNCTION = 47500;
	
	// Depends on view elements, page model, ws client and page function
	// Should come before templates
	public static final int PRIORITY_PAGE_BINDING = 47966;
	
	// Depends on Page, page model
	public static final int PRIORITY_HTML_TEMPLATE = 48424;
	
	// Depends on Page, page model
	public static final int PRIORITY_VIEW_TEMPLATE_SET = 48424;
	
	// Depends on view elements, page model and vm function
	public static final int PRIORITY_PAGE_FINALIZER = 49999;
	
	// Depends on page being finished.
	public static final int PRIORITY_PAGE_NAVIGATION = 50000;
	
	// Depends on servlet web service
	public static final int PRIORITY_JS_WEB_SERVICE_CLIENT = 50000;

}

