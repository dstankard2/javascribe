package net.sf.javascribe.patterns.js;

import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.javascript.JavascriptCode;

/**
 * https://github.com/julianshapiro/velocity/wiki
 * @author DCS
 */
@Plugin
public class Velocity2AnimationProvider implements AnimationProvider {

	@Override
	public JavascriptCode show(String divVarName, String animation, String duration, String onComplete)
			throws JavascribeException {
		JavascriptCode ret = new JavascriptCode();
		
		ret.appendCodeText(divVarName + ".style.opacity = '0.0';\n" 
				+ divVarName + ".style.display = '';\nVelocity(" + divVarName 
				+ ",{opacity: '1.0'} , "
				+ "{duration: 400"
				+ ", complete: function() { "+ ((onComplete!=null) ? onComplete : "") +"}});");
		return ret;
	}

	@Override
	public JavascriptCode hide(String divVarName, String animation, String duration, String onComplete)
			throws JavascribeException {
		JavascriptCode ret = new JavascriptCode();
		
		ret.appendCodeText("Velocity(" + divVarName 
				+ ",{opacity: '0.0'} , "
				+ "{duration: 400"
				+ ", complete: function() { a.style.display = 'none';\n"+divVarName+".style.opacity = '0.0';\n"+ ((onComplete!=null) ? onComplete : "") +"}});");
// divVarName + ".style.opacity = '0.0';\n" 
		return ret;
	}

	@Override
	public String getName() {
		return "VelocityJS";
	}

}
