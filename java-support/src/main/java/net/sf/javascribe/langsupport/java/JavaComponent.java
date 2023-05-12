package net.sf.javascribe.langsupport.java;

import net.sf.javascribe.api.annotation.ConfigProperty;
import net.sf.javascribe.api.config.Component;

public abstract class JavaComponent extends Component {

	private String javaRootPackage = null;

	public abstract String getPkg();

	public String getJavaRootPackage() {
		return javaRootPackage;
	}
	
	public String getFullPackage() {
		return javaRootPackage + '.' + getPkg();
	}

	@ConfigProperty(required = true, name = JavaUtils.CONFIG_PROPERTY_JAVA_ROOT_PACKAGE,
			description = "Root Java package of the code distribution.", example = "net.sf.javascribe")
	public void setJavaRootPackage(String javaRootPackage) {
		this.javaRootPackage = javaRootPackage;
	}

}
