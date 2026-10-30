package org.iottree.driver.zigbee.zspec.event;

import org.iottree.driver.zigbee.zcl.ZCLCluster;
import org.iottree.driver.zigbee.zcl.ZCLClusterManager;
import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.iottree.driver.zigbee.zspec.*;
import org.iottree.driver.zigbee.zspec.event.IncomeMsgEvt;

public class IncomeMsgDefEvt implements IncomeMsgEvt
{
	int groupId ;
	
	int clusterId ;
	
	int src_ep ;
	
	int dst_ep ;
	
	int src_addr ;
	
	int linkQual  ;
	
	boolean bBroadcast ;
	
	ZCLFrame zclFrm ;
	
	public IncomeMsgDefEvt(int grpid,int clusterid,int src_ep,int dst_ep,
			int src_addr,boolean b_bcast,int link_qual,ZCLFrame zcl_f)
	{
		this.groupId = grpid ;
		this.clusterId = clusterid ;
		this.src_ep = src_ep ;
		this.dst_ep = dst_ep ;
		this.src_addr = src_addr ;
		this.bBroadcast = b_bcast ;
		this.zclFrm = zcl_f ;
		this.linkQual = link_qual ;
	}
	
	@Override
	public int getGroupId()
	{
		return this.groupId;
	}

	@Override
	public int getClusterId()
	{
		return this.clusterId ;
	}

	@Override
	public ZCLCluster getCluster()
	{
		return ZCLClusterManager.getInstance().getCluserByID(this.getClusterId()) ;
	}

	@Override
	public int getSrcEndpoint()
	{
		return this.src_ep ;
	}

	@Override
	public int getDstEndpoint()
	{
		return this.dst_ep ;
	}

	@Override
	public int getSrcAddr()
	{
		return this.src_addr ;
	}

	@Override
	public boolean getWasBroadcast()
	{
		return this.bBroadcast ;
	}

	@Override
	public int getLinkQuality()
	{
		return this.linkQual;
	}

	@Override
	public ZCLFrame getZCLFrame(StringBuilder failedr)
	{
		return this.zclFrm;
	}

}
