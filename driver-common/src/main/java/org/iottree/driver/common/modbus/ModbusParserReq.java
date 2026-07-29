package org.iottree.driver.common.modbus;

import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.List;

public abstract class ModbusParserReq extends ModbusParser
{
	int[] limitDevIds = null ;
	

	public ModbusParser asLimitDevIds(List<Integer> devids)
	{
		if(devids==null||devids.size()<=0)
		{
			limitDevIds=null;
			return this ;
		}
		
		int s = devids.size();
		int[] ids = new int[s] ;
		for(int i = 0 ; i < s ; i ++)
			ids[i] = devids.get(i);
		
		this.limitDevIds = ids ;
		return this ;
	}
	
	public boolean checkLimitDevId(int devid)
	{
		if(limitDevIds==null||limitDevIds.length<=0)
			return true;
		for(int did:this.limitDevIds)
			if(did==devid)
				return true ;
		return false;
	}
	
	public abstract ModbusCmd parseReqCmdInLoop(PushbackInputStream inputs) throws IOException ;
	// 

}
