package net.sf.javascribe.patterns.view.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.expressions.ExpressionUtil;

public class JaEval2 {
	
	private List<String> impliedVars = new ArrayList<String>();
	private String code = null;
	CodeExecutionContext execCtx = null;

	public JaEval2(String code,CodeExecutionContext execCtx) {
		this.code = code;
		this.execCtx = execCtx;
	}
	
	public JaEval2 addImpliedVariable(String name) {
		impliedVars.add(name);
		return this;
	}

	public JaEvalResult parseExpression() {
		JaEvalResult res = JaEvalResult.newInstance(code);
		JaEvalResult ret = readPart("expr", res, null);
		if (ret==null) {
			res.setErrorMessage("Couldn't parse expression '"+code+"'");
			ret = res;
		}
		return ret;
	}
	
	public JaEvalResult parseCodeBlock() {
		JaEvalResult res = JaEvalResult.newInstance(code);
		JaEvalResult ret = null;
		char c = res.getRemaining().nextNonWs();
		if (c!=0) {
			res.getRemaining().backtrack();
			if ((!res.getRemaining().getCode().endsWith(";")) 
					&& (!res.getRemaining().getCode().endsWith("}"))) {
				res.getRemaining().append(";");
			}
			ret = readCodeBlock(res,null);
		}
		if (ret==null) {
			ret = res;
			res.setErrorMessage("Couldn't find a match when parsing code block '"+res.getRemaining().getCode()+"'");
		}
		return ret;
	}
	
	protected JaEvalResult readCodeBlock(JaEvalResult currentResult,String ending) {
		JaEvalResult ret = null;
		JaEvalResult current = null;

		ret = currentResult.createNew();

		while(ret.getRemaining().getRemaining()>0) {
			current = readCodeLine(ret);
			if (current==null) return null;
			if (current.getErrorMessage()!=null) return current;
			ret.merge(current,true);
			char c = ret.getRemaining().nextNonWs();
			if (c>0) ret.getRemaining().backtrack();
			if (ret.getRemaining().startsWith(ending)) {
				ret.getResult().append(ending);
				for(int i=0;i<ending.length();i++) {
					ret.getRemaining().next();
				}
			}
		}
		return ret;
	}

	// Never returns null
	protected JaEvalResult readCodeLine(JaEvalResult currentResult) {
		JaEvalResult ret = null;
		
		for(String s : JaEvalConst.codeLine) {
			ret = readPattern(s,currentResult, false, null);
			if (ret==null) continue;
			if (ret.getErrorMessage()!=null) {
				return ret;
			} else {
				break;
			}
		}
		if (ret==null) ret = readAssignment(currentResult);
		
		return ret;
	}
	
	protected JaEvalResult readAssignment(JaEvalResult currentResult) {
		JaEvalResult ret = currentResult.createNew();
		JaEvalResult sub = null;
		String varRef = null;
		String value = null;
		
		varRef = readVariableReference(ret);
		if (varRef==null) return null;
		char c = ret.getRemaining().nextNonWs();
		if (c!='=') return null;
		sub = readPattern("$expr$",ret,false,null);
		if (sub==null) return null;
		if (sub.getErrorMessage()!=null) return sub;
		ret.merge(sub, false);
		value = sub.getResult().toString();
		c = ret.getRemaining().nextNonWs();
		if ((c!=';') && (c!=0)) return null;
		
		String line = null;
		try {
			line = ExpressionUtil.evaluateSetExpression(varRef, value, execCtx);
		} catch(Exception e) {}
		for(String v : impliedVars) {
			if (line!=null) break;
			try {
				line = ExpressionUtil.evaluateSetExpression(v+'.'+varRef, value, execCtx);
			} catch(Exception e) { }
		}
		if (line==null) {
			ret.setErrorMessage("Couldn't evaluate assignment '"+varRef+"' = '"+value+"'");
		} else {
			ret.getResult().append(line+";\n");
		}
		
		return ret;
	}
	
	protected JaEvalResult readPart(String name,JaEvalResult currentResult,String startIgnore) {
		String[] patterns = null;
		JaEvalResult ret = null;
		
		if (name.equals("number")) ret = readNumberLiteral(currentResult);
		//else if (name.equals("expr")) ret = readExpression(currentResult, false, startIgnore);
		else if (name.equals("string")) ret = readStringLiteral(currentResult);
		else if (name.equals("identifier")) ret = readIdentifier(currentResult);
		else if (name.equals("fnCall")) ret = readFunctionCall(currentResult);
		else if (name.equals("varRef")) ret = readVarRef(currentResult);
		else {
			if (name.equals("codeLine")) patterns = JaEvalConst.codeLine;
			else if (name.equals("expr")) patterns = JaEvalConst.expr;
			else {
				throw new RuntimeException("Couldn't do an eval because a pattern contained '"+name+"'");
			}
			ret = currentResult.createNew();
			for(String s : patterns) {
				JaEvalResult res = readPattern(s,currentResult,false,startIgnore);
				if ((res==null) || (res.getErrorMessage()!=null)) continue;
				ret.merge(res,true);
				break;
			}
		}
		
		return ret;
	}
	
