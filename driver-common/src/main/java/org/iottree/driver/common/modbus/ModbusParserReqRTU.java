package org.iottree.driver.common.modbus;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.List;

import kotlin.NotImplementedError;

public class ModbusParserReqRTU extends ModbusParserReq
{
	int pST = 0;

	transient private int devId = -1;

	transient private int fc = -1;

	public ModbusParserReqRTU()
	{

	}

	public ModbusCmd parseReqCmdInLoop(PushbackInputStream inputs) throws IOException
	{
		byte[] bs = new byte[2];
		readFill(inputs, bs, 0, 2);
		this.devId = bs[0] & 0xFF;
		if (!checkLimitDevId(devId))
		{
			inputs.unread(bs, 1, 1);
			return null;
		}
		fc = bs[1] & 0xFF;
		int dlen = checkReqFCDataLen(fc);
		if (dlen < 0)
		{
			inputs.unread(fc);
			return null;
		}

		if (dlen > 0)
		{
			byte[] pkbs = new byte[2 + dlen];
			readFill(inputs, pkbs, 2, dlen);
			pkbs[0] = bs[0];
			pkbs[1] = bs[1];
			int crc = ModbusCmd.modbus_crc16_check(pkbs, 6);
			if (pkbs[6] != (byte) (((crc >> 8)) & 0xFF) || pkbs[7] != ((byte) (crc & 0xFF)))
			{
				inputs.unread(pkbs, 2, 6);
				inputs.unread(this.fc);
				return null;
			}
			return parseReqFC(pkbs);
		}

		//
		switch (fc)
		{
		case ModbusCmd.MODBUS_FC_WRITE_MULTI_REG:
			return parseReqWriteWords(bs,inputs);
		case ModbusCmd.MODBUS_FC_WRITE_MULTI_COIL:
			return parseReqWriteBits(bs,inputs);
		default:
			return new ModbusCmdErr(ModbusCmd.Protocol.rtu,null,
					(short)this.devId,(short)fc,(short)0x04) ;
		}
	}


	private int checkReqFCDataLen(int fc) throws IOException
	{
		// int r = 0 ;
		switch (fc)
		{
		case ModbusCmd.MODBUS_FC_READ_COILS: // 1
		case ModbusCmd.MODBUS_FC_READ_DISCRETE_INPUT:// = 0x02;
			return 6;
		case ModbusCmd.MODBUS_FC_READ_HOLD_REG:// = 0x03;
		case ModbusCmd.MODBUS_FC_READ_INPUT_REG:
			return 6;
		case ModbusCmd.MODBUS_FC_WRITE_SINGLE_COIL:
		case ModbusCmd.MODBUS_FC_WRITE_SINGLE_REG:
			return 6;
		case ModbusCmd.MODBUS_FC_WRITE_MULTI_REG:
		case ModbusCmd.MODBUS_FC_WRITE_MULTI_COIL:
			return 0;
		default:
			return -1;
		}
	}

	private ModbusCmd parseReqFC(byte[] pkbs) throws IOException
	{
		// int r = 0 ;
		switch (this.fc)
		{
		case ModbusCmd.MODBUS_FC_READ_COILS: // 1
		case ModbusCmd.MODBUS_FC_READ_DISCRETE_INPUT:// = 0x02;
			return parseReqReadBits(pkbs);
		case ModbusCmd.MODBUS_FC_READ_HOLD_REG:// = 0x03;
		case ModbusCmd.MODBUS_FC_READ_INPUT_REG:
			return parseReqReadInt16s(pkbs);
		case ModbusCmd.MODBUS_FC_WRITE_SINGLE_COIL:
			return parseReqWriteBit(pkbs);
		case ModbusCmd.MODBUS_FC_WRITE_SINGLE_REG:
			return parseReqWriteWord(pkbs);
		default:
			//System.out.println("fc==" + fc);
			return new ModbusCmdErr(ModbusCmd.Protocol.rtu,null,
					(short)this.devId,(short)fc,(short)0x04) ;
		}
	}
	

