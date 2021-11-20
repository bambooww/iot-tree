package org.iottree.driver.common.modbus;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 
 * @author Jason Zhu
 *
 */
public class ModbusCmdEx extends  ModbusCmd
{
	byte[] reqData = null ;
	
	byte[] respData = null ;
	
	public ModbusCmdEx(int dev_addr,byte[] reqdata)
	{
		super(-1,dev_addr) ;
		
		reqData = reqdata ;
	}
	
	public short getFC()
	{
		return -1 ;
	}
	
	public byte[] getRespData()
	{
		return respData ;
	}
	
	public int calRespLenRTU()
	{
		return -1 ;
	}
	
	@Override
	protected int reqRespRTU(OutputStream ous, InputStream ins) throws Exception
	{
		byte[] pdata = new byte[reqData.length+1] ;
		pdata[0] = (byte)slaveAddr ;
		
		System.arraycopy(reqData,0,pdata,1,reqData.length) ;
		
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
	        
	        
	    }
	    
	    respData = new byte[rlen] ;
	    System.arraycopy(mbuss_adu, 0, respData, 0, rlen) ;
	    
	    com_stream_end() ;
	    
	    return rlen ;
	    
	}

	@Override
	protected int reqRespTCP(OutputStream ous, InputStream ins) throws Exception
	{
		
		return 0;
	}

}
