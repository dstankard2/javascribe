package net.sf.javascribe.patterns.view.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.expressions.ExpressionUtil;

public class JavascriptEvaluator {
	private List<String> impliedVars = new ArrayList<String>();
	private String code = null;
	CodeExecutionContext execCtx = null;

	public JavascriptEvaluator(String code,CodeExecutionContext execCtx) {
		this.code = code;
		this.execCtx = execCtx;
	}
	
	public JavascriptEvaluator addImpliedVariable(String name) {
		impliedVars.add(name);
		return this;
	}
	
	public JavascriptEvalResult evalCodeBlock() {
		JavascriptEvalResult ret = null;
		JavascriptEvalResult res = JavascriptEvalResult.newInstance(code.trim());
		
		ret = readCodeBlock(res, null);
		if (ret==null) {
			ret = res;
			res.setErrorMessage("Couldn't eval code block '"+code+"'");
		}
		
		return ret;
	}
	
	public JavascriptEvalResult evalExpression() {
		JavascriptEvalResult res = JavascriptEvalResult.newInstance(code);
		JavascriptEvalResult ret = null;
		
		ret = readExpression(res, null, null);
		if (ret==null) {
			ret = res;
			ret.setErrorMessage("Couldn't evaluate Javascript expression '"+code+"'");
		}
		return ret;
	}
	
	// Ending = null -> There should be no more non-whitespace.
	// Ending is "" -> Return true always
	// Ending is not empty -> current.getRemaining should start with ending
	protected boolean testEnding(JavascriptEvalResult current,String ending) {
		boolean ret = false;
		if (current!=null) {
			current.getRemaining().skipWs();
			if (ending==null) {
				ret = (current.getRemaining().next(false)==0);
			} else {
				if (ending.equals("")) ret = true;
				else return (current.getRemaining().startsWith(ending));
			}
		}
		return ret;
	}
	
	// If ending==null then read all remaining characters.
	// If ending is "" then read as much as possible.
	// If ending is non-zero-length string, then return a result if the remaining starts with ending
	// Entry point of reading a string
	protected JavascriptEvalResult readPattern(String pattern,JavascriptEvalResult current,String ending) {
		JavascriptEvalResult ret = current.createNew();
		
		int end = 0;
		int prevEnd = -1;

		ret.getRemaining().skipWs();
		int i = pattern.indexOf('$');
		while(i>=0) {
			if (i>prevEnd) {
				String skip = pattern.substring(prevEnd+1, i);
				for(int j=0;j<skip.length();j++) {
					char nextSkip = skip.charAt(j);
					if (nextSkip==' ') {
						char t = ret.getRemaining().next(true);
						if ((t==0) || (!Character.isWhitespace(t))) return null;
						ret.getRemaining().skipWs();
						ret.getResult().append(' ');
					} else if (nextSkip=='_') {
						ret.getRemaining().skipWs();
					} else {
						char test = ret.getRemaining().next(true);
						if (test!=skip.charAt(j)) {
							return null;
						}
						ret.getResult().append(test);
					}
				}
			}
			ret.getRemaining().skipWs();
			end = pattern.indexOf('$', i+1);
			String name = pattern.substring(i+1, end);

			String skip = null;
			if (i==0) {
				skip = pattern.substring(0, end+1);
			}
			
			JavascriptEvalResult sub = null;
			String str = "";
			int x = end+1;
			if (x>=pattern.length()) {
				str = "";
			} else {
				while((x<pattern.length()) && (pattern.charAt(x)!='$')) {
					char c = pattern.charAt(x);
					if ((c=='_') || (c==' ')) {
						break;
					}
					str = str + pattern.charAt(x);
					x++;
				}
			}
			sub = readPart(name, ret, skip, str);
			if (sub==null) {
				return null;
			}
			else ret.merge(sub,true);
			if (ret.getErrorMessage()!=null) {
				return ret;
			}
			ret.getRemaining().skipWs();
			prevEnd = end;
			i = pattern.indexOf('$',end+1);
		}
		
		// Handle the end of the expression
		if (prevEnd<pattern.length()-1) {
			String endStr;
			if (prevEnd>=0) endStr = pattern.substring(prevEnd+1);
			else endStr = pattern;
			while(endStr.length()>0) {
				char pc = endStr.charAt(0);
				if (pc=='_') {
					ret.getRemaining().skipWs();
				} else if (pc==' ') {
					char c = ret.getRemaining().next(true);
					if ((c==0) || (!Character.isWhitespace(c))) return null;
					ret.getResult().append(' ');
					ret.getRemaining().skipWs();
				} else {
					char c = ret.getRemaining().next(true);
					if (c!=pc) {
						return null;
					}
					ret.getResult().append(c);
				}
				endStr = endStr.substring(1);
			}
		}
		if (!testEnding(ret,ending)) {
			return null;
		}
		
		return ret;
	}
	