	protected JaEvalResult readFunctionCall(JaEvalResult currentResult) {
		JaEvalResult ret = currentResult.createNew();
		String ref = readVariableReference(ret);
		if (ref==null) {
			return null;
		}

		ref = getFinalRef(ref,ret,false);
		if (ref.startsWith("_$$$")) ref = ref.substring(4);
		if (ret.getErrorMessage()!=null) return ret;
		ret.getResult().append(ref);
		char c = ret.getRemaining().nextNonWs();
		boolean firstParam = true;
		if (c!='(') return null;
		ret.getResult().append(c);
		c = ret.getRemaining().nextNonWs();
		while(c!=')') {
			ret.getRemaining().backtrack();
			if (!firstParam) {
				char x = ret.getRemaining().nextNonWs();
				if (x!=',') return null;
				ret.getResult().append(',');
			} else firstParam = false;
			
			JaEvalResult sub = readPart("expr", ret, null);
			//JaEvalResult sub = readPattern("$expr$",ret,false,null);
			if (sub==null) return null;
			if (sub.getErrorMessage()!=null) return sub;
			ret.merge(sub,true);
			c = ret.getRemaining().nextNonWs();
		}
		ret.getResult().append(c);
		return ret;
	}
	
	protected JaEvalResult readVarRef(JaEvalResult currentResult) {
		JaEvalResult ret = currentResult.createNew();
		String ref = readVariableReference(ret);
		if (ref==null) {
			return null;
		}
		ref = getFinalRef(ref,ret,false);
		String temp = ref;
		if (temp.startsWith("_$$$")) temp = temp.substring(4);
		
		for(String s : JaEvalConst.keywords) {
			if (ref.equals(s)) {
				ret.getResult().append(ref);
				return ret;
			}
		}
		
		if (ret.getErrorMessage()!=null) return ret;
		try {
			String finalRef;
			if (!ref.startsWith("_$$$"))
				finalRef = ExpressionUtil.evaluateValueExpression("${"+ref+"}", "object", execCtx);
			else {
				finalRef = ref.substring(4);
			}
			ret.getResult().append(finalRef);
		} catch(Exception e) {
			System.err.println("Unexpected error");
			e.printStackTrace();
		}
		return ret;
	}
	
	protected String getFinalRef(String ref,JaEvalResult currentResult,boolean isFunction) {
		String type = null;

		for(String s : JaEvalConst.keywords) {
			if (ref.equals(s)) return ref;
		}
		
		try {
			type = execCtx.evaluateTypeForExpression(ref);
			if (type!=null) {
				if ((isFunction) && (!type.equals("function"))) {
					currentResult.setErrorMessage("Found a function reference '"+ref+"' but it was not a function");
				}
				return ref;
			}
			
		} catch(Exception e) { }
		for(String s : this.impliedVars) {
			try {
				type = execCtx.evaluateTypeForExpression(s+'.'+ref);
				if (type!=null) {
					if ((isFunction) && (!type.equals("function"))) {
						currentResult.setErrorMessage("Found a function reference '"+ref+"' but it was not a function");
					}
					return s+'.'+ref;
				}
			} catch(Exception e) {
				
			}
		}
		
		return "_$$$"+ref;
	}
	
	// Reads a reference to variable in the current execCtx
	// will accept any reference that starts with "window." or "document."
	protected String readVariableReference(JaEvalResult currentResult) {
		StringBuilder build = new StringBuilder();
		boolean first = true;
		int place = currentResult.getRemaining().getPlace();
		boolean expectingFirst = true;
		
		do {
			if (first) {
				currentResult.getRemaining().skipWs();
				expectingFirst = false;
			}
			if (currentResult.getRemaining().getRemaining()==0) {
				if (build.length()>0) return build.toString();
				else return null;
			}
			char c = currentResult.getRemaining().next();
			if (first) {
				if (Character.isJavaIdentifierStart(c)) {
					first = false;
					build.append(c);
				} else {
					currentResult.getRemaining().setPlace(place);
					return null;
				}
			} else {
				if (c=='.') {
					build.append(c);
					first = true;
				}
				else if (Character.isWhitespace(c)) {
					expectingFirst = true;
				}
				else if (Character.isJavaIdentifierPart(c)) {
					if (expectingFirst) {
						// Something unexpected (or don't know what to do yet)
						currentResult.getRemaining().setPlace(place);
						return null;
					}
					build.append(c);
				} else {
					// Reached the end.
					currentResult.getRemaining().backtrack();
					return build.toString();
				}
			}
		} while(currentResult.getRemaining().getRemaining()>0);
		
		return build.toString();
	}
	
