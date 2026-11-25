package org.iottree.core.aiagent;

import org.iottree.core.UAVal;

public class EmbeddingField
{
	public String name ;
	
	public UAVal.ValTP vt ;
	
	public String idx ;
	
	public EmbeddingField(String name,UAVal.ValTP vt)
	{
		this.name = name ;
		this.vt = vt ;
	}
	
	public EmbeddingField asIdx(String idx)
	{
		this.idx = idx ;
		return this ;
	}
}
