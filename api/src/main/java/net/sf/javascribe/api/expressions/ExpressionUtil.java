package net.sf.javascribe.api.expressions;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.AttributeHolder;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.VariableType;
import net.sf.javascribe.api.types.BooleanType;
import net.sf.javascribe.api.types.IntegerType;
import net.sf.javascribe.api.types.StringType;

public class ExpressionUtil {

	public static ValueExpression buildExpressionFromCode(String code,String type) {
		ValueExpression ret = new ValueExpression();
		ret.addAtom(new CodeFragmentExpressionAtom(code, type));
		ret.setType(type);
		return ret;
	}
	
	public static String evaluateValueExpression(String expr,String targetType,CodeExecutionContext execCtx) throws JavascribeException {
		ValueExpression exp = buildValueExpression(expr,targetType,execCtx);
		return getEvaluatedExpression(exp, execCtx);
	}
	
	public static String evaluateBooleanExpression(String expr,CodeExecutionContext execCtx) throws JavascribeException {
		ValueExpression exp = buildBooleanExpression(expr,execCtx);
		return getEvaluatedExpression(exp, execCtx);
	}
	
	public static ValueExpression buildValueExpression(String expr,String targetType,CodeExecutionContext execCtx) throws JavascribeException {
		ValueExpression ret = new ValueExpression();
        List<String> atomStrings = null;

        ret.setType(targetType);
        atomStrings = findValueExpressionAtoms(expr);
        for(String currentAtom : atomStrings) {
            if (currentAtom.indexOf("${")==0) {
                VarReferenceExpressionAtom var = null;
                String actualAtom = currentAtom.substring(2,currentAtom.length()-1);
                var = evaluateVarRefAtom(actualAtom,null,execCtx);
                ret.addAtom(var);
                if ((targetType==null) || (targetType.equals("object"))) {
                	if (var.getType()==null)
                		var.setType(var.getFinalType());
                }
            } else {
                ConstantReferenceExpressionAtom con = null;
                con = new ConstantReferenceExpressionAtom(currentAtom,targetType);
                ret.addAtom(con);
                if ((targetType==null) || (targetType.equals("object"))) {
                	IntegerType ti = (IntegerType)execCtx.getType("integer");
                	if (ti.isValidIntegerAtom(currentAtom)) {
                		con.type = "integer";
                	} else {
                		BooleanType bi = (BooleanType)execCtx.getType("boolean");
                		if (bi.isValidBooleanAtom(currentAtom)) {
                			con.type = "boolean";
                		} else {
                			con.type = "string";
                		}
                	}
                }
            }
        }
        
        return ret;
	}
	
	public static ValueExpression buildBooleanExpression(String expr,CodeExecutionContext execCtx) throws JavascribeException {
		ValueExpression ret = new ValueExpression();
		String op = null;
		String left = null;
		String right = null;
		
		BooleanExpressionAtom a = new BooleanExpressionAtom();
		ret.addAtom(a);
		if (expr.indexOf("==")>0) {
			int i = expr.indexOf("==");
			op = "==";
			left = expr.substring(0, i);
			right = expr.substring(i+2);
		} else if (expr.indexOf(">=")>0) {
			int i = expr.indexOf(">=");
			op = ">=";
			left = expr.substring(0, i);
			right = expr.substring(i+2);
		} else if (expr.indexOf("<=")>0) {
			int i = expr.indexOf("<=");
			op = "<=";
			left = expr.substring(0, i);
			right = expr.substring(i+2);
		} else if (expr.indexOf(">")>0) {
			int i = expr.indexOf(">");
			op = ">";
			left = expr.substring(0, i);
			right = expr.substring(i+1);
		} else if (expr.indexOf("<")>0) {
			int i = expr.indexOf("<");
			op = "<";
			left = expr.substring(0, i);
			right = expr.substring(i+1);
		} else throw new JavascribeException("Found invalid boolean expression '"+expr+"'");
		a.setOperator(BooleanOperator.findOperator(op));
		a.setLeftOperand(ExpressionUtil.buildValueExpression(left, "object", execCtx));
		a.setRightOperand(ExpressionUtil.buildValueExpression(right, "object", execCtx));
		ret.setType("boolean");
		
		return ret;
	}
	
