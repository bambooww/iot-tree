package org.iottree.core.msgnet.nodes;

import java.util.Date;
import java.util.List;

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
import org.json.JSONObject;

public class NE_Debug extends MNNodeEnd implements ILang
{
	boolean bOutWin = true;
	boolean bOutConsole = false;
	
	int maxBufferedMsnNum = 100 ;
	
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
		//super.RT_renderDiv(divblks);
		
		StringBuilder divsb = new StringBuilder() ;

		divsb.append("<div class=\"rt_blk\" style='position:relative;height:98%;'><div style='background-color:#aaaaaa;white-space: nowrap;'>Debug Messages  <button onclick=\"clear_debug_list('"+this.getId()+"')\">clear</button> &nbsp; <button onclick=\"start_stop_debug_list(this,'"+this.getId()+"')\">stop</button></div>") ;
		divsb.append("<div class=\"rt_debug_list\" id=\"debug_n_"+this.getId()+"\" max_buf_num='"+this.maxBufferedMsnNum+"'></div>") ;
		divsb.append("</div>") ;
		
		divblks.add(new DivBlk("node_debug",divsb.toString())) ;
	}
}
