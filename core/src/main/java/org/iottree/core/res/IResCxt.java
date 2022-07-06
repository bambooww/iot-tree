package org.iottree.core.res;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.iottree.core.util.Convert;

/**
 * for XxxManager impl to support Res
 * @author jason.zhu
 *
 */
public interface IResCxt extends IResNode
{

	public static final String PRE_PRJ="p" ;
	
	public static final String PRE_DEVDEF="d" ;
	
	public static final String PRE_COMP="c" ;
//	
	
	public String getResCxtId() ;
//	/**
//	 * 
//	 * @return null is not support
//	 */
//	public String getResCxtName() ;
	
	public String getResPrefix() ;
	
	public IResNode getResNodeById(String res_id) ;
	
	default public String getResLibId()
	{
		return getResPrefix()+"_"+getResCxtId();
	}
//	
//	//public ResDir getResCxt(String uid);
//	
//	public boolean isResReadOnly() ;
	//public List<ResCxt> getResCxts() ;
	
	public File getResRootDir() ;
	
	/**
	 * save ref objects root dir
	 * null = is not support
	 * @return
	 */
	public File getRefRootDir() ;
	
	
	default public File getRefLibDir(String refn,String libid)
	{
		List<String> rns =  getResRefferNames() ;
		if(rns==null||!rns.contains(refn))
			return null ;
		return new File(getRefRootDir(),refn+"_"+libid+"/") ;
	}
	
	default public File getRefNorFile(String refn,String libid,String resid,String name)
	{
		File libd = getRefLibDir(refn,libid);
		if(libd==null)
			return null ;
		if(Convert.isNullOrEmpty(resid))
			return new File(libd,name) ;
		else
			return new File(libd,resid+"/"+name) ;
	}
	
	default public File getRefResFile(String refn,String libid,String resid,String name)
	{
		File libd = getRefLibDir(refn,libid);
		if(libd==null)
			return null ;
		if(Convert.isNullOrEmpty(resid))
			return new File(libd,"_res/"+name) ;
		else
			return new File(libd,resid+"/_res/"+name) ;
	}
	
//	default public String readRefFileTxt(String sor_reslibid,String name,boolean b_nor)  throws IOException
//	{
//		File f = getRefFile(sor_reslibid,name,b_nor);
//		if(f==null)
//			return null ;
//		return Convert.readFileTxt(f, "UTF-8") ;
//	}
	
	default public File getRefFile(String sor_reslibid,String sor_resid,String name,boolean b_nor) //throws IOException
	{
		int k = sor_reslibid.indexOf('_') ;
		if(k<=0)
			return null ;
		return getRefFile(sor_reslibid.substring(0,k),sor_reslibid.substring(k+1),sor_resid,name,b_nor) ;
	}
	
	default public File getRefFile(String sor_prefix,String sor_libid,String sor_resid,String name,boolean b_nor)// throws IOException
	{
		File rld = getRefLibDir(sor_prefix,sor_libid);
		if(rld==null)
			return null ;
		File f = null;
		if(b_nor)
			f = getRefNorFile(sor_prefix,sor_libid,sor_resid,name) ;
		else
			f = getRefResFile(sor_prefix,sor_libid,sor_resid,name) ;
		if(f.exists())
			return f ;
		
//		if(bcopy)
//		{
//			ResItem ri = ResManager.getInstance().getResItem(sor_prefix, sor_libid,name) ;
//			if(ri==null)
//				return null ;
//			File sorf= ri.getResFile() ;
//			if(!sorf.exists())
//				return null ;
//			if(!f.getParentFile().exists())
//				f.getParentFile().mkdirs() ;
//			FileUtils.copyFile(sorf, f);
//		}
		return f;
	}
	
	public List<String> getResRefferNames() ;
}
