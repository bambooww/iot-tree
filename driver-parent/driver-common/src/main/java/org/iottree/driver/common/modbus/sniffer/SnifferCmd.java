package org.iottree.driver.common.modbus.sniffer;

import org.iottree.driver.common.modbus.ModbusCmd;

public class SnifferCmd
{
	public static String createUniqueId(int devid,int fc,int regpos,int regnum)
	{
		return devid+"_"+fc+"_"+regpos+"_"+regnum ;
	}
	
	
	int devId = -1 ;
	
	int fc = -1 ;
	
	int regPos = -1 ;
	
	int regNum = -1 ;
	
	
	ModbusCmd findedCmd = null ;
	
	byte[] findedData = null ;
	
	public String getUniqueId()
	{
		return createUniqueId(devId,this.fc,this.regPos,this.regNum);
	}
	
	public int getDevId()
	{
		return devId ;
	}
	
	public ModbusCmd getFindedCmd()
	{
		return findedCmd ;
	}
	
	public byte[] getFindedData()
	{
		return findedData ;
	}
}
