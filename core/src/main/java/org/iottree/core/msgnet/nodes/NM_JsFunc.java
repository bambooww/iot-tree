package org.iottree.core.msgnet.nodes;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.ILang;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

public class NM_JsFunc extends MNNodeMid implements ILang
{
	int outNum = 1 ;
	
	String onInitJS = "" ;
	
	String onMsgJS = "" ;
	
	String onEndJS = "" ;
	
	@Override
	public String getColor()
	{
		return "#ffcea0";
	}
	
	@Override
	public String getIcon()
	{
		return "\\uf09a";
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
		return outNum;
	}

//	@Override
	public String getTP()
	{
		return "js_func";
	}

	@Override
	public String getTPTitle()
	{
		return g("js_func");
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
		jo.put("out_num", this.outNum) ;
		jo.putOpt("on_msg_js", this.onMsgJS) ;
		jo.putOpt("on_init_js", this.onInitJS) ;
		jo.putOpt("on_end_js", this.onEndJS) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo, long up_dt)
	{
		this.outNum = jo.optInt("out_num",1) ;
		if(this.outNum<=0)
			this.outNum = 1 ;
		this.onMsgJS = jo.optString("on_msg_js", "") ;
		this.onInitJS = jo.optString("on_init_js", "") ;
		this.onEndJS = jo.optString("on_end_js", "") ;
	}
	
	// --------------

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{
		return RTOut.createOutAll(msg) ;
	}
}
