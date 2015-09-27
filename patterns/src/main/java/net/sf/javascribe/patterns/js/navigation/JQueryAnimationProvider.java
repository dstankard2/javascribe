package net.sf.javascribe.patterns.js.navigation;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.javascript.JavascriptCode;

@Scannable
public class JQueryAnimationProvider implements AnimationProvider {

	@Override
	public JavascriptCode show(String div,String animation,String duration,String onComplete) {
		JavascriptCode ret = new JavascriptCode(true);

		if (onComplete!=null) {
			ret.append("$('#'+"+div+").show('"+animation+"',{},"+duration+","+onComplete+");\n");
		} else {
			ret.append("$('#'+"+div+").show('"+animation+"',{},"+duration+");\n");
		}
		
		return ret;
	}

	@Override
	public JavascriptCode hide(String elementId,String animation,String duration,String onComplete) {
		JavascriptCode ret = new JavascriptCode(true);
		
		if (onComplete!=null) {
			ret.append("$('#'+"+elementId+").hide('"+animation+"',{},"+duration+","+onComplete+");\n");
		} else {
			ret.append("$('#'+"+elementId+").hide('"+animation+"',{},"+duration+");\n");
		}
		
		return ret;
	}

	@Override
	public String getName() {
		return "jquery";
	}

}
