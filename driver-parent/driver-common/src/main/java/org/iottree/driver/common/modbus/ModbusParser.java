package org.iottree.driver.common.modbus;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.List;

public abstract class ModbusParser
{

	protected short checkFC(short v)
	{
		switch(v)
		{
		case ModbusCmd.MODBUS_FC_READ_COILS: //1
		case ModbusCmd.MODBUS_FC_READ_DISCRETE_INPUT:// = 0x02;
		case ModbusCmd.MODBUS_FC_READ_HOLD_REG:// = 0x03;
		case ModbusCmd.MODBUS_FC_READ_INPUT_REG:
		case ModbusCmd.MODBUS_FC_WRITE_SINGLE_COIL:
		case ModbusCmd.MODBUS_FC_WRITE_SINGLE_REG:
			return v ;
		default:
			return -1 ;
		}
	}
	
	protected static void readFill(InputStream ins,byte[] bs,int offset,int len) throws IOException
	{
		int rlen = 0 ;
		int r = 0 ;
		while((r=ins.read(bs, rlen+offset, len-rlen))>=0)
		{
			rlen += r ;
			if(rlen==len)
				return ;
		}
		
		if(r<0)
			throw new IOException("end of stream") ;
	}
}
