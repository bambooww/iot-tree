package org.iottree.driver.zigbee.zspec.cluster;

import org.iottree.driver.zigbee.zcl.ZCLClusterManager;
import org.iottree.driver.zigbee.zspec.ZSCluster;
import org.iottree.driver.zigbee.zspec.ZSEndPoint;

public class ZSCluster_genOnOff extends ZSCluster
{
	public ZSCluster_genOnOff(ZSEndPoint owner)
	{
		super(owner, ZCLClusterManager.getInstance().getCluserByID(0x0006));
	}

}
