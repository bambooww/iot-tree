package org.iottree.driver.common.modbus.sniffer;

import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.basic.ByteOrder;
import org.iottree.core.util.xmldata.DataUtil;
import org.iottree.driver.common.ModbusAddr;
import org.iottree.driver.common.modbus.ModbusCmd;
import org.iottree.driver.common.modbus.ModbusCmdRead;

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
	
	
	
	
	ModbusCmdRead findedCmd = null ;
	
	byte[] findedData = null ;
	
	long findedDT = -1 ;
	
	public SnifferCmd(ModbusCmdRead mc)
	{
		this.devId = mc.getDevAddr();
		this.fc= mc.getFC() ;
		this.regPos = mc.getRegAddr() ;
		this.regNum = mc.getRegNum() ;
		this.findedCmd = mc ;
	}
	
	public String getUniqueId()
	{
		return createUniqueId(devId,this.fc,this.regPos,this.regNum);
	}
	
	public int getDevId()
	{
		return devId ;
	}
	
	public int getFC()
	{
		return this.fc;
	}
	
	public ModbusCmdRead getFindedCmd()
	{
		return findedCmd ;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getRespLen()
	{
		if(this.findedCmd==null)
			return -1 ;
		return this.findedCmd.calRespLenRTU();
	}
	
	public boolean parseResp(byte[] respbs)
	{
		int devid = (respbs[0] & 0xFF) ;
		if(devid!=findedCmd.getDevAddr())
			return false;
		if(findedCmd.getFC()!=(respbs[1] & 0xFF))
			return false;
		//check crc
		int len = respbs.length ;
		int crc = ModbusCmd.modbus_crc16_check(respbs, len-2) ;
		if(respbs[len-2]!=(byte)((crc>>8) & 0xFF) ||
				respbs[len-1]!=(byte)(crc & 0xFF))
		{
			return false ;
		}
		findedData = new byte[len-5] ;
		System.arraycopy(respbs, 3, findedData, 0, len-5);
		findedDT = System.currentTimeMillis() ;
		return true;
	}
	
	public byte[] getFindedData()
	{
		return findedData ;
	}
	
	public long getFindedDT()
	{
		return this.findedDT;
	}
	
	public Object getValByAddr(ModbusAddr ma)
	{
		UAVal.ValTP vt = ma.getValTP();
		if(vt==null)
			return null ;
		if(vt==UAVal.ValTP.vt_bool)
		{
			int regp = ma.getRegPos() ;
			int byte_ps = regp/8 ;
			if(byte_ps<regPos)
				return null ;
			if(byte_ps>=regPos+regNum)
				return null ;
			int idx = byte_ps -  regPos ;
			byte b = this.findedData[idx];
			boolean r = (b & (2<<(byte_ps%8))) > 0;
			return r ;
		}
		
		int regp = ma.getRegPos() ;
		if(regp<regPos)
			return null ;
		
		int idx = (regp-regPos)*2;
		switch(vt)
		{
		case vt_int16:
		case vt_uint16:
			if(regp+1>regPos+regNum)
				return null ;
			short shortv = DataUtil.bytesToShort(findedData, idx);
			if(vt==ValTP.vt_uint16)
				return ((int)shortv)&0xffff ;
			else
				return shortv ;
		case vt_int32:
		case vt_uint32:
			if(regp+2>regPos+regNum)
				return null ;
			int intv = DataUtil.bytesToInt(findedData, idx,ByteOrder.ModbusWord);
			if(vt==ValTP.vt_uint32)
				return ((long)intv)&0xffffffff ;
			else
				return intv ;
		case vt_int64:
			if(regp+4>regPos+regNum)
				return null ;
			return DataUtil.bytesToLong(findedData, idx, ByteOrder.ModbusWord) ;
		case vt_float:
			if(regp+2>regPos+regNum)
				return null ;
			return DataUtil.bytesToFloat(findedData, idx);
		case vt_double:
			if(regp+4>regPos+regNum)
				return null ;
			return DataUtil.bytesToDouble(findedData,idx);
		default:
			return null ;
		}
	}
	
	public String toString()
	{
		return devId+" fc="+fc+" regnum="+regNum ;
	}
}