	protected JavascriptEvalResult readPart(String name,JavascriptEvalResult current,String startIgnore,String ending) {
		JavascriptEvalResult ret = null;

		if (name.equals("number")) ret = readNumberLiteral(current);
		else if (name.equals("thenElse")) ret = readThenElse(current);
		else if (name.equals("string")) ret = readStringLiteral(current);
		else if (name.equals("identifier")) ret = readIdentifier(current,false);
		else if (name.equals("forLoop")) ret = readForLoop(current);
		else if (name.equals("varRef")) ret = readVariableReference(current,true);
		else if (name.equals("fnArgs")) ret = readFnArgs(current);
		else if (name.equals("declaration")) ret = readDeclaration(current);
		else if (name.equals("assignment")) ret = readAssignment(current);
		else if (name.equals("codeLines")) ret = readCodeBlock(current, ending);
		else if (name.equals("codeLine")) ret = readCodeLine(current);
		else if (name.equals("expr")) ret = readExpression(current,startIgnore,ending);
		else if (name.equals("objDef")) ret = readObjDef(current);
		else if (name.equals("arrayDef")) ret = readArrayDef(current);
		else if (name.equals("comment")) ret = readComment(current);

		if (!testEnding(ret,ending)) {
			ret = null;
		}
		return ret;
	}
	
	protected JavascriptEvalResult readComment(JavascriptEvalResult current) {
		/*
		JavascriptEvalResult ret = current.createNew();
		
		ret.getRemaining().skipWs();
		if (ret.getRemaining().startsWith("//")) {
			// skip until after \n
		}
		*/

		return null;
	}
	
	protected JavascriptEvalResult readObjDef(JavascriptEvalResult current) {
		JavascriptEvalResult ret = current.createNew();

		ret.getRemaining().skipWs();
		if (!ret.getRemaining().startsWith('{')) return null;
		ret.getResult().append('{');
		ret.getRemaining().skip(1);
		boolean done = false;

		ret.getRemaining().skipWs();
		if (ret.getRemaining().next(false)=='}') {
			ret.getResult().append(ret.getRemaining().next(true));
			return ret;
		}
		while(!done) {
			JavascriptEvalResult r = readIdentifier(ret, false);
			if ((r==null) || (r.getErrorMessage()!=null)) return r;
			ret.merge(r, true);
			ret.getRemaining().skipWs();
			if (!ret.getRemaining().startsWith(':')) return null;
			ret.getResult().append(':');
			ret.getRemaining().skip(1);
			r = readExpression(ret, null, ",");
			if (r==null) {
				r = readExpression(ret, null, "}");
			}
			if ((r==null) || (r.getErrorMessage()!=null)) return r;
			ret.merge(r, true);
			ret.getRemaining().skipWs();
			char c = ret.getRemaining().next(true);
			ret.getResult().append(c);
			if (c=='}') {
				done = true;
			} else if (c!=',') {
				ret.setErrorMessage("Invalid object definition in code expression");
				return ret;
			}
		}
		
		return ret;
	}