	private ModbusCmd parseReqWriteWords(byte[] bs_h,PushbackInputStream inputs) throws IOException
	{
		byte[] bs = new byte[5];
		readFill(inputs, bs, 0, 5);

		int reg_addr = bs[0] & 0xFF;
		reg_addr <<= 8;
		reg_addr += (bs[1] & 0xFF);

		int reg_n = bs[2] & 0xFF;
		reg_n <<= 8;
		reg_n += (bs[3] & 0xFF);

		int byte_n = bs[4] & 0xFF;
		if (byte_n != reg_n * 2||byte_n>260)
		{
			inputs.unread(bs);
			inputs.unread(fc);
			return null;
		}
		byte[] vbs = new byte[byte_n+2] ;
		readFill(inputs,vbs,0,byte_n+2) ;
		int crc = ModbusCmd.modbus_crc16_check_seg(bs_h, 2, bs, 5, vbs, byte_n) ;
		if(vbs[vbs.length-2] != (byte)((crc>>8) & 0xFF) || vbs[vbs.length-1] != (byte)(crc & 0xFF))
		{
			inputs.unread(vbs);
			inputs.unread(bs);
			inputs.unread(fc);
			return null ;
		}
	    
		int[] vals = new int[reg_n];
		for(int i = 0 ; i < reg_n ; i ++)
		{
			int tmpv = vbs[i*2] & 0xFF ;
			tmpv <<= 8 ;
			tmpv += vbs[i*2+1] & 0xFF ;
			vals[i] = tmpv ;
		}
		
		ModbusCmdWriteWords r = new ModbusCmdWriteWords(this.devId, reg_addr, vals);
		return r;
	}

	private ModbusCmd parseReqWriteBits(byte[] bs_h,PushbackInputStream inputs) throws IOException
	{
		byte[] bs = new byte[5];
		readFill(inputs, bs, 0, 5);

		int reg_addr = bs[0] & 0xFF;
		reg_addr <<= 8;
		reg_addr += (bs[1] & 0xFF);

		int reg_n = bs[2] & 0xFF;
		reg_n <<= 8;
		reg_n += (bs[3] & 0xFF);

		int byte_n = bs[4] & 0xFF;
		int cc_bn = reg_n/8+((reg_n%8)>0?1:0) ;
		if (byte_n != cc_bn||byte_n>260)
		{
			inputs.unread(bs);
			inputs.unread(fc);
			return null;
		}
		byte[] vbs = new byte[byte_n+2] ;
		readFill(inputs,vbs,0,byte_n+2) ;
		int crc = ModbusCmd.modbus_crc16_check_seg(bs_h, 2, bs, 5, vbs, byte_n) ;
		if(vbs[vbs.length-2] != (byte)((crc>>8) & 0xFF) || vbs[vbs.length-1] != (byte)(crc & 0xFF))
		{
			inputs.unread(vbs);
			inputs.unread(bs);
			inputs.unread(fc);
			return null ;
		}
	    
		boolean[] vals = new boolean[reg_n];
		for(int i = 0 ; i < reg_n ; i ++)
		{
			int idx = i/8+((i%8)>0?1:0) ;
			int tmpv = vbs[idx] & 0xFF;
			vals[i] = (tmpv & (1<<(i%8))) > 0;
		}
		
		ModbusCmdWriteBits r = new ModbusCmdWriteBits(this.devId, reg_addr, vals);
		return r;
	}

	private ModbusCmdReadBits parseReqReadBits(byte[] bs) throws IOException
	{
		int reg_addr = bs[2] & 0xFF;
		reg_addr <<= 8;
		reg_addr += (bs[3] & 0xFF);

		int reg_num = bs[4] & 0xFF;
		reg_num <<= 8;
		reg_num += (bs[5] & 0xFF);

		ModbusCmdReadBits r = new ModbusCmdReadBits(this.devId, (short) fc);
		r.regAddr = reg_addr;
		r.regNum = reg_num;
		return r;

	}

	// private transient ModbusCmdReadWords mcReadWords = null ;

	private ModbusCmdReadWords parseReqReadInt16s(byte[] bs) throws IOException
	{
		int reg_addr = bs[2] & 0xFF;
		reg_addr <<= 8;
		reg_addr += (bs[3] & 0xFF);

		int reg_num = bs[4] & 0xFF;
		reg_num <<= 8;
		reg_num += (bs[5] & 0xFF);

		ModbusCmdReadWords r = new ModbusCmdReadWords(this.devId, (short) fc);
		r.regAddr = reg_addr;
		r.regNum = reg_num;
		return r;
	}

