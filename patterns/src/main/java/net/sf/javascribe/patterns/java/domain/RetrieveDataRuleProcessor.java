package net.sf.javascribe.patterns.java.domain;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.PropertyEntry;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.types.ServiceOperation;
import net.sf.javascribe.langsupport.java.JavaClassSourceFile;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.types.JavaVariableType;
import net.sf.javascribe.langsupport.java.types.impl.JavaServiceType;
import net.sf.javascribe.patterns.xml.java.domain.RetrieveDataRule;

@Plugin
public class RetrieveDataRuleProcessor implements ComponentProcessor<RetrieveDataRule> {
	ProcessorContext ctx;
	RetrieveDataRule comp;
	
	@Override
	public void process(RetrieveDataRule component, ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Java8");
		this.ctx = ctx;
		this.comp = component;
		JavaClassSourceFile src = DomainRuleUtils.getServiceSourceFile(comp, ctx);
		JavaServiceType serviceType = DomainRuleUtils.getServiceType(comp, ctx);
		CodeExecutionContext execCtx = new CodeExecutionContext(ctx);

		String attrib = comp.getReturnAttribute();
		String params = comp.getParams();
		String ruleName = comp.getName();

		if (ruleName.equals(attrib)) {
			throw new JavascribeException("Couldn't determine rule name from attribute name '"+attrib+"'");
		}
		String attribTypeName = ctx.getSystemAttribute(attrib);
		if (attribTypeName==null) {
			throw new JavascribeException("Couldn't determine type for system attribute '"+attrib+"'");
		}
		JavaVariableType attribType = JavascribeUtils.getType(JavaVariableType.class, attribTypeName, ctx);

		MethodSource<JavaClassSource> methodSrc = src.getSrc().addMethod();
		ServiceOperation op = new ServiceOperation(ruleName);
		op.returnType(attribTypeName);
		methodSrc.setName(ruleName);
		methodSrc.setReturnType(attribType.getImport());
		methodSrc.setPublic();
		JavaCode code = new JavaCode();
		
		List<PropertyEntry> ruleParams = JavascribeUtils.readParametersAsList(params, ctx);
		if (ruleParams.size()==0) {
			ctx.getLog().warn("Found no parameters for rule - ensure that the 'params' XML attribute is specified");
		}
		for(PropertyEntry entry : ruleParams) {
			String key = entry.getName();
			JavaVariableType type = (JavaVariableType)entry.getType();
			op.addParam(key, type.getName());
			src.addImport(type);
			methodSrc.addParameter(type.getClassName(), key);
			execCtx.addVariable(key, type.getName());
		}
		code.append(attribType.declare(attrib, execCtx));

		Map<String,JavaServiceType> serviceRefs = DomainRuleUtils.getDependencyRefs(comp, ctx);
		DomainDataRuleSet currentRules = DomainRuleUtils.getDomainDataRules(ctx);
		// Add this to the service refs to use rules that have already been defined
		serviceRefs.put("this", serviceType);
		
		JavaCode bodyCode = internalFindAttribute(0,attrib,execCtx,serviceRefs,currentRules);
		if (bodyCode==null) {
			throw new JavascribeException("Unable to resolve rule - unable to find attribute '"+attrib+"'");
		}

		code.append(bodyCode);
		code.appendCodeText("return "+attrib+";");
		methodSrc.setBody(code.getCodeText());
		src.addImports(code);
		serviceType.addOperation(op);
	}

	protected JavaCode internalFindAttribute(int level,String attrib,CodeExecutionContext execCtx,Map<String,JavaServiceType> serviceRefs, DomainDataRuleSet currentRules) throws JavascribeException {
		JavaCode ret = null;

		level++;
		if (level>=4) {
			return null;
		}

		// Is the attribute in the current execCtx?
		if (execCtx.getTypeForVariable(attrib)!=null) {
			return new JavaCode();
		}
		ret = findInCurrentRules(level,attrib,execCtx,serviceRefs,currentRules);
		if (ret==null) {
			ret = findInDependencies(level,attrib,execCtx,serviceRefs,currentRules);
		}
		
		if (ret==null) {
			ctx.getLog().warn("Unable to locate attribute '"+attrib+"' in current code execution context");
		}
		return ret;
	}

