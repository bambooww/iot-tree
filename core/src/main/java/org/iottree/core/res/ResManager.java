package org.iottree.core.res;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.iottree.core.DevCat;
import org.iottree.core.DevDef;
import org.iottree.core.DevManager;
import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.comp.CompCat;
import org.iottree.core.comp.CompItem;
import org.iottree.core.comp.CompManager;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;

public class ResManager
{
	static ILogger log = LoggerManager.getLogger(ResManager.class) ;
	
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
	

//	public IResNode getResNode(String resnodeid)
//	{
//		IResNode rc = null ;
//		List<String> subids = null;//
//		String id =null;
//		if(resnodeid.startsWith(IResCxt.PRE_PRJ))
//		{
//			id = resnodeid.substring(IResCxt.PRE_PRJ.length()+1) ;
//			rc = UAManager.getInstance();
//		}
//		else if(resnodeid.startsWith(IResCxt.PRE_DEVDEF))
//		{
//			id = resnodeid.substring(IResCxt.PRE_DEVDEF.length()+1) ;
//			rc = DevManager.getInstance();
//			//return null ;
//		}
//		else if(resnodeid.startsWith(IResCxt.PRE_COMP))
//		{
//			id = resnodeid.substring(IResCxt.PRE_COMP.length()+1) ;
//			rc = CompManager.getInstance() ;
//		}
//		else
//		{
//			return null ;
//		}
//		subids = Convert.splitStrWith(id, "-") ;
//		for(int i = 0 ; i < subids.size(); i ++)
//		{
//			rc = rc.getResNodeSub(subids.get(i)) ;
//			if(rc==null)
//				break ;
//		}
//		return rc;
//	}
//	
//	public ResDir getResDir(String resnodeid)
//	{
//		IResNode rc = getResNode(resnodeid) ;
//		if(rc==null)
//			return null ;
//		return rc.getResDir() ;
//	}
//	
//	
//	public ResItem findResItem(String res_node_id,String resname)
//	{
//		IResNode rc = getResNode(res_node_id) ;
//		if(rc==null)
//			return null ;
//		do
//		{
//			ResDir rd = rc.getResDir() ;
//			if(rd!=null)
//			{
//				ResItem ri = rd.getResItem(resname) ;
//				if(ri!=null)
//					return ri ;
//			}
//			rc = rc.getResNodeParent() ;
//		}while(rc!=null) ;
//		return null ;
//	}
	
	
	private HashMap<IResCxt,ResLib> rc2lib = new HashMap<>() ;
	
	private HashMap<String,ResReferer> key2ref = new HashMap<>() ;
	/**
	 * get local reslib by cxt
	 * @param rc
	 * @return
	 */
	public ResLib getResLib(IResCxt rc)
	{
		ResLib r = rc2lib.get(rc) ;
		if(r!=null)
			return r ;
		
//		String n = rc.getResCxtName();
		//if(Convert.isNullOrEmpty(n))
		//	throw new RuntimeException("res cxt is not suppoort local") ;
		
		synchronized(this)
		{
			r = rc2lib.get(rc) ;
			if(r!=null)
				return r ;
			
			File nordir = rc.getResRootDir();
			File rdir = new File(nordir,"_res/") ;
			r = new ResLib(rc.getResPrefix(),nordir,rdir) ;
			rc2lib.put(rc, r) ;
			return r ;
		}
	}
	
	public ResReferer getResRef(IResCxt rc,String name)
	{
		List<String> refns = rc.getResRefferNames() ;
		if(refns==null||refns.size()<=0)
			return null ;
		if(!refns.contains(name))
			return null ;
		
		String cxtn = rc.getResPrefix();//.getResCxtName() ;
		String k = cxtn+"-"+name ;
		
		ResReferer rr = key2ref.get(k) ;
		if(rr!=null)
			return rr ;
		
		synchronized(this)
		{
			rr = key2ref.get(k) ;
			if(rr!=null)
				return rr ;
			
			File dirf = new File(rc.getResRootDir(),"ref_"+name+"/") ;
			rr = new ResReferer(name,dirf);
			
			key2ref.put(k, rr) ;
			return rr ;
		}
	}
	
	public IResCxt getResCxtByLibId(String reslibid)
	{
		int k = reslibid.indexOf('_') ;
		if(k<=0)
			return null ;
		String p = reslibid.substring(0,k) ;
		String cxtid = reslibid.substring(k+1) ;
		return getResCxt(p,cxtid);
	}
	
//	public ResDir getRefResDir(String ref_lib_id,String res_lib_id,String res_id)
//	{
//		
//	}
	
	public IResNode getResNode(String res_lib_id,String res_id)
	{
		IResCxt rc = getResCxtByLibId(res_lib_id);
		if(rc==null)
		{
			return null;
		}
		IResNode rnode = null ;
		if(Convert.isNotNullEmpty(res_id))
		{
			rnode = rc.getResNodeById(res_id) ;
			if(rnode==null)
			{
				return null;
			}
		}
		else
		{
			res_id = "" ;
			rnode = rc ;
		}
		
		return rnode;//.getResDir() ;
	}
	
