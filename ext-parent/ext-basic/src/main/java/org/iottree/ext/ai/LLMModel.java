package org.iottree.ext.ai;


/**
 * LLM Model
 * 
 * @author jason.zhu
 */
public abstract class LLMModel
{
	public abstract String getName();
	
	public abstract String getModel();
	
	public abstract long getSize();
}
