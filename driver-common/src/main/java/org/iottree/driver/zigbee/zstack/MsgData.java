package org.iottree.driver.zigbee.zstack;

public abstract class MsgData extends Msg
{
	protected byte[] data = null;
	
	public MsgData()
	{
		super() ;
	}
	
	public MsgData(int cmd0,int cmd1)
	{
		super(cmd0,cmd1) ;
	}
	
	public MsgData(byte cmd0_type,byte cmd0_sub,int cmd1)
	{
		super(cmd0_type, cmd0_sub, cmd1);
	}
	
	public abstract int getDataLenMin() ;
	
	public boolean isCluster()
	{
		return false ;
	}
	
	protected byte[] getClusterData()
	{
		return null;
	}
	
	protected boolean parseClusterData(byte[] data, StringBuilder failedr)
	{
		return false;
	}
	
	@Override
	public final byte[] getData()
	{
		if(this.isCluster())
			return getClusterData();
		return this.data;
	}
	
	@Override
	protected boolean parseData(byte[] data, StringBuilder failedr)
	{
		if(this.isCluster())
		{
			return parseClusterData(data,failedr) ;
		}
		
		int dlen = getDataLenMin() ;
		if(dlen<=0)
			return true ;
		
		if(data==null||data.length<dlen)
			return false;
		
		this.data = data ;
		return true;
	}
}