	// Start of evaluations
	protected JavascriptEvalResult readArrayDef(JavascriptEvalResult current) {
		JavascriptEvalResult ret = current.createNew();

		ret.getRemaining().skipWs();
		if (!ret.getRemaining().startsWith('[')) return null;
		ret.getResult().append('[');
		ret.getRemaining().skip(1);
		boolean done = false;
		
		ret.getRemaining().skipWs();
		if (ret.getRemaining().next(false)==']') {
			ret.getResult().append(ret.getRemaining().next(true));
			return ret;
		}
		while(!done) {
			JavascriptEvalResult r = readExpression(ret, null,",");
			if (r==null) {
				r = readExpression(ret,null,"]");
				if (r!=null) {
					if (r.getErrorMessage()!=null) return r;
					done = true;
				}
				else return null;
			} else if (r.getErrorMessage()!=null) return r;
			ret.merge(r, true);
			ret.getRemaining().skipWs();
			ret.getResult().append(ret.getRemaining().next(true));
		}
		
		return ret;
	}

	protected JavascriptEvalResult readExpression(JavascriptEvalResult current,String startIgnore,String ending) {
		JavascriptEvalResult ret = current.createNew();

		current = current.createNew();
		current.getRemaining().skipWs();
		for(String s : JavascriptEvalConst.expr) {
			if (startIgnore!=null) {
				if (s.startsWith(startIgnore)) continue;
			}
			ret = readPattern(s, current, ending);
			if (ret!=null) break;
		}

		return ret;
	}
	
	// Reads a portion after "if (expr)"
	protected JavascriptEvalResult readThenElse(JavascriptEvalResult current) {
		JavascriptEvalResult ret = current.createNew();
		boolean done = false;
		JavascriptEvalResult sub = null;
		boolean elseDone = false;
		
		while(!done) {
			// Read some code lines
			ret.getRemaining().skipWs();
			sub = readPattern("{$codeLines$}",ret,"");
			if (sub==null) sub = readPattern("$codeLine$",ret,"");
			// If there are no codelines then the structure is invalid.
			if (sub==null) {
				ret.setErrorMessage("Couldn't read if then else structure");
				return ret;
			}
			ret.merge(sub, true);
			// We're done after reading codelines for the else.
			if (elseDone) {
				done = true;
			} else {
				ret.getRemaining().skipWs();
				// Read else if, else
				sub = readPattern("else if ($expr$)",ret,"");
				if (sub==null) {
					sub = readPattern("else ",ret,"");
					if (sub!=null) {
						elseDone = true;
					} else {
						// Done with the if then else structure
						done = true;
					}
				}
				if (sub!=null) {
					ret.merge(sub, true);
				}
			}
		}
		return ret;
	}
	
	protected JavascriptEvalResult readCodeBlock(JavascriptEvalResult current,String ending) {
		JavascriptEvalResult ret = current.createNew();

		ret.getRemaining().skipWs();
		//boolean done = ((ret.getRemaining().next(false)==0) 
		//		|| ((ending==null) || (ending.length()==0) || (ret.getRemaining().startsWith(ending))));
		//while(!done) {
		while((ret.getRemaining().next(false)!=0) 
				&& ((ending==null || ending.length()==0) || (!ret.getRemaining().startsWith(ending)))) {
			JavascriptEvalResult sub = readCodeLine(ret);
			if (sub==null) return null;
			if (sub.getErrorMessage()!=null) return sub;
			ret.merge(sub,true);
			ret.getResult().append('\n');
			ret.getRemaining().skipWs();
		}
		return ret;
	}
	
	protected JavascriptEvalResult readCodeLine(JavascriptEvalResult current) {
		JavascriptEvalResult ret = null;

		for(String s : JavascriptEvalConst.codeLine) {
			ret = readPattern(s,current,"");
			if (ret!=null) break;
		}
		
		return ret;
	}