	public ResDir getResDir(String res_lib_id,String res_id)
	{
		IResNode rn = getResNode(res_lib_id,res_id);
		if(rn==null)
			return null ;
		return rn.getResDir() ;
	}
	
	public IResCxt getResCxt(String prefix,String cxtid)
	{
		switch(prefix)
		{
		case IResCxt.PRE_COMP:
			return CompManager.getInstance().getCompLibById(cxtid) ;
		case IResCxt.PRE_DEVDEF:
			return DevManager.getInstance().getDevLibById(cxtid) ;
		case IResCxt.PRE_PRJ:
			return UAManager.getInstance().getPrjById(cxtid) ;
		default:
			return null ;
		}
	}
	
	public ResLib getResLibByLibId(String reslibid)
	{
		IResCxt rc = this.getResCxtByLibId(reslibid) ;
		if(rc==null)
			return null ;
		return this.getResLib(rc) ;
	}
	
	public ResItem getResItemNoRef(String reslibid,String res_id,String name)
	{
		return getResItemWithRef(null,reslibid,res_id,name) ;
	}
	
	
	public ResItem getResItem(String prefix,String cxtid,String name)
	{
		IResCxt rc = this.getResCxt(prefix,cxtid) ;
		if(rc==null)
			return null ;
		ResLib lib = this.getResLib(rc) ;
		if(lib==null)
			return null ;
		
		return lib.getResItem(name) ;
	}
	