	/* TODO: Implement this
	// Params tenantId
	// Resolve clientOverviewDataList
	// translator.getClientOverviewData
	// clientDao.getClients
	protected JavaCode resolveList(int level, String attrib, CodeExecutionContext execCtx, Map<String,JavaServiceType> serviceRefs, DomainDataRuleSet currentRules) throws JavascribeException {
		JavaCode ret = null;

		String single = JavascribeUtils.getSingle(attrib);
		if (single!=null) {
			List<Entry<String,JavaServiceType>> validRules = new ArrayList<>();
			String getName = "get"+JavascribeUtils.getUpperCamelName(single);
			String findName = "find"+JavascribeUtils.getUpperCamelName(single);
			
			for(Entry<String,JavaServiceType> entry : serviceRefs.entrySet()) {
				String ref = entry.getKey();
				JavaServiceType serviceType = entry.getValue();
				
			}
		}

		return ret;
	}
	*/
	
	protected JavaCode findInDependencies(int level,String attrib,CodeExecutionContext execCtx,Map<String,JavaServiceType> serviceRefs, DomainDataRuleSet currentRules) throws JavascribeException {
		JavaCode ret = null;
		String getName = "get"+JavascribeUtils.getUpperCamelName(attrib);
		String findName = "find"+JavascribeUtils.getUpperCamelName(attrib);
		
		for(Entry<String,JavaServiceType> entry : serviceRefs.entrySet()) {
			String ref = entry.getKey();
			JavaServiceType serviceType = entry.getValue();
			
			List<ServiceOperation> getOperations = serviceType.getOperations(getName);
			for(ServiceOperation op : getOperations) {
				ret = invokeOperation(level, attrib, execCtx, serviceRefs, currentRules, ref, op);
				if (ret!=null) {
					break;
				}
			}
			if (ret==null) {
				List<ServiceOperation> findOperations = serviceType.getOperations(findName);
				for(ServiceOperation op : findOperations) {
					ret = invokeOperation(level, attrib, execCtx, serviceRefs, currentRules, ref, op);
					if (ret!=null) {
						break;
					}
				}
			}
			if (ret!=null) {
				break;
			}
		}

		return ret;
	}
	
	protected JavaCode findInCurrentRules(int level,String attrib,CodeExecutionContext execCtx,Map<String,JavaServiceType> serviceRefs, DomainDataRuleSet currentRules) throws JavascribeException {
		JavaCode ret = null;
		//Map<String,String> ruleParams = null;

		List<DomainDataRule> rules = currentRules.findRulesForAttribute(attrib);
		for(DomainDataRule rule : rules) {
			if (execCtx.getVariableType(rule.getServiceRef())!=null) {
				// For each rule, find the parameters as attributes.  If they are all found, attempt to invoke the rule.
				for(ServiceOperation op : rule.getOperations()) {
					ret = invokeOperation(level, attrib, execCtx, serviceRefs, currentRules, rule.getServiceRef(), op);
					if (ret!=null) {
						break;
					}
				}
				if (ret!=null) {
					break;
				}
			}
		}

		return ret;
	}

	private JavaCode invokeOperation(int level, String attrib, CodeExecutionContext execCtx,
			Map<String, JavaServiceType> serviceRefs, DomainDataRuleSet currentRules, String ref,
			ServiceOperation op) throws JavascribeException {
		JavaCode ret;
		ret = new JavaCode();
		for(String param : op.getParamNames()) {
			if (execCtx.getTypeForVariable(param)==null) {
				String paramTypeName = op.getParamType(param);
				JavaVariableType paramType = execCtx.getType(JavaVariableType.class, paramTypeName);
				ret.append(paramType.declare(param, execCtx));
				JavaCode append = internalFindAttribute(level, param, execCtx, serviceRefs, currentRules);
				if (append!=null) {
					ret.append(append);
					execCtx.addVariable(param, paramTypeName);
				} else {
					ret = null;
					break;
				}
			}
		}
		if (ret!=null) {
			JavaCode invoke = JavaUtils.callJavaOperation(attrib, ref, op, execCtx, null);
			if (invoke!=null) {
				ret.append(invoke);
			} else {
				ret = null;
			}
		}
		return ret;
	}

}