	protected JaEvalResult readIdentifier(JaEvalResult currentResult) {
		JaEvalResult ret = null;
		ret = currentResult.createNew();
		boolean first = true;
		StringBuilder ref = new StringBuilder();
		
		char c = ret.getRemaining().nextNonWs();
		while(c!=0) {
			if (first) {
				if (Character.isJavaIdentifierStart(c)) {
					first = false;
					ref.append(c);
				} else {
					break;
				}
			} else {
				if (Character.isJavaIdentifierPart(c)) {
					ref.append(c);
				} else {
					break;
				}
			}
			c = ret.getRemaining().next();
		}
		if (first) ret = null;
		if (c!=0) ret.getRemaining().backtrack();
		ret.getResult().append(ref.toString());
		return ret;
	}
	
	protected boolean isKeyword(String identifier) {
		for(String s : JaEvalConst.keywords) {
			if (identifier.trim().equals(s)) return true;
		}
		return false;
	}

	protected JaEvalResult readNumberLiteral(JaEvalResult currentResult) {
		JaEvalResult ret = currentResult.createNew();

		boolean first = true;
		boolean decimal = false;
		while(ret.getRemaining().getRemaining()>0) {
			char c;
			if (first) c = ret.getRemaining().nextNonWs();
			else c = ret.getRemaining().next();
			if (c==0) break;
			if ((c=='.') && (decimal)) return null;
			if (c=='.') decimal = true;
			if ((!Character.isDigit(c)) && (c!='.')) {
				ret.getRemaining().backtrack();
				break;
			}
			ret.getResult().append(c);
		}
		
		if (ret.getResult().length()==0) ret = null;
		
		return ret;
	}
	
	protected JaEvalResult readStringLiteral(JaEvalResult currentResult) {
		JaEvalResult ret = currentResult.createNew();
		
		char first = ret.getRemaining().nextNonWs();
		boolean escaped = false;
		if ((first!='"') && (first!='\'')) return null;
		ret.getResult().append(first);
		char next = ret.getRemaining().next();
		while((next!=first) || (escaped)) {
			if (next==0) {
				return null;
			}
			if (next==first) escaped = false;
			ret.getResult().append(next);
			if (next=='\\') {
				if (escaped) escaped = false;
				else escaped = true;
			}
			next = ret.getRemaining().next();
		}
		ret.getResult().append(first);
		
		return ret;
	}

	/*
	protected JaEvalResult readExpression(JaEvalResult currentResult,boolean readTilEnd,String startIgnore) {
		JaEvalResult res = null;

		for(String s : JaEvalConst.expr) {
			res = readPattern(s,currentResult,readTilEnd, startIgnore);
			if (res!=null) return res;
		}
		return null;
	}
	*/

	protected JaEvalResult readPattern(String pattern,JaEvalResult currentResult,boolean readTilEnd,String startIgnore) {
		JaEvalResult ret = currentResult.createNew();
		int end = 0;
		int prevEnd = -1;

		if (startIgnore!=null) {
			if (pattern.startsWith("$"+startIgnore+"$")) return null;
		}
		int i = pattern.indexOf('$');
		while(i>=0) {
			if (i>prevEnd) {
				String skip = pattern.substring(prevEnd+1, i);
				for(int j=0;j<skip.length();j++) {
					char nextSkip = skip.charAt(j);
					if (Character.isWhitespace(nextSkip)) {
						if (!Character.isWhitespace(ret.getRemaining().next())) return null;
						char t = ret.getRemaining().nextNonWs();
						if (t!=0) ret.getRemaining().backtrack();
						ret.getResult().append(' ');
					} else {
						char test = ret.getRemaining().nextNonWs();
						if (test!=skip.charAt(j)) {
							return null;
						}
						ret.getResult().append(test);
					}
				}
			}
			end = pattern.indexOf('$', i+1);
			String name = pattern.substring(i+1, end);
			JaEvalResult sub;
			if (name=="codeLines") {
				int x = end;
				String str = "";
				while((x<pattern.length()) && (pattern.charAt(x)!='$')) {
					str = str + pattern.charAt(x);
					x++;
				}
				if (str.equals("")) str = null;
				// Read a code block
				sub = readCodeBlock(ret, str);
			} else {
				// Look for name
				if (i==0) {
					sub = readPart(name,ret,name);
				} else {
					sub = readPart(name,ret,null);
				}
			}
			if (sub==null) {
				return null;
			}
			else ret.merge(sub,true);
			if (ret.getErrorMessage()!=null) {
				return ret;
			}
			prevEnd = end;
			i = pattern.indexOf('$',end+1);
		}
		
		// Handle the end of the expression
		if (prevEnd<pattern.length()-1) {
			String endStr;
			if (prevEnd>=0) endStr = pattern.substring(prevEnd+1);
			else endStr = pattern;
			boolean first = true;
			while(endStr.length()>0) {
				char c;
				if (first) c = ret.getRemaining().nextNonWs();
				else c = ret.getRemaining().next();
				char pc = endStr.charAt(0);
				if (c!=pc) {
					return null;
				}
				endStr = endStr.substring(1);
				ret.getResult().append(c);
			}
		}
		if (readTilEnd) {
			// This is not a match if the code still has non-WS
			if (ret.getRemaining().nextNonWs()!=0) {
				return null;
			}
		}

		return ret;
	}
	
}

