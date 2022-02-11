package org.iottree.driver.common.modbus;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.List;

public class ModbusParserReq extends ModbusParser
{
	int pST = 0 ;
	
	int[] limitDevIds = null ;
	
	
	transient private int devId = -1 ;
	
	transient private short fc = -1 ;
	
	public ModbusParserReq()
	{
		
	}
	
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
	
	private short checkFC(short v)
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
	
	
	
	/**
	 * state machine in parse Modbus RTU
	 * @param inputs
	 * @return
	 * @throws IOException 
	 */
	public ModbusCmd parseReqCmdInLoopRTU(PushbackInputStream inputs) throws IOException
	{
		int c ;
		switch(pST)
		{
		case 0://git devid
			c = inputs.read();
			if(c<0)
				throw new IOException("end of stream") ;
			if(!checkLimitDevId(c))
				break ;
			devId = c ;
			pST = 1 ;
			break;
		case 1://git fc,
			c = inputs.read() ;
			if(c<0)
				throw new IOException("end of stream") ;
			fc = checkFC((short)c) ;
			if(fc<0)
			{
				inputs.unread(c);
				pST = 0;
				break ;
			}
			pST = 2;
			break;
		case 2:
			return parseReqFC(inputs) ;
		default:
			break ;
			
		}
		return null ;
	}
	
	
	
	private ModbusCmd parseReqFC(PushbackInputStream inputs) throws IOException
	{
		//int r = 0 ;
		switch(this.fc)
		{
		case ModbusCmd.MODBUS_FC_READ_COILS: //1
		case ModbusCmd.MODBUS_FC_READ_DISCRETE_INPUT:// = 0x02;
			return parseReqReadBits(inputs);
		case ModbusCmd.MODBUS_FC_READ_HOLD_REG:// = 0x03;
		case ModbusCmd.MODBUS_FC_READ_INPUT_REG:
			return parseReqReadInt16s(inputs) ;
		case ModbusCmd.MODBUS_FC_WRITE_SINGLE_COIL:
			return parseReqWriteBit(inputs) ;
		case ModbusCmd.MODBUS_FC_WRITE_SINGLE_REG:
			return parseReqWriteWord(inputs) ;
		default:
			System.out.println("fc=="+fc) ;
			return null ;
		}
	}
	
	
	
	private ModbusCmdReadBits parseReqReadBits(PushbackInputStream inputs) throws IOException
	{
			
//			pdata[2] = (byte) ((regAddr >> 8) & 0xFF) ;
//		    pdata[3] = (byte) (regAddr & 0xFF) ;
//		    pdata[4] = (byte) (regNum >> 8) ;
//		    pdata[5] = (byte) (regNum & 0xFF) ;
//		    
//		    int crc = modbus_crc16_check(pdata,6);
//		    pdata[6] = (byte)((crc>>8) & 0xFF) ;
//		    pdata[7] = (byte)(crc & 0xFF) ;
		    
			byte[] bs = new byte[8] ;
			readFill(inputs,bs,2,6) ;
			
			bs[0] = (byte)this.devId ;
			bs[1] = (byte)this.fc ;
			int crc = ModbusCmd.modbus_crc16_check(bs,6);
		    if(bs[6] != (byte)(((crc>>8)) & 0xFF) || bs[7] != ((byte)(crc & 0xFF)))
		    {
		    	inputs.unread(bs, 2, 6);
		    	inputs.unread(this.fc);
		    	pST = 0;
		    	return null ;
		    }
		    
		    int reg_addr = bs[2] & 0xFF ;
		    reg_addr <<= 8 ;
		    reg_addr += (bs[3] &0xFF) ;
		    
		    int reg_num = bs[4] & 0xFF ;
		    reg_num <<= 8 ;
		    reg_num += (bs[5] &0xFF) ;
		    
		    ModbusCmdReadBits r = new ModbusCmdReadBits(this.devId,fc) ; ;
		    r.regAddr = reg_addr ;
		    r.regNum = reg_num ;
		    return r ;
		    
	}
	
	//private transient ModbusCmdReadWords mcReadWords = null ;
	