    /**
     * Sets the given variable (in the given context) to the value expressed by the JADL value expression.
     * @param var A JADL variable reference, without "${}"
     * @param value A JADL value expression to set the var to.
     * @param execCtx Current code execution context
     * @return String that expresses the result of setting the variable.
     * @throws CodeGenerationException If there is an issue.
     */
    public static String evaluateSetExpression(String var,ValueExpression value,CodeExecutionContext execCtx) throws JavascribeException {
    	StringBuffer build = new StringBuffer();
    	String evaluatedValue = null;
    	VarReferenceExpressionAtom atom = null;
    	
    	if (var.indexOf('.')>0) {
    		atom = evaluateVarRefAtom(var, null, execCtx);
    		build.append(evaluateSet(atom,value,execCtx));
    	} else {
    		String typeName = findType(var, execCtx);
    		if (typeName==null) {
    			throw new JavascribeException("Found null type name for var expression '"+var+"'");
    		}
    		evaluatedValue = getEvaluatedExpression(value, execCtx);

    		// May have to replace this later with code that can determine how to set a var value.
    		build.append(var+" = "+evaluatedValue+";\n");
    	}
    	
    	return build.toString();
    }

    public static String evaluateSet(VarReferenceExpressionAtom varToSet,ValueExpression value,CodeExecutionContext execCtx) throws JavascribeException {
    	return evaluateSet(varToSet,null,null,value,execCtx);
    }
    
	public static String getEvaluatedExpression(ValueExpression expr,CodeExecutionContext execCtx) throws JavascribeException {
        StringBuffer buf = new StringBuffer();
        VariableType type = null;
        String targetType = expr.getType();

        if (targetType==null) {
        	throw new JavascribeException("Found an expression with no type");
        }
        type = (VariableType)execCtx.getType(targetType);
        if (targetType.equals("integer")) {
        	IntegerType acc = null;
        	acc = (IntegerType)execCtx.getType("integer");
        	buf.append(acc.evaluateExpression(expr,execCtx));
        } else if (targetType.equals("string")) {
        	StringType acc = null;
        	acc = (StringType)execCtx.getType("string");
        	buf.append(acc.evaluateExpression(expr,execCtx));
        } else if (targetType.equals("boolean")) {
        	BooleanType acc = null;
        	acc = (BooleanType)execCtx.getType("boolean");
        	buf.append(acc.evaluateExpression(expr, execCtx));
        } else if ((type!=null) || (targetType.equals("object"))) {
        	if (expr.getAtomType(0).equals(ValueExpression.VAR_REF_EXPR_ATOM)) {
        		// A non-atom type.  The expr will have 1 element with a var ref atom.
        		VarReferenceExpressionAtom ref = null;
        		ref = expr.getVarReferenceExpressionEntry(0);
        		buf.append(ExpressionUtil.evaluateVarRefExpressionAtom(ref, targetType, execCtx));
        	} else if (expr.getAtomType(0).equals(ValueExpression.CONST_REF_EXPR_ATOM)) {
        		// A constant reference.  We treat it as a string unless its type is null
        		//                    ConstantReferenceExpressionAtom ref = null;
        		//                    ref = expr.getConstantReferenceExpressionEntry(0);
        		if (expr.getConstantReferenceExpressionEntry(0).getType()==null) {
        			buf.append("null");
        		} else if (expr.getConstantReferenceExpressionEntry(0).getText().equals("null")) {
        			buf.append("null");
        		} else {
        			StringType str = (StringType)execCtx.getType("string");
        			buf.append(str.evaluateExpression(expr, execCtx));
        		}
        	} else if (expr.getAtomType(0).equalsIgnoreCase(ValueExpression.CODE_FRAGMENT_EXPR_ATOM)) {
        		CodeFragmentExpressionAtom ref = expr.getCodeFragmentExpressionEntry(0);
        		buf.append(ref.getText());
        	}
        } else {
        	throw new IllegalArgumentException("Illegal type for evaluated expression: "+targetType);
        }
        
        return buf.toString();
	}

	public ValueExpression getExpression(String value,String targetType,CodeExecutionContext execCtx) {
		ValueExpression ret = null;
		
		
		return ret;
	}