	private ModbusCmdWriteBit parseReqWriteBit(byte[] bs) throws IOException
	{
		int reg_addr = bs[2] & 0xFF;
		reg_addr <<= 8;
		reg_addr += (bs[3] & 0xFF);

		boolean bv = (bs[4] & 0xFF) == 0xFF;
		// reg_num <<= 8 ;
		// reg_num += (bs[5] &0xFF) ;
		ModbusCmdWriteBit r = new ModbusCmdWriteBit(this.devId);
		r.regAddr = reg_addr;
		r.bwVal = bv;
		// System.out.println("on parsed write bit reg_addr="+reg_addr+" v"+bv+"
		// th="+Thread.currentThread().getId()) ;
		return r;
	}

	// private transient ModbusCmdWriteWord mcWriteWord = null ;

	private ModbusCmdWriteWord parseReqWriteWord(byte[] bs) throws IOException
	{
		int reg_addr = bs[2] & 0xFF;
		reg_addr <<= 8;
		reg_addr += (bs[3] & 0xFF);

		int v = bs[4] & 0xFF;
		v <<= 8;
		v += (bs[5] & 0xFF);

		ModbusCmdWriteWord r = new ModbusCmdWriteWord(this.devId);
		r.regAddr = reg_addr;
		r.wVal = v;
		// System.out.println("on parsed write w reg_addr="+reg_addr+" v"+v+"
		// th="+Thread.currentThread().getId()) ;
		return r;
	}

