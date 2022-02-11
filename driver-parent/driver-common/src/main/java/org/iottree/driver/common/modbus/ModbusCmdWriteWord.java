package org.iottree.driver.common.modbus;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class ModbusCmdWriteWord extends ModbusCmd
{
	int regAddr = 0 ;
	//int regNum = 0 ;
	//long timeoutMs = 10 ;
	
	boolean[] ret_vals = new boolean[300] ;
	int ret_val_num = 0 ;
	
	int wVal = -1;
	
	short fc = MODBUS_FC_WRITE_SINGLE_REG ;
	
	public ModbusCmdWriteWord(int devid)
	{
		super(1000,devid) ;
	}
	
	public ModbusCmdWriteWord(long scan_inter_ms,
			int s_addr,int reg_addr,int val)
	{
		super(scan_inter_ms,s_addr) ;
		
		
		this.regAddr = reg_addr ;
		wVal = val ;
		//this.regNum = reg_num ;
		//this.timeoutMs = timeoutms ;
	}
	
	public ModbusCmdWriteWord(
			int s_addr,int reg_addr,int val)
	{
		this(-1,s_addr,reg_addr,val) ;
	}
	
	public int getRegAddr()
	{
		return regAddr ;
	}
	
	
	public int getWriteVal()
	{
		return wVal ;
	}
	
	
	public void setWriteVal(int v)
	{
		wVal = v ;
	}
	
	
//	public boolean[] getRetVals()
//	{
//		boolean[] rets = new boolean[ret_val_num] ;
//		System.arraycopy(ret_vals, 0, rets,0,ret_val_num) ;
//		return rets ;
//	}
	
	public int calRespLenRTU()
	{
		return -1 ;
	}

	public short getFC()
	{
		return MODBUS_FC_WRITE_SINGLE_REG ;
	}

	protected int reqRespRTU(
			OutputStream ous,InputStream ins)
		throws Exception
	{
		ret_val_num = 0 ;
		
		byte[] pdata = new byte[8] ;
		pdata[0] = (byte)slaveAddr ;
	    pdata[1] = MODBUS_FC_WRITE_SINGLE_REG ;
	    pdata[2] = (byte) ((regAddr >> 8) & 0xFF) ;
	    pdata[3] = (byte) (regAddr & 0xFF) ;
	    pdata[4] = (byte) ((wVal>>8) &0xFF) ;
	    pdata[5] = (byte) (wVal & 0xFF) ;
	    
	    int crc = modbus_crc16_check(pdata,6);
	    pdata[6] = (byte)((crc>>8) & 0xFF) ;
	    pdata[7] = (byte)(crc & 0xFF) ;
	    
	    clearInputStream(ins) ;
	    ous.write(pdata) ;
	    ous.flush() ;
	    
	    
	    
	    //////////////////////////////////////
	    //

	    int rlen=0,mayrlen=0,i ;

	    com_stream_recv_start(ins);
	    
	    while(com_stream_in_recving())
	    {
	        rlen = com_stream_recv_chk_len_timeout(ins) ;
	        if(rlen==0)
	            continue ;
	        
	        if(mayrlen>0)
	        {//ֻ
	            if(rlen>=mayrlen)
	                break ;
	        }
	        else
	        {//
	            if(mbuss_adu[0]!=(byte)slaveAddr)
	            {//
	                break ;
	            }
	            if(rlen<3)
	                continue ;
	            
	            if(mbuss_adu[1]!=(byte)MODBUS_FC_WRITE_SINGLE_REG)
	            {//
	                if(mbuss_adu[1]==(byte)(MODBUS_FC_WRITE_SINGLE_REG+0x80))
	                {//
	                    int perrc = 0xFF & (int)mbuss_adu[2] ; 
	                }
	                break ;
	            }
	            else
	            {//
	                mayrlen = 8;
	            }
	        }
	        
	        //////////////
	        
	    }
	    
	    if(mayrlen<=0 || rlen<mayrlen)
	    {
	        com_stream_end() ;
	        if(rlen<=0)
	        	return ERR_RECV_TIMEOUT ;//recvTimeout may be adjust
	        if(rlen<mayrlen)
	        	return ERR_RECV_END_TIMEOUT ;//recvEndTimeout may be adjust
	        else
	        	return 0 ;//err
	    }
	    
	    if(crc!=modbus_crc16_check(mbuss_adu,6))//mayrlen-2))
	    {
	        com_stream_end() ;
	        return ERR_CRC ;
	    }

	    com_stream_end() ;

	    return 1 ;
	}
	
	
	protected int reqRespTCP(OutputStream ous, InputStream ins) throws Exception
	{
		byte[] pdata = new byte[12] ;
		
		lastTcpCC ++ ;
		if(lastTcpCC>=65535)
			lastTcpCC = 1 ;
		
		ret_val_num = 0 ;
		//mbap
		pdata[0] = (byte) ((lastTcpCC >> 8) & 0xFF) ;
		pdata[1] = (byte) (lastTcpCC & 0xFF) ;
		pdata[2] = pdata[3] = 0 ;
		pdata[4] = 0 ;
		pdata[5] = 6 ;
		//pdu

		pdata[6] = (byte)slaveAddr ;
	    pdata[7] = MODBUS_FC_WRITE_SINGLE_REG ;
	    pdata[8] = (byte) ((regAddr >> 8) & 0xFF) ;
	    pdata[9] = (byte) (regAddr & 0xFF) ;
	    pdata[10] = (byte) ((wVal>>8) &0xFF) ;
	    pdata[11] = (byte) (wVal & 0xFF) ;

	    
	    clearInputStream(ins) ;
	    ous.write(pdata) ;
	    ous.flush() ;
	   
	    byte[] read_mbap = new byte[6];
	    do
	    {
	    	read_mbap[0] = (byte)ins.read() ;
	    }
	    while(pdata[0]!=read_mbap[0]);
	    int rlen = 1,r ;
	    while((r=ins.read(read_mbap, rlen, 6-rlen))>0)
	    {
	    	rlen += r ;
	    }
	    if(rlen!=6)
	    {
	    	return 0 ;
	    }
	    
	    int i;
	    for(i = 1 ; i < 4; i ++)
	    {
	    	if(pdata[i]!=read_mbap[i])
	    		return 0 ;//err
	    }
	    
	    int pdulen = ((int)read_mbap[4])&0xFF ;
	    pdulen <<= 8 ;
	    pdulen += ((int)read_mbap[5])&0xFF ;
	    if(pdulen>255)
	    	return 0 ;

	    byte[] recvpdu = new byte[pdulen] ;
	    rlen = 0 ;
	    
	    while((r=ins.read(recvpdu, rlen, pdulen-rlen))>0)
	    {
	    	rlen += r ;
	    }
	    if(rlen!=pdulen)
	    {
	    	return 0 ;//err
	    }
	    //read pdu ok
	    if(recvpdu[0]!=(byte)slaveAddr)
	    {
	    	return 0 ;//err
	    }
	    if(recvpdu[1]!=pdata[7])
        {
            //if(mbuss_adu[1]==(byte)(fc+0x80))
	    	return 0 ;
        }
	    
	    ret_val_num = 0 ;
	    
	    return 1 ;
	}
	

	public static byte[] createResp(short addr,short reg_addr,short vdata)
	{
		byte[] data = new byte[8] ;
		
		
		data[0] = (byte)addr ;
		data[1] = MODBUS_FC_WRITE_SINGLE_COIL;
		data[2] = (byte)(reg_addr>>8) ;
		data[3] = (byte)(reg_addr) ;
		data[4] = (byte)(vdata>>8) ;
		data[5] = (byte)vdata ;
		
		int crc = modbus_crc16_check(data,6);
	    data[6] = (byte)((crc>>8) & 0xFF) ;
	    data[7] = (byte)(crc & 0xFF) ;
		
		return data ;
	}
	

	static ModbusCmdWriteWord createReqMC(byte[] bs,int[] pl)
	{
		if(bs.length<8)
			return null ;
		
		int crc = modbus_crc16_check(bs,6);
		if(bs[6]!=(byte)((crc>>8) & 0xFF) ||
				bs[7]!=(byte)(crc & 0xFF))
		{
			return null ;
		}
		
	    
		short addr = (short)(bs[0] & 0xFF) ;
		short fc =  (short)(bs[1] & 0xFF) ;
		
		int reg_addr = (int)(0xFF & bs[2]) ;
		reg_addr <<= 8 ;
		reg_addr += (int)(0xFF & bs[3]) ;
		
		int val =  (int)(0xFF & bs[4]) ;
		val <<= 8 ;
		val += (int)(0xFF & bs[5]) ;
		if(bs.length>8)
			pl[0] = 8 ;
		else
			pl[0] = -1 ;
		
		return new ModbusCmdWriteWord(addr,reg_addr,val) ;
	}
	
}