	public static String findType(String varRef, CodeExecutionContext execCtx)
	throws JavascribeException {
		String ret = null;
		int i = 0;
		String temp = varRef;
		String sub = null;
		VariableType type = null;
		AttributeHolder holder = null;
		AttributeHolder parentType = null;

		i = temp.indexOf('.');
		while (i >= 0) {
			if (i == 0)
				throw new IllegalArgumentException("Found illegal expression "
						+ varRef);
			sub = temp.substring(0, i);
			if (parentType==null) {
				type = execCtx.getTypeForVariable(sub);
			} else {
				String typeName = parentType.getAttributeType(sub);
				type = execCtx.getType(typeName);
			}

			if (!(type instanceof AttributeHolder)) {
				throw new IllegalArgumentException("Found illegal expression "
						+ varRef);
			}
			holder = (AttributeHolder) type;
			if (parentType != null) {
				temp = temp.substring(i + 1);
				parentType = holder;
			} else {
				parentType = holder;
				temp = temp.substring(i + 1);
			}
			i = temp.indexOf('.');
		}
		if (parentType == null) {
			ret = execCtx.getVariableType(temp);
		} else {
			ret = parentType.getAttributeType(temp);
		}

		return ret;
	}

	public static String evaluateVarRefExpressionAtom(VarReferenceExpressionAtom a,String targetType,CodeExecutionContext execCtx) throws JavascribeException {
		return evaluateVarRefExpressionAtom(null,a,targetType,execCtx);
	}
	
	/*
	 * private utility methods for evaluating expressions.
	 */

    /**
     * Returns a list of strings, each of which is an atom in the value expression.
     * This method does no validation of atoms such as checking for valid types.
     * @param valueExpression The value expression to break into atoms.
     * This method does no checking of types in the value expression.
     * @return A list of strings
     */
    private static List<String> findValueExpressionAtoms(String valueExpression) {
        List<String> ret = null;
        int start = 0;
        
        int end = 0;

        ret = new ArrayList<String>();
        start = valueExpression.indexOf("${");
        
        while(start>=0) {
            if (start>end) {
                ret.add(valueExpression.substring(end,start));
            }
            end = valueExpression.indexOf("}",start);
            if (end<0) {
                throw new IllegalArgumentException("Invalid expression '"+valueExpression+"' specified.");
            }
            ret.add(valueExpression.substring(start,end+1));
            end++;
            start = valueExpression.indexOf("${",end);
        }
        if (valueExpression.length()>end) {
            ret.add(valueExpression.substring(end));
        }
        
        return ret;
    }

	private static String evaluateVarRefExpressionAtom(String holder,VarReferenceExpressionAtom a,String targetType,CodeExecutionContext execCtx) throws JavascribeException {
		String ret = null;
		AttributeHolder thisHolder = null;
		
		if (holder!=null) {
			if (a.getNestedProperty()!=null) {
				VarReferenceExpressionAtom ne = a.getNestedProperty();
				thisHolder = (AttributeHolder)a.getType();
				ne = a.getNestedProperty();
				ret = thisHolder.getCodeToRetrieveAttribute(holder, ne.getName(), ne.getType().getName(),execCtx);
				ret = evaluateVarRefExpressionAtom(ret,ne,targetType,execCtx);
			} else {
				ret = holder;
			}
		} else {
			if (a.getNestedProperty()!=null) {
				VarReferenceExpressionAtom ne = a.getNestedProperty();
				thisHolder = (AttributeHolder)a.getType();
				ret = thisHolder.getCodeToRetrieveAttribute(a.getName(), ne.getName(), targetType, execCtx);
				ret = evaluateVarRefExpressionAtom(ret,ne,targetType,execCtx);
			} else {
				ret = a.getName();
			}
		}
		
		return ret;
	}
	
