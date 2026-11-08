package org.iottree.core.ai;

import org.iottree.core.util.IdCreator;

/**
 * ai inference rule
 * 
 * using nature language,one of LLM context prompt
 * 
 * @author jason.zhu
 */
public class RuleOfInfer extends Rule
{

	@Override
	public boolean checkRule(AIRole role)
	{
		String ss=  """
				{
				"name":"aaa"
				}
				""";
		
		return false;
		
	}
	
}
