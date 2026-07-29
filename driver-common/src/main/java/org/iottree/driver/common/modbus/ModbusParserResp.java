package org.iottree.driver.common.modbus;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.List;

public class ModbusParserResp extends ModbusParser
{
	public static class RespRet
	{
		/**
		 * -1 discard 0 - err 1 ok
		 */
		int retST = 0 ;
		
		int errCode = -1 ;
		
		public RespRet(int retst)
		{
			this.retST = retst ;
		}
		
		public int getReturnST()
		{
			return retST ;
		}
		
		public boolean isDiscard()
		{
			return retST<0 ;
		}
		
		public boolean isErrRet()
		{
			return retST ==0 ;
		}
		
		public boolean isSuccRet()
		{
			return retST>0 ;
		}
		
		public int getErrCode()
		{
			return errCode ;
		}
	}
	
	private static RespRet createErrRet(int errcode)
	{
		RespRet r = new RespRet(0) ;
		r.errCode = errcode ;
		return r ;
	}
	
	private static final RespRet ERR = new RespRet(0) ;
	
	private static final RespRet DISCARD = new RespRet(-1) ;
	
	public static class RespRetReadBits extends RespRet
	{
		boolean[] readVals = null ;
		
		public RespRetReadBits(int ret_st,boolean[] readvals)
		{
			super(ret_st) ;
			this.readVals = readvals ;
		}
		
		public boolean[] getReadVals()
		{
			return readVals ;
		}
	}
	
	public static class RespRetReadInt16s extends RespRet
	{
		int[] readVals = null ;
		
		public RespRetReadInt16s(int ret_st,int[] readvals)
		{
			super(ret_st) ;
			this.readVals = readvals ;
		}
		
		public int[] getReadVals()
		{
			return readVals ;
		}
	}
	
	//private int pST = 0 ;

	private int devId = -1 ;
	
	private short fc = -1 ;
	
	private int readNum = -1 ;
	
	//private ModbusCmd retMC = null ; 
	
	public ModbusParserResp()
	{
		
	}
	
	public ModbusParserResp initDevFC(int devid,short fc,int readnum)
	{
		this.devId = devid ;
		this.fc = fc ;
		//pST = 0 ;
		this.readNum = readnum ;
		return this ;
	}
	
	
	/**
	 * 
	 * @param inputs
	 * @return  -1 will discard all  0 - known 1 - succ
	 * @throws IOException
	 */
	public RespRet parseRespCmdRTU(InputStream inputs) throws IOException
	{
		int c ;
		//git devid
		c = inputs.read();
		if(c!=this.devId)//all data will be discard
			return DISCARD ;
		c = inputs.read() ;
		if(this.fc==c)
		{
			return parseRespFC(inputs) ;
		}
		else if(c==this.fc+0x80)
		{
			c = inputs.read() ;
			return createErrRet(c) ;
		}
		else
		{//discard all
			return DISCARD ;
		}
	}
	
//	public ModbusCmd getReturnCmd()
//	{
//		return this.retMC ;
//	}
	
	
	private RespRet parseRespFC(InputStream inputs) throws IOException
	{
		//int r = 0 ;
		switch(this.fc)
		{
		case ModbusCmd.MODBUS_FC_READ_COILS: //1
		case ModbusCmd.MODBUS_FC_READ_DISCRETE_INPUT:// = 0x02;
			return parseRespReadBits(inputs);
		case ModbusCmd.MODBUS_FC_READ_HOLD_REG:// = 0x03;
		case ModbusCmd.MODBUS_FC_READ_INPUT_REG:
			return parseRespReadInt16s(inputs) ;
		default:
			//System.out.println("fc=="+fc) ;
			return DISCARD ;
		}
	}
	