	private ModbusCmdReadWords parseReqReadInt16s(PushbackInputStream inputs) throws IOException
	{
//			pdata[2] = (byte) ((regAddr >> 8) & 0xFF) ;
//		    pdata[3] = (byte) (regAddr & 0xFF) ;
//		    pdata[4] = (byte) (regNum >> 8) ;
//		    pdata[5] = (byte) (regNum & 0xFF) ;
//		    
//		    int crc = modbus_crc16_check(pdata,6);
//		    pdata[6] = (byte)((crc>>8) & 0xFF) ;
//		    pdata[7] = (byte)(crc & 0xFF) ;
		    
			byte[] bs = new byte[8] ;
			readFill(inputs,bs,2,6) ;
			
			bs[0] = (byte)this.devId ;
			bs[1] = (byte)this.fc ;
			int crc = ModbusCmd.modbus_crc16_check(bs,6);
		    if(bs[6] != (byte)(((crc>>8)) & 0xFF) || bs[7] != ((byte)(crc & 0xFF)))
		    {
		    	inputs.unread(bs, 2, 6);
		    	inputs.unread(this.fc);
		    	pST = 0;
		    	return null ;
		    }
		    
		    int reg_addr = bs[2] & 0xFF ;
		    reg_addr <<= 8 ;
		    reg_addr += (bs[3] &0xFF) ;
		    
		    int reg_num = bs[4] & 0xFF ;
		    reg_num <<= 8 ;
		    reg_num += (bs[5] &0xFF) ;
		    
		    ModbusCmdReadWords r = new ModbusCmdReadWords(this.devId,fc) ;
		    r.regAddr = reg_addr ;
		    r.regNum = reg_num ;
		    return r ;
	}
	
	//private transient ModbusCmdWriteBit mcWriteBit = null ;
	
	private ModbusCmdWriteBit parseReqWriteBit(PushbackInputStream inputs) throws IOException
	{
//	    pdata[2] = (byte) ((regAddr >> 8) & 0xFF) ;
//	    pdata[3] = (byte) (regAddr & 0xFF) ;
//	    if(bwVal)
//	    	pdata[4] = (byte) 0xFF ;
//	    else
//	    	pdata[4] = (byte) 0x0 ;
//	    pdata[5] = 0 ;
//	    
//	    int crc = modbus_crc16_check(pdata,6);
//	    pdata[6] = (byte)((crc>>8) & 0xFF) ;
//	    pdata[7] = (byte)(crc & 0xFF) ;
		
		byte[] bs = new byte[8] ;
		readFill(inputs,bs,2,6) ;
		
		bs[0] = (byte)this.devId ;
		bs[1] = (byte)this.fc ;
		int crc = ModbusCmd.modbus_crc16_check(bs,6);
	    if(bs[6] != (byte)(((crc>>8)) & 0xFF) || bs[7] != ((byte)(crc & 0xFF)))
	    {
	    	inputs.unread(bs, 2, 6);
	    	inputs.unread(this.fc);
	    	pST = 0;
	    	return null ;
	    }
	    
	    int reg_addr = bs[2] & 0xFF ;
	    reg_addr <<= 8 ;
	    reg_addr += (bs[3] &0xFF) ;
	    
	    boolean bv = (bs[4] & 0xFF)==0xFF ;
	    //reg_num <<= 8 ;
	    //reg_num += (bs[5] &0xFF) ;
	    ModbusCmdWriteBit r = new ModbusCmdWriteBit(this.devId) ;
	    r.regAddr = reg_addr ;
	    r.bwVal =bv;
	    return r ;
	}
	
	
	//private transient ModbusCmdWriteWord mcWriteWord = null ;
	
	private ModbusCmdWriteWord parseReqWriteWord(PushbackInputStream inputs) throws IOException
	{
//		if(mcWriteWord==null)
//			mcWriteWord = new ModbusCmdWriteWord(this.devId) ;
//	    pdata[2] = (byte) ((regAddr >> 8) & 0xFF) ;
//	    pdata[3] = (byte) (regAddr & 0xFF) ;
//	    if(bwVal)
//	    	pdata[4] = (byte) 0xFF ;
//	    else
//	    	pdata[4] = (byte) 0x0 ;
//	    pdata[5] = 0 ;
//	    
//	    int crc = modbus_crc16_check(pdata,6);
//	    pdata[6] = (byte)((crc>>8) & 0xFF) ;
//	    pdata[7] = (byte)(crc & 0xFF) ;
		
		byte[] bs = new byte[8] ;
		readFill(inputs,bs,2,6) ;
		
		bs[0] = (byte)this.devId ;
		bs[1] = (byte)this.fc ;
		int crc = ModbusCmd.modbus_crc16_check(bs,6);
	    if(bs[6] != (byte)(((crc>>8)) & 0xFF) || bs[7] != ((byte)(crc & 0xFF)))
	    {
	    	inputs.unread(bs, 2, 6);
	    	inputs.unread(this.fc);
	    	pST = 0;
	    	return null ;
	    }
	    
	    int reg_addr = bs[2] & 0xFF ;
	    reg_addr <<= 8 ;
	    reg_addr += (bs[3] &0xFF) ;
	    
	    int v = bs[4] & 0xFF ;
	    v <<= 8 ;
	    v += (bs[5] &0xFF) ;
	    
	    ModbusCmdWriteWord r = new ModbusCmdWriteWord(this.devId) ;
	    r.regAddr = reg_addr ;
	    r.wVal =v;
	    return r ;
	}
	
	
}
