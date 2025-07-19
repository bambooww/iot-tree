package org.iottree.core.msgnet.nodes;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.msgnet.MNNodeEnd;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.MNBase.DivBlk;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ILang;
import org.iottree.core.util.jt.JSONTemp;
import org.iottree.core.ws.WSMsgNetRoot;
import org.json.JSONArray;
import org.json.JSONObject;

public class NE_Debug extends MNNodeEnd implements ILang
{
	public static class AnaItem
	{
		public String subN ; // like payload or payload.aa or h1.vv
		
		public float min =0;
		
		public float max = 100 ;
		
		public String color ;
		
		public String title ;
		
		public String unit ;
		
		public boolean yaxis_right =false;
		
		public JSONObject toJO()
		{
			JSONObject ret = new JSONObject() ;
			ret.putOpt("subn", this.subN) ;
			ret.putOpt("min", this.min) ;
			ret.putOpt("max", this.max) ;
			ret.putOpt("color", this.color) ;
			ret.putOpt("title", this.title) ;
			ret.putOpt("unit", this.unit) ;
			ret.putOpt("yaxis_right", this.yaxis_right) ;
			return ret;
		}
		
		public static AnaItem fromJO(JSONObject jo)
		{
			AnaItem ret = new AnaItem() ;
			ret.subN = jo.optString("subn") ;
			if(Convert.isNullOrEmpty(ret.subN))
				return null ;
			ret.min = jo.optFloat("min",0);
			ret.max = jo.optFloat("max",100);
			ret.color = jo.optString("color");
			ret.title = jo.optString("title");
			ret.unit = jo.optString("unit");
			ret.yaxis_right = jo.optBoolean("yaxis_right",false) ;
			return ret ;
		}
	}
	
	boolean bOutWin = true;
	boolean bOutConsole = false;
	
	int maxBufferedMsnNum = 100 ;
	

	//support analysis or not
	boolean bSupAna = false;
	
	/**
	 * payload is simple value
	 */
	//AnaItem simplePldItem = null ;
	
	/**
	 * payload is json obj
	 */
	LinkedHashMap<String,AnaItem> subn2items = null ;
	
	public int getMaxBufferedMsgNum()
	{
		return maxBufferedMsnNum;
	}
	
	@Override
	public String getIcon()
	{
		return "\\uf188";
	}
	
	@Override
	public String getColor()
	{
		return "#7caa82";
	}
	
	@Override
	public String getTitleColor()
	{
		return "#eeeeee" ;
	}
	
	@Override
	public boolean supportOutOnOff()
	{
		return true;
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

//	@Override
	public String getTP()
	{
		return "debug";
	}

	@Override
	public String getTPTitle()
	{
		return g("debug");
	}
	
	public boolean isSupAna()
	{
		return this.bSupAna ;
	}
	
	public LinkedHashMap<String,AnaItem> getSubN2AnaItem()
	{
		return this.subn2items;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		//jo.put("b_delay", this.bDelayExec) ;
		jo.put("out_win", this.bOutWin) ;
		jo.put("out_console", this.bOutConsole) ;
		jo.put("buf_len", maxBufferedMsnNum) ;
		jo.put("sup_ana", this.bSupAna) ;
//		if(this.simplePldItem!=null)
//			jo.putOpt("simple_ana_item", this.simplePldItem.toJO()) ;
		if(subn2items!=null&&subn2items.size()>0)
		{
			JSONArray jarr = new JSONArray() ;
			for(AnaItem ai:subn2items.values())
			{
				jarr.put(ai.toJO()) ;
			}
			jo.put("ana_items",jarr) ;
		}
		return jo ;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.bOutWin= jo.optBoolean("out_win", true) ;
		this.bOutConsole = jo.optBoolean("out_console",false) ;
		this.maxBufferedMsnNum = jo.optInt("buf_len",100) ;
		if(this.maxBufferedMsnNum<=0)
			this.maxBufferedMsnNum = 100 ;
		this.bSupAna = jo.optBoolean("sup_ana",false) ;
//		JSONObject tmpjo = jo.optJSONObject("simple_ana_item") ;
//		if(tmpjo!=null)
//			this.simplePldItem = AnaItem.fromJO(tmpjo) ;
		JSONArray jarr = jo.optJSONArray("ana_items") ;
		if(jarr!=null)
		{
			this.subn2items = new LinkedHashMap<>() ;
			for(int i = 0 ; i < jarr.length() ; i ++)
			{
				JSONObject jo00 = jarr.getJSONObject(i) ;
				AnaItem ai = AnaItem.fromJO(jo00) ;
				if(ai==null)
					continue ;
				this.subn2items.put(ai.subN,ai) ;
			}
		}
	}

	
	@Override
	protected final RTOut RT_onMsgIn(MNConn in_conn,MNMsg msg)
	{
		if(!this.getOutOnOff())
			return null;//
		JSONObject jo = msg.toJO() ;
//		if(bOutConsole)
//		{
//			System.out.println(Convert.toFullYMDHMS(new Date()) +" node "+this.getTitle()) ;
//			System.out.println(jo) ;
//		}
//		if(bOutWin)
//		{
//			
//		}
		// push to client

		MNNode fromn = in_conn.getFromBelongToNode() ;
		int fromidx = in_conn.getOutIdx() ;
		JSONObject debugjo = new JSONObject() ;
		debugjo.put("from",fromn.getTitle());
		debugjo.put("from_idx",fromidx);
		debugjo.put("nodeid",this.getId());
		debugjo.put("dt",System.currentTimeMillis());
		debugjo.put("msg",jo) ;
		WSMsgNetRoot.pushToClient(debugjo.toString());
		return null;//RTOut.NONE;
	}
	
	@Override
	protected void RT_renderDiv(List<DivBlk> divblks)
	{
		super.RT_renderDiv(divblks);
		
		StringBuilder divsb = new StringBuilder() ;
		int h = this.bSupAna?30:90;
		divsb.append("<div class=\"rt_blk\" style='position:relative;height:"+h+"%;'><div style='background-color:#aaaaaa;white-space: nowrap;'>Debug Messages  <button onclick=\"clear_debug_list('"+this.getId()+"')\">clear</button> &nbsp; <button onclick=\"start_stop_debug_list(this,'"+this.getId()+"')\">stop</button></div>") ;
		divsb.append("<div class=\"rt_debug_list\" id=\"debug_n_"+this.getId()+"\" max_buf_num='"+this.maxBufferedMsnNum+"'></div>") ;
		divsb.append("</div>") ;
		
		divblks.add(new DivBlk("node_debug",divsb.toString())) ;
		
		
	}
	
	@Override
	public int RT_hasPanel()
	{
		if(this.bSupAna)
			return 60 ;
		else
			return 0 ;
	}
}