	//
	// public ModbusCmd parseReqCmdInLoop0(PushbackInputStream inputs) throws
	// IOException
	// {
	// int c ;
	// switch(pST)
	// {
	// case 0://git devid
	// c = inputs.read();
	// if(c<0)
	// throw new IOException("end of stream") ;
	// if(!checkLimitDevId(c))
	// break ;
	// devId = c ;
	// pST = 1 ;
	// break;
	// case 1://git fc,
	// c = inputs.read() ;
	// if(c<0)
	// throw new IOException("end of stream") ;
	// fc = checkFC((short)c) ;
	// if(fc<0)
	// {
	// inputs.unread(c);
	// pST = 0;
	// break ;
	// }
	// pST = 2;
	// break;
	// case 2:
	// return parseReqFC(inputs) ;
	// default:
	// break ;
	//
	// }
	// return null ;
	// }
	//
	//
	//
	// private ModbusCmd parseReqFC(PushbackInputStream inputs) throws
	// IOException
	// {
	// //int r = 0 ;
	// switch(this.fc)
	// {
	// case ModbusCmd.MODBUS_FC_READ_COILS: //1
	// case ModbusCmd.MODBUS_FC_READ_DISCRETE_INPUT:// = 0x02;
	// return parseReqReadBits(inputs);
	// case ModbusCmd.MODBUS_FC_READ_HOLD_REG:// = 0x03;
	// case ModbusCmd.MODBUS_FC_READ_INPUT_REG:
	// return parseReqReadInt16s(inputs) ;
	// case ModbusCmd.MODBUS_FC_WRITE_SINGLE_COIL:
	// return parseReqWriteBit(inputs) ;
	// case ModbusCmd.MODBUS_FC_WRITE_SINGLE_REG:
	// return parseReqWriteWord(inputs) ;
	// default:
	// System.out.println("fc=="+fc) ;
	// return null ;
	// }
	// }
	//
	//
	//
	// private ModbusCmdReadBits parseReqReadBits(PushbackInputStream inputs)
	// throws IOException
	// {
	// byte[] bs = new byte[8] ;
	// readFill(inputs,bs,2,6) ;
	//
	// bs[0] = (byte)this.devId ;
	// bs[1] = (byte)this.fc ;
	// int crc = ModbusCmd.modbus_crc16_check(bs,6);
	// if(bs[6] != (byte)(((crc>>8)) & 0xFF) || bs[7] != ((byte)(crc & 0xFF)))
	// {
	// inputs.unread(bs, 2, 6);
	// inputs.unread(this.fc);
	// pST = 0;
	// return null ;
	// }
	//
	// int reg_addr = bs[2] & 0xFF ;
	// reg_addr <<= 8 ;
	// reg_addr += (bs[3] &0xFF) ;
	//
	// int reg_num = bs[4] & 0xFF ;
	// reg_num <<= 8 ;
	// reg_num += (bs[5] &0xFF) ;
	//
	// ModbusCmdReadBits r = new ModbusCmdReadBits(this.devId,(short)fc) ; ;
	// r.regAddr = reg_addr ;
	// r.regNum = reg_num ;
	// pST = 0;
	// return r ;
	//
	// }
	//
	// //private transient ModbusCmdReadWords mcReadWords = null ;
	//
	// private ModbusCmdReadWords parseReqReadInt16s(PushbackInputStream inputs)
	// throws IOException
	// {
	// byte[] bs = new byte[8] ;
	// readFill(inputs,bs,2,6) ;
	//
	// bs[0] = (byte)this.devId ;
	// bs[1] = (byte)this.fc ;
	// int crc = ModbusCmd.modbus_crc16_check(bs,6);
	// if(bs[6] != (byte)(((crc>>8)) & 0xFF) || bs[7] != ((byte)(crc & 0xFF)))
	// {
	// inputs.unread(bs, 2, 6);
	// inputs.unread(this.fc);
	// pST = 0;
	// return null ;
	// }
	//
	// int reg_addr = bs[2] & 0xFF ;
	// reg_addr <<= 8 ;
	// reg_addr += (bs[3] &0xFF) ;
	//
	// int reg_num = bs[4] & 0xFF ;
	// reg_num <<= 8 ;
	// reg_num += (bs[5] &0xFF) ;
	//
	// ModbusCmdReadWords r = new ModbusCmdReadWords(this.devId,(short)fc) ;
	// r.regAddr = reg_addr ;
	// r.regNum = reg_num ;
	// pST = 0;
	// return r ;
	// }
	//
	// //private transient ModbusCmdWriteBit mcWriteBit = null ;
	//
	// private ModbusCmdWriteBit parseReqWriteBit(PushbackInputStream inputs)
	// throws IOException
	// {
	// byte[] bs = new byte[8] ;
	// readFill(inputs,bs,2,6) ;
	//
	// bs[0] = (byte)this.devId ;
	// bs[1] = (byte)this.fc ;
	// int crc = ModbusCmd.modbus_crc16_check(bs,6);
	// if(bs[6] != (byte)(((crc>>8)) & 0xFF) || bs[7] != ((byte)(crc & 0xFF)))
	// {
	// inputs.unread(bs, 2, 6);
	// inputs.unread(this.fc);
	// pST = 0;
	// return null ;
	// }
	//
	// int reg_addr = bs[2] & 0xFF ;
	// reg_addr <<= 8 ;
	// reg_addr += (bs[3] &0xFF) ;
	//
	// boolean bv = (bs[4] & 0xFF)==0xFF ;
	// //reg_num <<= 8 ;
	// //reg_num += (bs[5] &0xFF) ;
	// ModbusCmdWriteBit r = new ModbusCmdWriteBit(this.devId) ;
	// r.regAddr = reg_addr ;
	// r.bwVal =bv;
	// pST = 0;
	// System.out.println("on parsed write bit reg_addr="+reg_addr+" v"+bv+"
	// th="+Thread.currentThread().getId()) ;
	// return r ;
	// }
	//
	//
	// //private transient ModbusCmdWriteWord mcWriteWord = null ;
	//
	// private ModbusCmdWriteWord parseReqWriteWord(PushbackInputStream inputs)
	// throws IOException
	// {
	// byte[] bs = new byte[8] ;
	// readFill(inputs,bs,2,6) ;
	//
	// bs[0] = (byte)this.devId ;
	// bs[1] = (byte)this.fc ;
	// int crc = ModbusCmd.modbus_crc16_check(bs,6);
	// if(bs[6] != (byte)(((crc>>8)) & 0xFF) || bs[7] != ((byte)(crc & 0xFF)))
	// {
	// inputs.unread(bs, 2, 6);
	// inputs.unread(this.fc);
	// pST = 0;
	// return null ;
	// }
	//
	// int reg_addr = bs[2] & 0xFF ;
	// reg_addr <<= 8 ;
	// reg_addr += (bs[3] &0xFF) ;
	//
	// int v = bs[4] & 0xFF ;
	// v <<= 8 ;
	// v += (bs[5] &0xFF) ;
	//
	// ModbusCmdWriteWord r = new ModbusCmdWriteWord(this.devId) ;
	// r.regAddr = reg_addr ;
	// r.wVal =v;
	// pST = 0;
	// //System.out.println("on parsed write w reg_addr="+reg_addr+" v"+v+"
	// th="+Thread.currentThread().getId()) ;
	// return r ;
	// }

}
