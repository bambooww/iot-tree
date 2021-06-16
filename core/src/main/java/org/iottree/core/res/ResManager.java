package org.iottree.core.res;

import java.util.*;

import org.iottree.core.DevCat;
import org.iottree.core.DevDef;
import org.iottree.core.DevManager;
import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.comp.CompCat;
import org.iottree.core.comp.CompManager;
import org.iottree.core.util.Convert;

public class ResManager
{
	static ResManager instance = null;
	
	public static ResManager getInstance()
	{
		if(instance!=null)
			return instance;
		
		synchronized(ResManager.class)
		{
			if(instance!=null)
				return instance;
			
			instance = new ResManager() ;
			return instance ;
		}
	}
	
	private ResManager()
	{}
	
//	/**
//	 * 
//	 * @param rc
//	 */
//	void setResCxt(ResDir rc)
//	{
//		cxtid2cxt.put(rc.getCxtId(), rc) ;
//		
//	}
//	
//	public List<ResDir> listResCxts()
//	{
//		ArrayList<ResDir> rcs = new ArrayList<>() ;
//		rcs.addAll(cxtid2cxt.values()) ;
//		Collections.sort(rcs);
//		return rcs ;
//	}
	
	//private static ResDir getByUid(ResDir )
	
	public ResDir getResDir(String uidid)
	{
		IResNode rc = null ;
		List<String> subids = null;//
		String id =null;
		if(uidid.startsWith(IResCxt.PRE_PRJ))
		{
			id = uidid.substring(IResCxt.PRE_PRJ.length()+1) ;
			rc = UAManager.getInstance();
		}
		else if(uidid.startsWith(IResCxt.PRE_DEVDEF))
		{
			id = uidid.substring(IResCxt.PRE_DEVDEF.length()+1) ;
			rc = DevManager.getInstance();
		}
		else if(uidid.startsWith(IResCxt.PRE_COMP))
		{
			id = uidid.substring(IResCxt.PRE_COMP.length()+1) ;
			rc = CompManager.getInstance() ;
		}
		else
		{
			return null ;
		}
		subids = Convert.splitStrWith(id, "-") ;
		for(int i = 0 ; i < subids.size(); i ++)
		{
			rc = rc.getResNodeSub(subids.get(i)) ;
			if(rc==null)
				break ;
		}
		if(rc==null)
			return null ;
		return rc.getResDir() ;
	}
//	/**
//	 * rep_id-name
//	 * global-cat/name.png
//	 * comp_id-xxx.jpg
//	 * @param resid
//	 * @return
//	 */
//	public ResItem getResItemById(String resid)
//	{
//		int k = resid.indexOf('-');
//		if(k<=0)
//			return null ;
//		String cxtid = resid.substring(0,k) ;
//		ResDir  rc =getResDir(cxtid) ;
//		if(rc==null)
//			return null ;
//		String resname = resid.substring(k+1) ;
//		return rc.getResItem(resname) ;
//
//		
//	}
	
}