	private RespRet parseRespReadBits(InputStream inputs) throws IOException
	{
//		int dlen = bdata.length/8 + ((bdata.length%8)>0?1:0);
//		
//		int rlen = 5+dlen ;
//		byte[] data = new byte[rlen] ;
//		
//		
//		data[0] = (byte)addr ;
//		data[1] = (byte)(req_fc) ;
//		data[2] = (byte)dlen ;
//		for(int i = 0 ; i < dlen ; i ++)
//		{
//			data[3+i] = 0 ;
//		}
//		
//		for(int i=0;i<bdata.length;i++)
//		{
//			int k = 3+i/8 ;
//			int bk = i%8 ;
//			if(bdata[i])
//				data[k] |= (byte)(1<<bk) ;
//		}
//		
//		int crc = modbus_crc16_check(data,rlen-2);
//	    data[rlen-2] = (byte)((crc>>8) & 0xFF) ;
//	    data[rlen-1] = (byte)(crc & 0xFF) ;
		    
			int byte_c = inputs.read() ;
			if(byte_c*8<readNum)
				return DISCARD ;
			int len = 3+byte_c+2 ;
			byte[] bs = new byte[len] ;
			
			readFill(inputs,bs,3,byte_c+2) ;
			
			bs[0] = (byte)this.devId ;
			bs[1] = (byte)this.fc ;
			bs[2] = (byte)byte_c ;
			int crc = ModbusCmd.modbus_crc16_check(bs,len-2);
		    if(bs[len-2] != (byte)(((crc>>8)) & 0xFF) || bs[len-1] != ((byte)(crc & 0xFF)))
		    {
		    	return DISCARD ;
		    }
		    
		   boolean[] bvs = new boolean[readNum] ;
		   for(int i = 0 ; i < readNum ; i ++)
		   {
			   int b = i/8 ;
			   int bit = i%8 ;
			   bvs[i] = (((bs[3+b]) & 0xFF) & (1<<bit))>0 ;
		   }
		    
		   return new RespRetReadBits(1,bvs) ;
	}
	
	private RespRet parseRespReadInt16s(InputStream inputs) throws IOException
	{
//		if(wdata==null||wdata.length>125)
//			return null ;
//		
//		int dlen = wdata.length*2;//bdata.length/8 + ((bdata.length%8)>0?1:0);
//	
//		int rlen = 5+dlen ;
//		byte[] data = new byte[rlen] ;
//		
//		
//		data[0] = (byte)addr ;
//		data[1] = (byte)(req_fc) ;
//		data[2] = (byte)dlen ;
//		
//		
//		for(int i=0;i<wdata.length;i++)
//		{
//			short w = wdata[i] ;
//			data[3+i*2] = (byte)(0xFF & (w>>8)) ;
//			data[3+i*2+1] = (byte)(0xFF & w) ;
//		}
//		
//		int crc = modbus_crc16_check(data,rlen-2);
//	    data[rlen-2] = (byte)((crc>>8) & 0xFF) ;
//	    data[rlen-1] = (byte)(crc & 0xFF) ;
		    
			int byte_c = inputs.read() ;
			if(byte_c/2!=readNum)
				return DISCARD ;
			int len = 3+byte_c+2 ;
			byte[] bs = new byte[len] ;
			
			readFill(inputs,bs,3,byte_c+2) ;
			
			bs[0] = (byte)this.devId ;
			bs[1] = (byte)this.fc ;
			bs[2] = (byte)byte_c ;
			int crc = ModbusCmd.modbus_crc16_check(bs,len-2);
		    if(bs[len-2] != (byte)(((crc>>8)) & 0xFF) || bs[len-1] != ((byte)(crc & 0xFF)))
		    {
		    	return DISCARD ;
		    }
		    
		   int[] bvs = new int[readNum] ;
		   for(int i = 0 ; i < readNum ; i ++)
		   {
			   int w = (bs[3+i*2] & 0xff) ;
			   w <<= 8 ;
			   w += (bs[3+i*2+1] & 0xff) ;
//				data[3+i*2] = (byte)(0xFF & (w>>8)) ;
//				data[3+i*2+1] = (byte)(0xFF & w) ;
			   bvs[i] = w ;
		   }
		    
		   return new RespRetReadInt16s(1,bvs) ;
	}
}
