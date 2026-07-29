package org.iottree.driver.common.modbus;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.List;

import kotlin.NotImplementedError;

public class ModbusParserReqTCP extends ModbusParserReq
{
	int pST = 0 ;
	
	private transient int lastTransId = -1 ;

	transient private int devId = -1 ;
	
	transient private short fc = -1 ;
	
	transient private byte[] mbap = null ;
	transient int transId = -1 ;
	transient int protoId = -1 ;
	transient private int len = -1 ;
	
	public ModbusParserReqTCP()
	{
		
	}
	
	public ModbusCmd parseReqCmdInLoop(PushbackInputStream inputs) throws IOException
	{
		byte[] bs = new byte[7] ;
		readFill(inputs,bs,0,7) ;
		transId = (bs[0]&0xFF)<<8 ;
		transId += bs[1] & 0xff ;
		protoId = (bs[2]&0xFF)<<8 ;
		protoId += bs[3] & 0xff ;
		if(protoId!=0)
		{
			inputs.unread(bs, 1, 6);
			return null;
		}
		len = bs[4] & 0xFF ;
		len<<=8 ;
		len += (bs[5] & 0xFF) ;
		if(len>260)
		{
			inputs.unread(bs, 1, 6);
			return null;
		}
		mbap = bs ;
		devId = bs[6] &0xFF ;
		if(!checkLimitDevId(devId))
		{
			inputs.unread(bs, 1, 6);
			return null ;
		}
		fc = (short)inputs.read();
		if(fc<0)
			throw new IOException("end of stream") ;
		int dlen = checkReqFCDataLen(fc) ;
		if(dlen<0 || (dlen>0 && dlen!=len-2))
		{
			inputs.unread(fc);
			inputs.unread(bs, 1, 6);
			return null ;
		}
		
		if(dlen==0)
		{
			dlen = len-2 ;
		}
		
		byte[] data = new byte[dlen];
		readFill(inputs,data,0,dlen) ;
		
		return parseReqFC(data) ;
		
	}
	
	private int checkReqFCDataLen(int fc) throws IOException
	{
		//int r = 0 ;
		switch(fc)
		{
		case ModbusCmd.MODBUS_FC_READ_COILS: //1
		case ModbusCmd.MODBUS_FC_READ_DISCRETE_INPUT:// = 0x02;
			return 4 ;
		case ModbusCmd.MODBUS_FC_READ_HOLD_REG:// = 0x03;
		case ModbusCmd.MODBUS_FC_READ_INPUT_REG:
			return 4 ;
		case ModbusCmd.MODBUS_FC_WRITE_SINGLE_COIL:
		case ModbusCmd.MODBUS_FC_WRITE_SINGLE_REG:
			return 4 ;
		case ModbusCmd.MODBUS_FC_WRITE_MULTI_REG:
		case ModbusCmd.MODBUS_FC_WRITE_MULTI_COIL:
			return 0 ;
		default:
			return -1 ;
		}
	}
	
	private ModbusCmd parseReqFC(byte[] data) throws IOException
	{
		//int r = 0 ;
		switch(this.fc)
		{
		case ModbusCmd.MODBUS_FC_READ_COILS: //1
		case ModbusCmd.MODBUS_FC_READ_DISCRETE_INPUT:// = 0x02;
			return parseReqReadBits(data);
		case ModbusCmd.MODBUS_FC_READ_HOLD_REG:// = 0x03;
		case ModbusCmd.MODBUS_FC_READ_INPUT_REG:
			return parseReqReadInt16s(data) ;
		case ModbusCmd.MODBUS_FC_WRITE_SINGLE_COIL:
			return parseReqWriteBit(data) ;
		case ModbusCmd.MODBUS_FC_WRITE_SINGLE_REG:
			return parseReqWriteWord(data) ;
		case ModbusCmd.MODBUS_FC_WRITE_MULTI_REG:
			return parseReqWriteWords(data) ;
		case ModbusCmd.MODBUS_FC_WRITE_MULTI_COIL:
			//return parseReqWriteBits(data) ;
		default:
			return new ModbusCmdErr(ModbusCmd.Protocol.tcp,mbap,
					(short)this.devId,(short)fc,(short)0x04) ;
		}
	}
	
	
	
