package org.iottree.core.msgnet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.msgnet.MNBase.DivBlk;

public class RTDebug
{
	HashMap<String,RTDebugPrompt> tp2ppt = new HashMap<>() ;
	
	MNBase belongTo ;
	String lvl ;
	String bgcolor ;
	
	RTDebug(MNBase item,String lvl,String bgcolor)
	{
		this.belongTo = item ;
		this.lvl = lvl ;
		this.bgcolor = bgcolor ;
	}
	
	public List<RTDebugPrompt> listPrompts()
	{
		ArrayList<RTDebugPrompt> rets = new ArrayList<>() ;
		rets.addAll(tp2ppt.values()) ;
		Collections.sort(rets);
		return rets ;
	}
	
	public RTDebugPrompt getPrompt(String tp)
	{
		return this.tp2ppt.get(tp) ;
	}
	
	public boolean hasPrompts()
	{
		return tp2ppt.size()>0 ;
	}
	
	public final void fire(String tp,String msg)
	{
		fire(tp,msg,null,null) ;
	}
	
	public final void fire(String tp,String msg,String detail)
	{
		fire(tp,msg,detail,null) ;
	}
	
	public final void fire(String tp,String msg,Throwable ee)
	{
		fire(tp,msg,null,ee) ;
	}
	
	/**
	 * called by overrider,to fire same err inf
	 * if msg and ee are null,it will clear inf
	 * @param msg
	 * @param ee
	 */
	public final void fire(String tp,String msg,String content,Throwable ee)
	{
		this.tp2ppt.put(tp, new RTDebugPrompt(tp,msg, content, ee)) ;
	}
	
	public final void clear(String tp)
	{
		this.tp2ppt.remove(tp) ;
	}
	
	public void renderDiv(List<DivBlk> divblks)
	{
		if(this.tp2ppt.size()<=0)
			return ;
		
		for(RTDebugPrompt ppt:listPrompts())
		{
			StringBuilder divsb = new StringBuilder() ;
			divsb.append("<div  class=\"rt_blk\" style='background-color:"+this.bgcolor+"'>["+lvl+"] "+ppt.getDTGapToNow()+" "+ppt.getMsg()) ;
			if(ppt.hasDetail())
				divsb.append("<button onclick=\"debug_prompt_detail(\'"+this.belongTo.getId()+"\','"+lvl+"','"+ppt.tp+"')\">Detail</button>") ;
			divsb.append("</div>") ;
			divblks.add(new DivBlk("debug_"+lvl+"_"+ppt.tp,divsb.toString())) ;
		}
	}
}
