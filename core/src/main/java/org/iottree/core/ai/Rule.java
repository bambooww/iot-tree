package org.iottree.core.ai;

import org.iottree.core.util.IdCreator;

public abstract class Rule
{
	String uid = null ;
	
	long create_dt ;
	
	long update_dt ;
	
	public Rule()
	{
		this.uid = IdCreator.newSeqId() ;
		this.create_dt = this.update_dt = System.currentTimeMillis() ;
	}
	
	public String getUID()
	{
		return this.uid ;
	}
	
	public abstract boolean checkRule(AIRole role) ;
}
