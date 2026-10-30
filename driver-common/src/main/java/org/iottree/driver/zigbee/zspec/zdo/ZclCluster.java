package org.iottree.driver.zigbee.zspec.zdo;

import org.iottree.core.util.Convert;

public abstract class ZclCluster
{
	protected byte[] data = null ;
	
	public abstract int getClusterId();
	
	public abstract String getClusterName() ;
	
	public abstract int getClusterPackMinLen();
	
	public byte[] getClusterPack()
	{
		return data ;
	}
	
	public final boolean fromClusterPack(byte[] data, StringBuilder failedr)
	{
		if(data==null||data.length<getClusterPackMinLen())
			return false;
		this.data = data ;
		return true ;
	}
	
	@Override
	public String toString()
	{
		return "Cluster[0x"+String.format("%04X",this.getClusterId())+"]";
	}
	
}
