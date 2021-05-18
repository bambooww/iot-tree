package org.iottree.core.util.dict;

import java.io.PrintWriter;

import javax.servlet.http.*;
import javax.servlet.jsp.JspWriter;

public class DictHelper
{
	public static boolean renderLocUIClientJSON(
			HttpServletRequest req,HttpServletResponse resp,String dd_class_name,int lvl)
	throws Exception
	{
		//JspWriter jw = req.
		DictManager dm = DictManager.getInstance() ;
		DataClass dc = dm.getModuleDataClass(req, dd_class_name);
		if(dc==null)
			return false;
		
		return renderUIClientJSON(req,resp,dc,lvl);
	}
	
	public static boolean renderSysUIClientJSON(
			HttpServletRequest req,HttpServletResponse resp,String dd_class_name,int lvl)
	throws Exception
	{
		//JspWriter jw = req.
		DictManager dm = DictManager.getInstance() ;
		DataClass dc = dm.getDataClass(dd_class_name) ;
		//DataClass dc = dm.getModuleDataClass(req, dd_class_name);
		if(dc==null)
			return false;
		
		return renderUIClientJSON(req,resp,dc,lvl);
	}
	
	public static boolean renderUIClientJSON(
			HttpServletRequest req,HttpServletResponse resp,
			DataClass dc,int lvl)
		throws Exception
	{
		DataNode[] rdns = dc.getValidRootNodes() ;
		if(rdns==null)
			return false;
		
		PrintWriter pw = resp.getWriter() ;
		pw.print("{lvl:"+lvl+",options:[") ;
		
		for(int i=0;i<rdns.length;i++)
		{
			DataNode dn = rdns[i] ;
			if(i>0)
				pw.print(",") ;
			renderDataNode(pw,dn,lvl) ;
		}
		pw.print("]}");
		return true ;
	}
	
	private static void renderDataNode(PrintWriter pw,DataNode dn,int lvl)
	{
		pw.print("{v:\'"+dn.getId()+"\',n:\'"+dn.getName()
				+"\',t:\'"+dn.getNameCN()
				+"\',en:\'"+dn.getNameEn()+"\'") ;
		if(lvl>1)
		{
			DataNode[] cdns = dn.getChildNodes() ;
			pw.print(",subs:[\r\n") ;
			if(cdns!=null)
			{
				for(int i=0;i<cdns.length;i++)
				{
					if(i>0)
						pw.print(",") ;
					renderDataNode(pw,cdns[i],0) ;
				}
				
			}
			
			pw.print("\r\n]") ;
		}
		pw.print("}"); ;
	}
}