	protected JavascriptEvalResult readAssignment(JavascriptEvalResult current) {
		JavascriptEvalResult ret = current.createNew();
		JavascriptEvalResult sub = null;
		String varRef = null;
		String value = null;
		
		sub = readVariableReference(ret,false);
		if (sub==null) return null;
		ret.merge(sub, false);
		varRef = sub.getResult().toString();
		ret.getRemaining().skipWs();
		char c = ret.getRemaining().next(true);
		if (c!='=') return null;
		ret.getRemaining().skipWs();
		sub = readExpression(ret,null,";");
		if (sub==null) return null;
		if (sub.getErrorMessage()!=null) return sub;
		ret.merge(sub, false);
		value = sub.getResult().toString();
		ret.getRemaining().skip(1); // skip ;
		String line = null;
		try {
			line = ExpressionUtil.evaluateSetExpression(varRef, value, execCtx);
			line = line.trim();
			if (!line.endsWith(";")) line = line + ";";
			line = line + '\n';
		} catch(Exception e) {
		}
		if (line==null) {
			for(String v : impliedVars) {
				try {
					line = ExpressionUtil.evaluateSetExpression(v+'.'+varRef, value, execCtx);
				} catch(Exception e) { }
				if (line!=null) break;
			}
		}
		if (line==null) {
			line = varRef+" = "+value+";";
		}
		ret.getResult().append(line);
		
		return ret;
	}
	
	protected JavascriptEvalResult readForLoop(JavascriptEvalResult current) {
		JavascriptEvalResult ret = current.createNew();
		JavascriptEvalResult sub = null;
		
		ret.getRemaining().skipWs();
		if (!ret.getRemaining().startsWith("for")) return null;
		ret.getRemaining().skip(3);
		ret.getResult().append("for");
		ret.getRemaining().skipWs();
		char c = ret.getRemaining().next(true);
		if (c!='(') return null;
		ret.getRemaining().skipWs();
		ret.getResult().append('(');
		//current = readPattern("declaration",ret,null);
		sub = readPattern("$declaration$;", ret, ";");
		if ((sub==null) || (sub.getErrorMessage()!=null)) return sub;
		ret.merge(sub, true);
		sub = readExpression(ret,null,"");
		//sub = readPart("expr",ret,null);
		if ((sub==null) || (sub.getErrorMessage()!=null)) return sub;
		ret.merge(sub,true);
		ret.getRemaining().skipWs();
		c = ret.getRemaining().next(true);
		if (c!=';') return null;
		ret.getResult().append(';');
		// Just read the third expression of the for loop and leave it as is
		c = ret.getRemaining().next(true);
		while(c!=')') {
			ret.getResult().append(c);
			c = ret.getRemaining().next(true);
		}
		ret.getResult().append(')');
		ret.getRemaining().skipWs();
		c = ret.getRemaining().next(true);
		if (c==0) return null;
		else if (c=='{') {
			ret.getResult().append('{');
			sub = readCodeBlock(ret,"}");
		} else {
			ret.getRemaining().backtrack();
			sub = readCodeLine(ret);
			//sub = readPart("codeLine", ret, null);
		}
		if ((sub==null) || (sub.getErrorMessage()!=null)) return sub;
		ret.merge(sub, true);

		return ret;
	}
	
	protected JavascriptEvalResult readFnArgs(JavascriptEvalResult current) {
		boolean first = true;
		JavascriptEvalResult ret = current.createNew();
		
		ret.getRemaining().skipWs();
		boolean done = false;
		while(!done) {
			ret.getRemaining().skipWs();
			if (ret.getRemaining().startsWith(')')) {
				done = true;
			} else {
				if (!first) {
					if (!ret.getRemaining().startsWith(',')) return null;
					ret.getRemaining().skip(1);
					ret.getResult().append(',');
				} else {
					first = false;
				}
				JavascriptEvalResult res = readExpression(ret, null, ")");
				if (res!=null) {
					ret.merge(res, true);
					done = true;
				} else {
					res = readExpression(ret,null,",");
					if (res!=null) {
						ret.merge(res, true);
					} else {
						return null;
					}
				}
			}
		}

		return ret;
	}
	