	public ResItem getResItemWithRef(String ref_lib_id,String reslibid,String resid,String name) //throws IOException
	{
		File reff = null ; 
		if(Convert.isNotNullEmpty(ref_lib_id)&&!ref_lib_id.equals(reslibid))
		{
			IResCxt refcxt=  this.getResCxtByLibId(ref_lib_id) ;
			reff = refcxt.getRefFile(reslibid,resid, name, false);
			if(reff!=null && reff.exists())
			{
				return new ResItem(reff) ;
			}
		}
		
		ResDir rdir = this.getResDir(reslibid, resid);
		// ResLib lib = getResLibByLibId(reslibid);
		if(rdir==null)
			return null ;
		
		ResItem r = rdir.getResItem(name) ;
		if(r==null)
			return null ;
		
		if(reff!=null)
		{
			try
			{
				FileUtils.copyFile(r.getResFile(), reff);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return r ;
	}
	
	
	private HashMap<String,CompItem> refKey2ci = new HashMap<>() ; 
	/**
	 * get or copy
	 * @param ref_lib_id
	 * @param res_lib_id
	 * @param compid
	 * @return
	 * @throws IOException 
	 */
	public CompItem getCompItem(String ref_lib_id,String res_lib_id,String compid) throws Exception
	{
		if(Convert.isNullOrEmpty(ref_lib_id) ||ref_lib_id.equals(res_lib_id))
		{
			return CompManager.getInstance().findCompItemById(res_lib_id,compid) ;
		}
		
		String k = ref_lib_id+"-"+res_lib_id+"-"+compid ;
		CompItem ci = null;// refKey2ci.get(k) ;
		if(ci!=null)
			return ci;
		
		if(log.isTraceEnabled())
		{
			log.trace("getCompItem in ref="+ref_lib_id+" res_lib_id="+res_lib_id+" res/compid="+compid);
		}
		//File ref_f = null ;
		
		IResCxt ref_rc = ResManager.getInstance().getResCxtByLibId(ref_lib_id) ;
		if(ref_rc==null)
		{
			return null ;
		}
		
			//String txt = ref_rc.readRefFileTxt(res_lib_id,compid+".data.txt",true,true) ;
		File refdir = new File(ref_rc.getRefRootDir(),res_lib_id+"/") ;
		File ref_compdir=  new File(refdir,compid+"/") ;
		if(ref_compdir.exists())
		{
			ci = CompItem.loadFromDir(res_lib_id,compid, ref_compdir) ;
			if(ci!=null)
			{
				refKey2ci.put(k,ci) ;
				
				if(log.isTraceEnabled())
				{
					log.trace("getCompItem in ref="+ref_lib_id+" res_lib_id="+res_lib_id+" res/compid="+compid +" get ok from ref lib");
				}
				
				return ci ;
			}
		}
		
		ci = CompManager.getInstance().findCompItemById(res_lib_id,compid) ;
		if(ci==null)
			return null;
		
		FileUtils.copyDirectory(ci.getCompItemDir(), ref_compdir);
		ci = CompItem.loadFromDir(res_lib_id,compid, ref_compdir) ;
		if(ci!=null)
		{
			if(log.isTraceEnabled())
			{
				log.trace("getCompItem in ref="+ref_lib_id+" res_lib_id="+res_lib_id+" res/compid="+compid +" get ok from sor lib");
			}
			
			refKey2ci.put(k,ci) ;
			
		}
		
		return ci ;
	}
	
	
	private HashMap<String,DevDef> refKey2dd = new HashMap<>() ;
	
	public DevDef getDevDef(String ref_lib_id,String res_lib_id,String res_id) throws Exception
	{
		return getDevDef(ref_lib_id,res_lib_id,res_id,false) ;
	}
	
	public DevDef getDevDef(String ref_lib_id,String res_lib_id,String res_id,boolean chk_eq) throws Exception
	{
		if(Convert.isNullOrEmpty(ref_lib_id) ||ref_lib_id.equals(res_lib_id))
		{
			return DevManager.getInstance().getDevDefById(res_lib_id, res_id) ;
		}
		
		String k = ref_lib_id+"-"+res_lib_id+"-"+res_id ;
		DevDef dd = null;
		if(!chk_eq)
		{
			//dd = refKey2dd.get(k) ;
			if(dd!=null)
				return dd;
		}
		
		if(log.isTraceEnabled())
		{
			log.trace("getDevDef in ref="+ref_lib_id+" res_lib_id="+res_lib_id+" res/compid="+res_id);
		}
		//File ref_f = null ;
		
		IResCxt ref_rc = ResManager.getInstance().getResCxtByLibId(ref_lib_id) ;
		if(ref_rc==null)
		{
			return null ;
		}
		
		String devlibid = res_lib_id.substring(2) ;
			//String txt = ref_rc.readRefFileTxt(res_lib_id,compid+".data.txt",true,true) ;
		File refdir = new File(ref_rc.getRefRootDir(),res_lib_id+"/") ;
		File ref_dddir=  new File(refdir,res_id+"/") ;
		
		boolean b_need_copy = false;
		if(chk_eq)
		{
			DevDef sordd = DevManager.getInstance().getDevDefById(devlibid, res_id) ;
			if(sordd==null)
				return null ;
			b_need_copy = !compareDirEq(sordd.getDevDefDir(),ref_dddir);
		}
		
		
		if(ref_dddir.exists() && !b_need_copy)
		{
			dd = DevDef.loadFromDir(ref_dddir,null);//res_lib_id,compid, ref_compdir) ;
			if(dd!=null)
			{
				refKey2dd.put(k,dd) ;
				
				if(log.isTraceEnabled())
				{
					log.trace("getDevDef in ref="+ref_lib_id+" res_lib_id="+res_lib_id+" res="+res_id +" get ok from ref lib");
				}
				return dd ;
			}
		}
		
		
		dd = DevManager.getInstance().getDevDefById(devlibid, res_id) ;
		if(dd==null)
			return null;
		
		//if(b_need_copy)
		if(ref_dddir.exists())
			FileUtils.cleanDirectory(ref_dddir);
		FileUtils.copyDirectory(dd.getDevDefDir(), ref_dddir);
		dd = DevDef.loadFromDir(ref_dddir,null);
		if(dd!=null)
		{
			if(log.isTraceEnabled())
			{
				log.trace("getDevDef in ref="+ref_lib_id+" res_lib_id="+res_lib_id+" res="+res_id +" get ok from sor lib");
			}
			
			refKey2dd.put(k,dd) ;
			
		}
		
		return dd ;
	}
	
	
//	public String getCompTxt(String ref_lib_id,String res_lib_id,String compid) throws IOException
//	{
//		File ref_f = null ;
//		if(Convert.isNotNullEmpty(ref_lib_id))
//		{
//			IResCxt ref_rc = ResManager.getInstance().getResCxtByLibId(ref_lib_id) ;
//			if(ref_rc!=null)
//			{
//				//String txt = ref_rc.readRefFileTxt(res_lib_id,compid+".data.txt",true,true) ;
//				ref_f= ref_rc.getRefFile(res_lib_id,compid+".data.txt",true);
//				if(ref_f.exists())
//					return Convert.readFileTxt(ref_f, "UTF-8") ;
//			}
//		}
//		
//		
//		CompItem ci = CompManager.getInstance().findCompItemById(res_lib_id,compid) ;
//		//ci = CompManager.getInstance().findCompItemById(compid);
//		if(ci!=null)
//		{
//			String txt = ci.getOrLoadCompData() ;
//			if(txt!=null && ref_f!=null)
//			{
//				Convert.writeFileTxt(ref_f,txt, "UTF-8");
//			}
//			return txt ;
//		}
//		return null ;
//	}
	
	public static boolean compareDirEq(File sor_dir,File tar_dir)
	{
		if(!sor_dir.exists())
			throw new IllegalArgumentException("sor dir not existed") ;
		
		if(!tar_dir.exists())
			return false;
		
		File[] sor_fs = sor_dir.listFiles();
		File[] tar_fs = tar_dir.listFiles() ;
		if(sor_fs.length!=tar_fs.length)
			return false;
		
		for(File sorf:sor_fs)
		{
			String fn = sorf.getName() ;
			File tarf = new File(tar_dir,fn) ;
			if(!tarf.exists())
				return false;
			
			if(sorf.isDirectory())
			{
				if(!tarf.isDirectory())
					return false;
				if(!compareDirEq(sorf,tarf))
					return false;
				
				continue ;
			}
			
			if(sorf.length()!=tarf.length())
				return false;
			if(sorf.lastModified()!=tarf.lastModified())
				return false;
		}
		
		return true;
	}
}
