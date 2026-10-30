package org.iottree.driver.zigbee.zspec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.driver.zigbee.zcl.ZCLCluster;
import org.iottree.driver.zigbee.zcl.ZCLClusterManager;
import org.iottree.driver.zigbee.zcl.cmd_frm.ReportAttr;
import org.iottree.driver.zigbee.zstack.msg_zdo.SimpleDescRspAREQ_CB;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 运行过程中使用的end point类，包含在ZStackEndPoint中
 * @author jason.zhu
 */
public class ZSEndPoint
{
	ZSDevice owner ;
	
	int id ; //end point id
	
	int deviceId ; // 设备类型
	
	int profileId ; // 设备大类
	
	List<Integer> in_cluster_ids = new ArrayList<>() ;
	
	List<Integer> out_cluster_ids = new ArrayList<>() ;
	
	private ArrayList<ZSCluster> in_clusters = null;
	
	private ArrayList<ZSCluster> out_clusters = null;
	
	public ZSEndPoint(ZSDevice ed,int ep_id)
	{
		this.owner = ed ;
		this.id = ep_id ;
	}
	
	public ZSDevice getOwner()
	{
		return this.owner ;
	}
	
	public int getEndPointId()
	{
		return this.id;
	}
	
	public List<Integer> getInClusterIds()
	{
		return this.in_cluster_ids ;
	}
	
	public List<ZSCluster> getInClusters()
	{
		if(this.in_clusters!=null)
			return this.in_clusters ;
		ArrayList<ZSCluster> cls = new ArrayList<>() ;
		for(Integer cid:this.in_cluster_ids)
		{
			ZCLCluster c = ZCLClusterManager.getInstance().getCluserByID(cid) ;
			if(c==null)
			{
				System.err.println("no cluster found with id="+cid) ;
				continue ;
			}
			ZSCluster zsc = new ZSCluster(this,c) ;
			cls.add(zsc) ;
		}
		return this.in_clusters = cls ;
	}
	
	public ZSCluster getInCluster(int cluster_id)
	{
		if(!in_cluster_ids.contains(cluster_id))
			return null ;
		
		for(ZSCluster c: getInClusters())
		{
			if(c.getClusterId()==cluster_id)
				return c;
		}
		return null ;
	}
	

	public List<Integer> getOutClusterIds()
	{
		return this.out_cluster_ids ;
	}
	
	public List<ZSCluster> getOutClusters()
	{
		if(this.out_clusters!=null)
			return this.out_clusters ;
		ArrayList<ZSCluster> cls = new ArrayList<>() ;
		for(Integer cid:this.out_cluster_ids)
		{
			ZCLCluster c = ZCLClusterManager.getInstance().getCluserByID(cid) ;
			if(c==null)
			{
				System.err.println("no cluster found with id="+cid) ;
				continue ;
			}
			ZSCluster zsc = new ZSCluster(this,c) ;
			cls.add(zsc) ;
		}
		return this.out_clusters = cls ;
	}
	
	public ZSCluster getOutCluster(int cluster_id)
	{
		if(!out_cluster_ids.contains(cluster_id))
			return null ;
		
		for(ZSCluster c: getOutClusters())
		{
			if(c.getClusterId()==cluster_id)
				return c;
		}
		return null ;
	}
	
	//for syn tags in UADev
	public int setupTagsJArr(JSONArray ret)
	{
		int r = 0 ;
		for(int c:this.in_cluster_ids)
		{
			ZSCluster zsc = this.getInCluster(c) ;
			if(zsc==null)
				continue ;
			r += zsc.setupTagsJArr(ret) ;
		}
		return r;
	}
	
	
	/**
	 * 根据rsp对象进行更新
	 * @param sdr
	 * @return true=有修改
	 */
	public boolean MGR_updateBy(SimpleDescRspAREQ_CB sdr)
	{
		int epid = sdr.getEndpoint() ;
		if(id!=epid)
			return false;
		boolean b_dirty = false;
		int dev_id = sdr.getDeviceId() ;
		if(deviceId!=dev_id)
		{
			this.deviceId = dev_id ;
			b_dirty = true ;
		}
		int prof_id = sdr.getProfileId() ;
		if(this.profileId!=prof_id)
		{
			this.profileId = prof_id ;
			b_dirty = true ;
		}
		List<Integer> ids = sdr.getInClusterList() ;
		if(!checkIdsSame(this.in_cluster_ids,ids))
		{
			this.in_cluster_ids = ids ;
			b_dirty = true;
		}
		
		ids = sdr.getOutClusterList() ;
		if(!checkIdsSame(this.out_cluster_ids,ids))
		{
			this.out_cluster_ids = ids ;
			b_dirty = true;
		}
		return b_dirty ;//
	}
	
	private boolean checkIdsSame(List<Integer> ids1,List<Integer> ids2)
	{
		if(ids1.size()!=ids2.size())
			return false;
		int n = ids1.size() ;
		for(int i = 0 ; i < n ; i ++)
		{
			int id1 = ids1.get(i) ;
			int id2 = ids2.get(i) ;
			if(id1!=id2)
				return false;
		}
		return true ;
	}
	
	public JSONObject toJO()
	{
		JSONObject ret = new JSONObject() ;
		ret.put("endpoint_id", this.id) ;
		ret.put("device_id", this.deviceId) ;
		ret.put("profile_id", this.profileId) ;
		ret.put("in_cluster_ids", this.in_cluster_ids) ;
		ret.put("out_cluster_ids", this.out_cluster_ids) ;
		
		JSONArray jarr = new JSONArray() ;
		ret.put("in_clusters",jarr) ;
		for(ZSCluster zsc:this.getInClusters())
		{
			jarr.put(zsc.toJO()) ;
		}
		jarr = new JSONArray() ;
		ret.put("out_clusters",jarr) ;
		for(ZSCluster zsc:this.getOutClusters())
		{
			jarr.put(zsc.toJO()) ;
		}
		return ret ;
	}
	
	public static ZSEndPoint fromJO(ZSDevice ed,JSONObject jo,StringBuilder failedr)
	{
		int ep_id = jo.optInt("endpoint_id",-1);
		if(ep_id<0)
			return null ;
		ZSEndPoint r = new ZSEndPoint(ed,ep_id) ;
		r.deviceId = jo.optInt("device_id",-1) ;
		r.profileId = jo.optInt("profile_id", -1);
		JSONArray ids_jarr = jo.optJSONArray("in_cluster_ids") ;
		if(ids_jarr!=null)
		{
			for(int i = 0 ; i < ids_jarr.length() ; i ++)
				r.in_cluster_ids.add(ids_jarr.getInt(i)) ;
		}
		ids_jarr = jo.optJSONArray("out_cluster_ids") ;
		if(ids_jarr!=null)
		{
			for(int i = 0 ; i < ids_jarr.length() ; i ++)
				r.out_cluster_ids.add(ids_jarr.getInt(i)) ;
		}
		
		return r ;
	}
	
	public boolean RT_onReportAttr(int cluster_id,ReportAttr ra,List<ZSAttrVal> ret,StringBuilder failedr)
	{
		ZSCluster c = this.getInCluster(cluster_id) ;
		if(c==null)
		{
			failedr.append("no cluster found with id="+cluster_id) ;
			return false ;
		}
		
		List<ZSAttrVal> avs = c.RT_onReportAttr(ra) ;
		ret.addAll(avs) ;
		return true ;
	}
}