	private ModbusCmdReadBits parseReqReadBits(byte[] bs) throws IOException
	{

		    int reg_addr = bs[0] & 0xFF ;
		    reg_addr <<= 8 ;
		    reg_addr += (bs[1] &0xFF) ;
		    
		    int reg_num = bs[2] & 0xFF ;
		    reg_num <<= 8 ;
		    reg_num += (bs[3] &0xFF) ;
		    //if(reg_num<=0|| reg_num>200)
		    //	return null ;
		    //System.out.println("fc="+fc+"reg addr="+reg_addr+" reg num="+reg_num) ;
		    ModbusCmdReadBits r = new ModbusCmdReadBits(this.devId,fc) ; ;
		    r.regAddr = reg_addr ;
		    r.regNum = reg_num ;
		    r.protocal = ModbusCmd.Protocol.tcp ;
		    r.mbap4Tcp = this.mbap ;
		    return r ;
	}
	
	//private transient ModbusCmdReadWords mcReadWords = null ;
	
	private ModbusCmdReadWords parseReqReadInt16s(byte[] bs) throws IOException
	{
		    int reg_addr = bs[0] & 0xFF ;
		    reg_addr <<= 8 ;
		    reg_addr += (bs[1] &0xFF) ;
		    
		    int reg_num = bs[2] & 0xFF ;
		    reg_num <<= 8 ;
		    reg_num += (bs[3] &0xFF) ;
		    
		    ModbusCmdReadWords r = new ModbusCmdReadWords(this.devId,fc) ;
		    r.regAddr = reg_addr ;
		    r.regNum = reg_num ;
		    r.protocal = ModbusCmd.Protocol.tcp ;
		    r.mbap4Tcp = this.mbap ;
		    return r ;
	}
	
	//private transient ModbusCmdWriteBit mcWriteBit = null ;
	
	private ModbusCmdWriteBit parseReqWriteBit(byte[] bs) throws IOException
	{
	    int reg_addr = bs[0] & 0xFF ;
	    reg_addr <<= 8 ;
	    reg_addr += (bs[1] &0xFF) ;
	    
	    boolean bv = (bs[2] & 0xFF)==0xFF ;
	    //reg_num <<= 8 ;
	    //reg_num += (bs[5] &0xFF) ;
	    ModbusCmdWriteBit r = new ModbusCmdWriteBit(this.devId) ;
	    r.regAddr = reg_addr ;
	    r.bwVal =bv;
	    r.protocal = ModbusCmd.Protocol.tcp ;
	    r.mbap4Tcp = this.mbap ;
	    return r ;
	}
	
	
	//private transient ModbusCmdWriteWord mcWriteWord = null ;
	
	private ModbusCmdWriteWord parseReqWriteWord(byte[] bs) throws IOException
	{
	    int reg_addr = bs[0] & 0xFF ;
	    reg_addr <<= 8 ;
	    reg_addr += (bs[1] &0xFF) ;
	    
	    int v = bs[2] & 0xFF ;
	    v <<= 8 ;
	    v += (bs[3] &0xFF) ;
	    
	    ModbusCmdWriteWord r = new ModbusCmdWriteWord(this.devId) ;
	    r.regAddr = reg_addr ;
	    r.wVal =v;
	    r.protocal = ModbusCmd.Protocol.tcp ;
	    r.mbap4Tcp = this.mbap ;
	    return r ;
	}
	
	private ModbusCmdWriteWords parseReqWriteWords(byte[] bs) throws IOException
	{
	    int reg_addr = bs[0] & 0xFF ;
	    reg_addr <<= 8 ;
	    reg_addr += (bs[1] &0xFF) ;
	    
	    int reg_n = bs[2] & 0xFF;
		reg_n <<= 8;
		reg_n += (bs[3] & 0xFF);

		int byte_n = bs[4] & 0xFF;
		if (byte_n != reg_n * 2||byte_n>260)
		{
			//inputs.unread(bs);
			//inputs.unread(fc);
			return null;
		}
		
		if(byte_n!=bs.length-5)
		{
			return null ;
		}
		int[] vals = new int[reg_n];
		for(int i = 0 ; i < reg_n ; i ++)
		{
			int tmpv = bs[5+i*2] & 0xFF ;
			tmpv <<= 8 ;
			tmpv += bs[5+i*2+1] & 0xFF ;
			vals[i] = tmpv ;
		}
	    //System.out.println(this.devId+" "+reg_addr+" "+vals.length) ;
	    ModbusCmdWriteWords r = new ModbusCmdWriteWords(this.devId,reg_addr,vals) ;
	    r.protocal = ModbusCmd.Protocol.tcp ;
	    r.mbap4Tcp = this.mbap ;
	    return r ;
	}
}
