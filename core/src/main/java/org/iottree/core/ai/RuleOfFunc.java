package org.iottree.core.ai;

/**
 * fix func rule
 * 
 * it's running is not depend LLM,but can create by LLM
 * 
 * @author jason.zhu
 */
public class RuleOfFunc extends Rule
{
	String desc ; // control rule description,it can push to llm to create js_code
	String js_code ;
	
	@Override
	public boolean checkRule(AIRole role)
	{
		return false;
	}

	public String getJsCode()
	{
		return this.js_code ;
	}
}