    private static VarReferenceExpressionAtom evaluateVarRefAtom(String ref,AttributeHolder container,CodeExecutionContext execCtx) throws JavascribeException {
        VarReferenceExpressionAtom ret = null;
        VarReferenceExpressionAtom nested = null;
        int index = 0;
        
        String typeName = null;
        VariableType type = null;
        String name = null;
        String attr = null;
        AttributeHolder holder = null;
        
        index = ref.indexOf('.');
        if (index==0) {
        	throw new IllegalArgumentException("Invalid expression found: '"+ref+"'");
        } else if (index>0) {
        	name = ref.substring(0, index);
        	if (container!=null) {
        		typeName = container.getAttributeType(name);
        		if (typeName==null)
        			throw new IllegalArgumentException("Couldn't find type for attribute '"+container.getName()+'.'+name+"'");
            	type = execCtx.getTypes().getType(typeName);
            	if (!(type instanceof AttributeHolder)) {
            		throw new IllegalArgumentException("Found invalid expression '"+ref+"'");
            	}
            	attr = ref.substring(index+1);
            	holder = (AttributeHolder)type;
            	try {
                	nested = evaluateVarRefAtom(attr,holder,execCtx);
            	} catch(IllegalArgumentException e) {
                	throw new IllegalArgumentException("Invalid expression found: '"+ref+"'");
            	}
            	ret = new VarReferenceExpressionAtom(name,holder);
            	ret.setNestedProperty(nested);
        	} else {
            	typeName = execCtx.getVariableType(name);
            	if (typeName==null) {
            		throw new JavascribeException("Couldn't find type for '"+name+"' in current execution context");
            	}
            	type = execCtx.getTypes().getType(typeName);
            	if (!(type instanceof AttributeHolder)) {
            		throw new IllegalArgumentException("Found invalid expression '"+ref+"'");
            	}
            	attr = ref.substring(index+1);
            	holder = (AttributeHolder)type;
            	try {
            		nested = evaluateVarRefAtom(attr,holder,execCtx);
            	} catch(IllegalArgumentException e) {
            		throw new IllegalArgumentException("Invalid expression found: '"+ref+"'");
            	}
            	ret = new VarReferenceExpressionAtom(name,holder);
            	ret.setNestedProperty(nested);
        	}
        } else { // No attribute
        	name = ref;
        	if (container!=null) {
        		typeName = container.getAttributeType(name);
        		if (typeName==null) {
        			throw new JavascribeException("Couldn't find type for '"+container.getName()+"."+name+"'");
        		}
        		type = execCtx.getTypes().getType(typeName);
        		if (type==null)
        			throw new JavascribeException("Found an invalid attribute type '"+typeName+"'");
        		ret = new VarReferenceExpressionAtom(name,type);
        	} else {
        		typeName = execCtx.getVariableType(name);
        		if (typeName==null) {
        			throw new JavascribeException("Found an unrecognizable expression  '"+name+"'");
        		}
        		type = execCtx.getTypes().getType(typeName);
        		ret = new VarReferenceExpressionAtom(name,type);
        	}
        }
        
        
        return ret;
    }
    
    private static String evaluateSet(VarReferenceExpressionAtom varToSet,String holderName,AttributeHolder holder,ValueExpression value,CodeExecutionContext execCtx) throws JavascribeException {
    	StringBuffer buf = new StringBuffer();
    	AttributeHolder thisHolder = null;
    	
    	if (varToSet.getNestedProperty().getNestedProperty()==null) {
			thisHolder = (AttributeHolder)varToSet.getType();
			String val = getEvaluatedExpression(value, execCtx);
    		if (holderName==null) {
    			buf.append(thisHolder.getCodeToSetAttribute(varToSet.getName(), varToSet.getNestedProperty().getName(), val, execCtx));
//    			buf.append(thisHolder.getCodeToSetAttribute(varToSet.getName(), varToSet.getNestedProperty().getName(), value, execCtx));
    		} else {
    			buf.append(thisHolder.getCodeToSetAttribute(holderName, varToSet.getNestedProperty().getName(), val, execCtx));
//    			buf.append(thisHolder.getCodeToSetAttribute(holderName, varToSet.getNestedProperty().getName(), value, execCtx));
    		}
    	} else {
    		if (holderName==null) {
    			thisHolder = (AttributeHolder)varToSet.getType();
    			String str = thisHolder.getCodeToRetrieveAttribute(varToSet.getName(), varToSet.getNestedProperty().getName(), varToSet.getNestedProperty().getType().getName(),execCtx);
    			buf.append(evaluateSet(varToSet.getNestedProperty(),str,thisHolder,value,execCtx));
    		} else {
    			
    		}
    	}
    	
    	return buf.toString();
    }
    
}
