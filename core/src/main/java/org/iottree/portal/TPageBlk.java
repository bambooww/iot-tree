package org.iottree.portal;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

import org.iottree.core.util.Convert;

/**
 * 模板内的标记块
 * @author Jason Zhu
 */
public class TPageBlk
{
	TPage owner ;
	
	String tp = null ;
	HashMap<String,String> props = new HashMap<String,String>() ;
	
	public TPageBlk(TPage owner,String n)
	{
		this.owner = owner ;
		int p = n.indexOf(' ');
		if(p>0)
		{
			tp = n.substring(0,p);
			String tmps = n.substring(p+1).trim();
			
			p = 0 ;
			while(tmps!=null)
			{
				int i = tmps.indexOf('=',p);
				if(i<0)
				{
					props.put(tmps,"");
					return ;
				}
				
				String pn = tmps.substring(p,i).trim();
				tmps = tmps.substring(i+1).trim() ;
				char c = tmps.charAt(0);
				if(c!='\''&&c!='\"')
					throw new RuntimeException("page block must like [#xx xx=\"xx\"#]");
				
				int j = tmps.indexOf(c,1);
				if(j<=0)
					throw new RuntimeException("page block must like [#xx xx=\"xx\"#]");
				
				String pv = tmps.substring(1,j);
				props.put(pn, pv);
				tmps = tmps.substring(j+1).trim() ;
				if(tmps.equals(""))
					break;
			}
		}
		else
		{
			tp = n ;
			//props = new HashMap<String,String>() ;
		}
	}
	
	TPageBlk(String tp,HashMap<String,String> p)
	{
		this.tp = tp ;
		props = p;
		if(props==null)
			props = new HashMap<String,String>() ;
	}
	
	public String getBlockTP()
	{
		return this.tp ;
	}
	
	public HashMap<String,String> getProps()
	{
		return props ;
	}
	
	public String getPropVal(String pn)
	{
		return props.get(pn) ;
	}
	
	public String getBlkName()
	{
		String r = props.get("n") ;
		if(r==null)
			return "" ;
		return r;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(TPage.BLK_S).append(this.tp);
		for(Map.Entry<String, String> kv:props.entrySet())
		{
			sb.append(" ").append(kv.getKey()).append("=\"").append(kv.getValue()).append("\"");
		}
		sb.append(TPage.BLK_E);
		return sb.toString();
	}
	
	public void renderOutDef(Writer out) throws IOException
	{
		String deftxt = this.getPropVal("def") ;
		if(deftxt==null)
			return ;
		out.write(deftxt);
	}
	
	public void renderOutSetup(Writer out,PageBlk pblk) throws IOException
	{
		String tt = "????" ;
		if(pblk!=null)
			tt = pblk.getSetupOutTitle() ;
		if(Convert.isNullOrTrimEmpty(tt))
			tt = "????";
		tt += "["+this.getBlkName()+"-"+this.getBlockTP()+"]" ;
		String ss = "<span style='border:1px solid red;background-color:yellow;color:#333;'>";
		ss += "["+this.getBlkName()+"-"+this.getBlockTP()+"]" ;
		ss += "<button  onclick=\"__on_blk_set__('set','"+this.getBlkName()+"','','')\">设置</button>";
		if(pblk!=null)
			ss += "<button style='background-color:#6fcf21' onclick=\"__on_blk_set__('edit','"+this.getBlkName()+"','"+pblk.getTP()+"','"+pblk.getTPT()+"')\">修改</button>";
		ss += "</span>";
//		out.write(pblk.getSetupOutt()) ;
//		
//		String blkn = this.getBlockName() ;
//		switch(blkn)
//		{
//		case "txt":
//		
//		}
//		String deftxt = this.getPropVal("def") ;
//		if(deftxt==null)
//			return ;
//		out.write(deftxt);
		out.write(ss) ;
	}
	
	public void RT_renderOut(Writer out,Page page,PageBlk pblk) throws IOException
	{
		String blk_tp = this.getBlockTP() ;
		if("title".equals(blk_tp))
		{
			out.write(page.getTitle()) ;
			return ;
		}
		
		if(pblk==null)
			return ;
		
		out.write(pblk.RT_getOutTxt());
		
	}
}
