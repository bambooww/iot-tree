package org.iottree.core.res;

import java.util.*;

public class ResCxtManager
{
	static ResCxtManager instance = null;
	
	public static ResCxtManager getInstance()
	{
		if(instance!=null)
			return instance;
		
		synchronized(ResCxtManager.class)
		{
			if(instance!=null)
				return instance;
			
			instance = new ResCxtManager() ;
			return instance ;
		}
	}
	
	HashMap<String,ResCxt> cxtid2cxt = new HashMap<>() ;
	
	HashMap<String,IResCxtRelated> id2cxtr = new HashMap<>() ;
	
	private ResCxtManager()
	{}
	
	/**
	 * 
	 * @param rc
	 */
	void setResCxt(ResCxt rc)
	{
		cxtid2cxt.put(rc.getCxtId(), rc) ;
		
	}
	
	public void setResCxtRelated(IResCxtRelated rcr)
	{

			//IResCxtRelated rcr = (IResCxtRelated)rc ;
			id2cxtr.put(rcr.getEditorName()+"-"+rcr.getEditorId(), rcr);

	}
	
	public List<ResCxt> listResCxts()
	{
		ArrayList<ResCxt> rcs = new ArrayList<>() ;
		rcs.addAll(cxtid2cxt.values()) ;
		Collections.sort(rcs);
		return rcs ;
	}
	
	public ResCxt getResCxt(String cxtid)
	{
		return cxtid2cxt.get(cxtid) ;
	}
	/**
	 * rep_id-name
	 * global-cat/name.png
	 * comp_id-xxx.jpg
	 * @param resid
	 * @return
	 */
	public ResItem getResItemById(String resid)
	{
		int k = resid.indexOf('-');
		if(k<=0)
			return null ;
		String cxtid = resid.substring(0,k) ;
		ResCxt  rc =getResCxt(cxtid) ;
		if(rc==null)
			return null ;
		String resname = resid.substring(k+1) ;
		return rc.getResItem(resname) ;
//		k = cxtid.indexOf('_') ;
//		String prefix = cxtid ;
//		
//		if(k>0)
//		{
//			prefix = cxtid.substring(0,k) ;
//			
//		}
		
	}
	
	public IResCxtRelated getResCxtRelated(String editorname,String editorid)
	{
		return id2cxtr.get(editorname+"-"+editorid);
	}
}
