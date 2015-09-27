package net.sf.javascribe.patterns.view.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;

public class JaEval2 {
	
	private List<String> impliedVars = new ArrayList<String>();
	private String code = null;
	private boolean exprOnly = false;
	CodeExecutionContext execCtx = null;

	public JaEval2(String code,CodeExecutionContext execCtx) {
		this.code = code;
		this.execCtx = execCtx;
	}
	
	public void addImpliedVariable(String name) {
		impliedVars.add(name);
	}

	public JaEvalResult parseExpression() {
		exprOnly = true;
		JaEvalResult res = JaEvalResult.newInstance(code,true);
		return readExpression(res,true,null);
	}
	
	public JaEvalResult parseCodeBlock() {
		exprOnly = false;
		JaEvalResult res = JaEvalResult.newInstance(code, false);
		res.getRemaining().nextNonWs();
		res.getRemaining().backtrack();
		return readCodeBlock(res,null);
	}
	
	protected JaEvalResult readCodeBlock(JaEvalResult currentResult,String ending) {
		JaEvalResult ret = null;
		JaEvalResult current = null;

		ret = currentResult.createNew(false);

		while(ret.getRemaining().getRemaining()>0) {
			current = readCodeLine(ret);
			if (current.getErrorMessage()!=null) return current;
			ret.merge(current);
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
		
		return ret;
	}
	
	protected JaEvalResult readPart(String name,JaEvalResult currentResult,String startIgnore) {
		String[] patterns = null;
		JaEvalResult ret = null;
		
		if (name.equals("number")) ret = readNumberLiteral(currentResult);
		else if (name.equals("expr")) ret = readExpression(currentResult, false, startIgnore);
		else if (name.equals("string")) ret = readStringLiteral(currentResult);
		else if (name.equals("identifier")) ret = readIdentifier(currentResult);
		else if (name.equals("fnCall")) ret = readFunctionCall(currentResult);
		else if (name.equals("varRef")) ret = readVarRef(currentResult);
		else {
			if (name.equals("codeLine")) patterns = JaEvalConst.codeLine;
			else {
				throw new RuntimeException("Couldn't do an eval because a pattern contained '"+name+"'");
			}
			for(String s : patterns) {
				JaEvalResult res = readPattern(s,currentResult,false,startIgnore);
				if (res==null) continue;
				ret = currentResult;
				ret.merge(res);
			}
		}
		
		return ret;
	}
	
	protected JaEvalResult readFunctionCall(JaEvalResult currentResult) {
		JaEvalResult ret = currentResult.createNew(true);

		return null;
	}
	
	protected JaEvalResult readVarRef(JaEvalResult currentResult) {
		JaEvalResult ret = currentResult.createNew(true);
		String ref = readVariableReference(ret);
		if (ref==null) {
			return null;
		}
		ret.getResult().append(ref);
		return ret;
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
				currentResult.getRemaining().toNextNonWs();
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
			/*
			if ((first) && (!Character.isJavaIdentifierStart(c))) return null;
			else if (!first) {
				if (c=='.') {
					first = true;
				} else {
					if (!Character.isJavaIdentifierPart(c)) {
						currentResult.getRemaining().backtrack();
						return build.toString();
					} else {
						build.append(c);
					}
				}
			} else {
				first = false;
				build.append(c);
			}
			*/
		} while(currentResult.getRemaining().getRemaining()>0);
		
		return build.toString();
	}
	
	protected JaEvalResult readIdentifier(JaEvalResult currentResult) {
		JaEvalResult ret = null;
		//ret = currentResult.createNew(true);
		
		return ret;
	}

	protected JaEvalResult readNumberLiteral(JaEvalResult currentResult) {
		JaEvalResult ret = currentResult.createNew(true);

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
		JaEvalResult ret = currentResult.createNew(exprOnly);
		
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

	protected JaEvalResult readExpression(JaEvalResult currentResult,boolean readTilEnd,String startIgnore) {
		JaEvalResult res = null;

		for(String s : JaEvalConst.expr) {
			res = readPattern(s,currentResult,readTilEnd, startIgnore);
			if (res!=null) {
				return res;
			}
		}
		return null;
	}

	protected JaEvalResult readPattern(String pattern,JaEvalResult currentResult,boolean readTilEnd,String startIgnore) {
		JaEvalResult ret = currentResult.createNew(exprOnly);
		int end = 0;
		int prevEnd = -1;
		boolean sendStartIgnore = false;

		if (startIgnore!=null) {
			if (pattern.startsWith("$"+startIgnore+"$")) return null;
		} else {
			if (pattern.startsWith("$")) sendStartIgnore = true;
		}
/*
		System.out.print("a");
		System.out.print("b");
		System.out.print("c");
*/
		int i = pattern.indexOf('$');
		while(i>=0) {
			if (i>prevEnd) {
				String skip = pattern.substring(prevEnd+1, i);
				for(int j=0;j<skip.length();j++) {
					char test = ret.getRemaining().nextNonWs();
					if (test!=skip.charAt(j)) {
						return null;
					}
					ret.getResult().append(test);
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
				if (sendStartIgnore) {
					sendStartIgnore = false; // ??
					sub = readPart(name,ret,name);
				} else {
					sub = readPart(name,ret,null);
				}
			}
			if (sub==null) {
				return null;
			}
			else ret.merge(sub);
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

