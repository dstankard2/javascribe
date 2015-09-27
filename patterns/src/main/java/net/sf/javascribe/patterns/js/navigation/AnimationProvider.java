package net.sf.javascribe.patterns.js.navigation;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.javascript.JavascriptCode;

public interface AnimationProvider {

	public JavascriptCode show(String div,String animation,String duration,String onComplete) throws JavascribeException;
	public JavascriptCode hide(String elementId,String animation,String duration,String onComplete) throws JavascribeException;
	public String getName();

}

