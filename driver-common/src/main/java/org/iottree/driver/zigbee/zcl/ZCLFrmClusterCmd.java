package org.iottree.driver.zigbee.zcl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.iottree.driver.zigbee.zcl.util.DataType;
import org.iottree.driver.zigbee.zcl.util.DataVal;
import org.json.JSONObject;

public class ZCLFrmClusterCmd extends ZCLFrame
{
	int clusterId ;
	
	ZCLCluster cluster ;
	
	ZCLCmd cmdDef ;
	
	int cmdId ;
	
	//HashMap<String,Object> pmName2Val ;
	
	private byte[] pm_pk_data = null ;
	
	public ZCLFrmClusterCmd(int cmd_id,boolean dir_s2c)
	{
		this.cmdId = cmd_id ;
		this.asFrameCtrlClusterCmd(dir_s2c); //dir_s2c=true
		
		//pm_pk_data = calParamBS(pm_name2val) ;
	}
	
	public ZCLFrmClusterCmd asClusterID(int cluster_id)
	{
		this.clusterId = cluster_id ;
		this.cluster = ZCLClusterManager.getInstance().getCluserByID(cluster_id) ;
		if(this.cluster==null)
			throw new IllegalArgumentException("no cluster found with id="+cluster_id) ;
		this.cmdDef = this.cluster.getCmdById(this.cmdId) ;
		if(this.cmdDef==null)
			throw new IllegalArgumentException("no command found with id="+cmdId) ;
		return this ;
	}
	
	public ZCLFrmClusterCmd asCmdParams(int cluster_id,HashMap<String,Object> pm_name2val)
	{
		if(cluster_id>=0)
			asClusterID(cluster_id) ;
		//this.pmName2Val = pm_name2val ;
		pm_pk_data = calParamBS(pm_name2val) ;
		return this ;
	}
	
	public ZCLFrmClusterCmd asCmdParams(int cluster_id,Object[] pm_vals)
	{
		if(cluster_id>=0)
			asClusterID(cluster_id) ;
		//this.pmName2Val = pm_name2val ;
		pm_pk_data = calParamBS(pm_vals) ;
		return this ;
	}
	
	private byte[] calParamBS(HashMap<String,Object> pm_name2val)
	{
		List<ZCLParam> pms = this.cmdDef.getParemeters() ;
		if(pms==null||pms.size()<=0)
			return null ;
		
		ArrayList<DataVal> pm_dvs = new ArrayList<>() ;
		int len = 0 ;
		for(ZCLParam pm:pms)
		{
			DataType dtp = pm.getDataType() ;
			Object objv = pm_name2val.get(pm.getName()) ;
			//if(objv==null)
			//	throw new RuntimeException("no pm value found with name="+pm.getName()) ;
			DataVal dv = new DataVal(dtp,objv) ;
			int octlen = dv.getOctetLen() ;
			len += octlen ;
		}
		
		byte[] r = new byte[len] ;
		int sidx = 0 ;
		for(DataVal dv:pm_dvs)
		{
			int rlen = DataType.packObjValTo(dv, r, sidx) ;
			sidx += rlen ;
		}
		return r ;
	}
	
	private byte[] calParamBS(Object[] pm_vals)
	{
		List<ZCLParam> pms = this.cmdDef.getParemeters() ;
		if(pms==null||pms.size()<=0)
			return null ;
		int n = pms.size() ;
		if(n!=pm_vals.length)
			throw new IllegalArgumentException("input cmd pm num is not fit to def") ;
		
		ArrayList<DataVal> pm_dvs = new ArrayList<>() ;
		int len = 0 ;
		for(int i = 0 ; i < n ; i ++)
		{
			ZCLParam pm = pms.get(i) ;
			DataType dtp = pm.getDataType() ;
			Object objv = pm_vals[i] ;
			//if(objv==null)
			//	throw new RuntimeException("no pm value found with name="+pm.getName()) ;
			DataVal dv = new DataVal(dtp,objv) ;
			int octlen = dv.getOctetLen() ;
			len += octlen ;
			pm_dvs.add(dv) ;
		}
		
		byte[] r = new byte[len] ;
		int sidx = 0 ;
		for(DataVal dv:pm_dvs)
		{
			int rlen = DataType.packObjValTo(dv, r, sidx) ;
			sidx += rlen ;
		}
		return r ;
	}

	@Override
	public int getCmdID()
	{
		return this.cmdId;
	}
	
	@Override
	public String getCmdName()
	{
		return "cluster";
	}
	
	public int getClusterID()
	{
		return this.clusterId ;
	}
	
	public ZCLCluster getCluster()
	{
		return this.cluster ;
	}
	
	public ZCLCmd getCmdDef()
	{
		return this.cmdDef ;
	}
	
	@Override
	protected int getPayloadLen()
	{
		if(pm_pk_data==null)
			return 0;
		return pm_pk_data.length ;
	}

	@Override
	protected int packPayloadDataTo(byte[] data, int offset)
	{
		if(pm_pk_data==null || pm_pk_data.length<=0)
			return 0;
		System.arraycopy(pm_pk_data, 0, data, offset, pm_pk_data.length);
		return pm_pk_data.length;
	}

	@Override
	protected boolean unpackPayloadData(byte[] data, int offset, StringBuilder failedr)
	{
		return true;
	}

	@Override
	protected JSONObject getPayloadJO()
	{
		return null;//new JSONObject().put("cluster_cmd_id",this.cmdId);
	}


}
