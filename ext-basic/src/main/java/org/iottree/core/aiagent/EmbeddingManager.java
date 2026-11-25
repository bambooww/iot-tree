package org.iottree.core.aiagent;

import java.io.File;
import java.util.HashMap;
import org.iottree.core.UAPrj;
import org.iottree.core.UAVal;
import org.iottree.core.util.IDirBase;


public class EmbeddingManager<T extends IDirBase>
{
	private static HashMap<String, EmbeddingManager<UAPrj>> prj2instance = new HashMap<>();
	
	public static EmbeddingManager<UAPrj> getInstance(UAPrj prj)
	{
		EmbeddingManager<UAPrj> r = prj2instance.get(prj.getId());
		if (r != null)
			return r;

		synchronized (EmbeddingManager.class)
		{
			r = prj2instance.get(prj.getId());
			if (r != null)
				return r;

			r = new EmbeddingManager<UAPrj>(prj);
			prj2instance.put(prj.getId(), r);
			return r;
		}
	}
	
	public static final String COLLNAME_HA = "embedding_ha" ;
	
	public static final String COLLNAME_nor = "embedding_nor" ;
	
	private static HashMap<String,EmbeddingFields> coll_name2fields = new HashMap<>() ;
	static
	{
		EmbeddingFields ha_ef = new EmbeddingFields() ;
		ha_ef.setField(new EmbeddingField("",UAVal.ValTP.vt_str));
		coll_name2fields.put(COLLNAME_HA,ha_ef) ;
	}
	
	T owner;
	
	private HashMap<String,EmbeddingSaver> name2embed_saver = new HashMap<>() ;

	/**
	 * if owner is UAManager,it will be global embedding data - for product
	 * if owner is UAPrj it will be prj embedding data which is private for user
	 * 
	 * @param owner
	 */
	EmbeddingManager(T owner)
	{
		this.owner = owner;
	}
	
	public T getOwner()
	{
		return owner ;
	}
	
	public synchronized EmbeddingSaver getEmbeddingSaver(String coll_name)
	{
		EmbeddingSaver ret = name2embed_saver.get(coll_name) ;
		if(ret!=null)
			return ret ;
		
		synchronized(this)
		{
			ret = name2embed_saver.get(coll_name) ;
			if(ret!=null)
				return ret ;
			
			String path = new File(this.owner.getDirBaseFile(),coll_name+"/").getAbsolutePath() ;
			EmbeddingFields efs = coll_name2fields.get(coll_name) ;
			if(efs==null)
				throw new IllegalArgumentException("unregistered collection name="+coll_name) ;
			
			ret = new EmbeddingSaverZvec(coll_name,  path, efs) ;
			name2embed_saver.put(coll_name,ret) ;
			return ret ;
		}
	}
}
