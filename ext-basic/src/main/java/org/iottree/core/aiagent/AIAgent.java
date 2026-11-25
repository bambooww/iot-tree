package org.iottree.core.aiagent;

import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;

public class AIAgent
{
	private static EmbeddingManager<UAManager> globalUAEmbedMgr = null ;
	
	public static EmbeddingManager<UAManager> getGlobalUAEmbedMgr()
	{
		if(globalUAEmbedMgr!=null)
			return globalUAEmbedMgr ;
		
		synchronized(EmbeddingManager.class)
		{
			if(globalUAEmbedMgr!=null)
				return globalUAEmbedMgr ;
			
			return globalUAEmbedMgr = new  EmbeddingManager<>(UAManager.getInstance());
		}
	}
	
	UAPrj prj ;
	
	EmbeddingManager<UAPrj> prjEmbedMgr = null ;
	
	public AIAgent(UAPrj prj)
	{
		this.prj = prj ;
	}
	
	public UAPrj getPrj()
	{
		return this.prj ;
	}
	
	public EmbeddingManager<UAPrj> getPrjEmbedMgr()
	{
		if(prjEmbedMgr!=null)
			return prjEmbedMgr ;
		
		synchronized(this)
		{
			if(prjEmbedMgr!=null)
				return prjEmbedMgr ;
			
			return prjEmbedMgr = new EmbeddingManager<>(this.prj);
		}
	}
	
}
