package org.iottree.core.msgnet.modules;

import java.time.LocalTime;

import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.util.RepeatTP;
import org.iottree.core.util.ILang;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

/**
 * 根据标签值动态变化满足的条件，触发起始节点
 * 
 * @author jason.zhu
 */
public class TagTrigger_NS extends MNNodeStart implements ILang
{
	
	@Override
	public String getColor()
	{
		return "#9fbccf";
	}
	
	@Override
	public String getIcon()
	{
		return "\\uf04b";
	}
	
	@Override
	public JSONTemp getInJT()
	{
		return null;
	}

	@Override
	public JSONTemp getOutJT()
	{
		return null;
	}

	@Override
	public int getOutNum()
	{
		return 1;
	}


	@Override
	public String getTP()
	{
		return "tag_trigger";
	}

	@Override
	public String getTPTitle()
	{
		return g("tag_trigger");
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return true ;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		//jo.put("b_delay", this.bDelayExec) ;
//		jo.put("delay_ms", this.delayExecMS) ;
//		jo.put("repeat_tp", this.repectTp.getInt()) ;
//		if(betweenS!=null)
//			jo.put("between_s", betweenS.toSecondOfDay());
//		if(betweenE!=null)
//			jo.put("between_e", betweenE.toSecondOfDay());
//		jo.put("on_week", onWeekMark) ;
		return jo ;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		//this.bDelayExec = jo.optBoolean("b_delay",false) ;
//			this.delayExecMS = jo.optLong("delay_ms",-1) ;
//			this.repectTp = RepeatTP.valOfInt(jo.optInt("repeat_tp",0)) ;
//			int ss = jo.optInt("between_s",-1) ;
//			if(ss>0)
//				this.betweenS = LocalTime.ofSecondOfDay(ss) ;
//			ss = jo.optInt("between_e",-1) ;
//			if(ss>0)
//				this.betweenE = LocalTime.ofSecondOfDay(ss) ;
//			this.onWeekMark = jo.optInt("on_week",0xFF) ;
	}

	//  --------------- 
	

}
