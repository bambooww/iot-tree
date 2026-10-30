package org.iottree.driver.zigbee.zspec;

import java.util.HashMap;

import org.iottree.driver.zigbee.zcl.ZCLCmd;
import org.json.JSONObject;

public class ZSCmd
{
	ZSCluster owner ;
	
	//int cmdId = -1 ;
	
	ZCLCmd cmd = null ;
	
	long updateDT = -1 ;
	
	public ZSCmd(ZSCluster owner,ZCLCmd cmd)
	{
		this.owner = owner ;
		this.cmd = cmd ;
		//this.cmdId = cmd.getID() ;
	}
	
	public ZSCluster getOwner()
	{
		return this.owner ;
	}
	
	public int getCmdId()
	{
		return this.cmd.getID();
	}
	
	public ZCLCmd getCmdDef()
	{
		return this.cmd ;
	}
	
	public String getTagAddr()
	{
		return this.owner.getTagAddrPrefix()+";cmd:"+this.getCmdId() ;
	}
	
	public JSONObject toJO()
	{
		JSONObject r = new JSONObject() ;
		r.put("up_dt",this.updateDT) ;
		r.put("cmd_id", this.getCmdId()) ;
		ZCLCmd cmddef = getCmdDef() ;
		if(cmddef!=null)
			r.put("cmd_def", cmddef.toJO()) ;
		return r ;
	}
	
	public static ZSCmd fromJO(ZSCluster c,JSONObject jo)
	{
		int cmd_id = jo.optInt("cmd_id",-1) ;
		if(cmd_id<0)
			return null ;
		ZCLCmd cmd = c.getClusterDef().getCmdById(cmd_id) ;
		if(cmd==null)
			return null ;
		ZSCmd r = new ZSCmd(c, cmd) ;
		r.updateDT = jo.optLong("up_dt",-1) ;
		return r ;
	}
	
	void RT_onCmd(HashMap<String,Object> pm)
	{
		this.updateDT = System.currentTimeMillis() ;
	}
}