	// Reads any variable reference, without validating it.
	// If assignable is true, the variable ref can't end with a function call (can't end with ')')
	// TODO: finish
	protected JavascriptEvalResult readVariableReference(JavascriptEvalResult current,boolean evaluate) {
		JavascriptEvalResult ret = current.createNew();
		boolean idRequired = true; // Means we have to find an identifier in this loop iteration
		boolean done = false;
		
		while(!done) {
			ret.getRemaining().skipWs();
			JavascriptEvalResult id = readIdentifier(ret, true);
			if (id!=null) {
				ret.merge(id, true);
				ret.getRemaining().skipWs();
				if (ret.getRemaining().startsWith('.')) {
					ret.getResult().append('.');
					ret.getRemaining().skip(1);
					idRequired = true;
				} else {
					idRequired = false;
				}
			} else {
				done = true;
				if (idRequired) {
					ret = null;
				}
			}
		}
		
		if (ret!=null) {
			String remaining = ret.getRemaining().toString();
			String ref = ret.getResult().toString();
			ret = JavascriptEvalResult.newInstance(remaining);
			ref = getFinalRef(ref);
			if (evaluate) {
				String finalRef = null;
				try {
					finalRef = ExpressionUtil.evaluateValueExpression("${"+ref+"}", "object", execCtx);
				} catch(Exception e) {
				}
				if (finalRef==null) finalRef = ref;
				ret.getResult().append(finalRef);
			} else {
				ret.getResult().append(ref);
			}
		}
		
		return ret;
	}
	
	protected String getFinalRef(String ref) {
		String type = null;

		for(String s : JavascriptEvalConst.keywords) {
			if (ref.equals(s)) return ref;
		}
		
		try {
			type = execCtx.evaluateTypeForExpression(ref);
			if (type!=null) return ref;
		} catch(Exception e) { }
		for(String s : this.impliedVars) {
			try {
				type = execCtx.evaluateTypeForExpression(s+'.'+ref);
				if (type!=null) {
					return s+'.'+ref;
				}
			} catch(Exception e) {
				
			}
		}
		
		return ref;
	}

	protected JavascriptEvalResult readDeclaration(JavascriptEvalResult current) {
		JavascriptEvalResult ret = null;
		ret = this.readPattern("var $identifier$", current, ";");
		if (ret==null) ret = this.readPattern("var $identifier$_=_$expr$", current, ";");
		return ret;
	}

	protected JavascriptEvalResult readIdentifier(JavascriptEvalResult current,boolean testFnCall) {
		JavascriptEvalResult ret = null;
		ret = current.createNew();
		boolean first = true;
		StringBuilder ref = new StringBuilder();
		
		
		
		if (testFnCall) {
			JavascriptEvalResult sub = readPattern("$identifier$($fnArgs$)",ret,"");
			if (sub!=null) {
				ret.merge(sub, true);
				return ret;
			}
		}
		
		ret.getRemaining().skipWs();
		char c = ret.getRemaining().next(true);
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
			c = ret.getRemaining().next(true);
		}
		if (first) return null;
		else {
			if (c!=0) ret.getRemaining().backtrack();
			// Check if it is a keyword
			for(String s : JavascriptEvalConst.keywords) {
				if (ref.toString().trim().equals(s)) return null;
			}
			ret.getResult().append(ref.toString());
		}
		ret.getRemaining().skipWs();
		if (ret.getRemaining().startsWith('[')) {
			JavascriptEvalResult sub = readPattern("[$expr$]",ret,"");
			if (sub==null) {
				ret.setErrorMessage("Invalid array or property reference");
			} else {
				ret.merge(sub, true);
			}
		}
		
		return ret;
	}

	protected JavascriptEvalResult readNumberLiteral(JavascriptEvalResult current) {
		JavascriptEvalResult ret = current.createNew();

		boolean decimal = false;
		ret.getRemaining().skipWs();
		while(ret.getRemaining().toString().length()>0) {
			char c;
			c = ret.getRemaining().next(true);
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

	protected JavascriptEvalResult readStringLiteral(JavascriptEvalResult current) {
		JavascriptEvalResult ret = current.createNew();
		
		ret.getRemaining().skipWs();
		char first = ret.getRemaining().next(true);
		boolean escaped = false;
		if ((first!='"') && (first!='\'')) return null;
		ret.getResult().append(first);
		char next = ret.getRemaining().next(true);
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
			next = ret.getRemaining().next(true);
		}
		ret.getResult().append(first);
		
		return ret;
	}
	
}
