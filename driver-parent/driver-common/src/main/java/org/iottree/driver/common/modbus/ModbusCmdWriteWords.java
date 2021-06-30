package org.iottree.driver.common.modbus;

import java.io.InputStream;
import java.io.OutputStream;

public class ModbusCmdWriteWords extends ModbusCmd
{
	int regAddr = 0 ;
	//int regNum = 0 ;
	//long timeoutMs = 10 ;
	
	boolean[] ret_vals = new boolean[300] ;
	int ret_val_num = 0 ;
	
	int[] wVals = null;
	
	public ModbusCmdWriteWords(long scan_inter_ms,
			int s_addr,int reg_addr,int[] vals)
	{
		super(scan_inter_ms,s_addr) ;
		
		
		this.regAddr = reg_addr ;
		wVals = vals ;
		//this.regNum = reg_num ;
		//this.timeoutMs = timeoutms ;
	}
	
	public ModbusCmdWriteWords(
			int s_addr,int reg_addr,int[] vals)
	{
		this(-1,s_addr,reg_addr,vals) ;
	}
	
//	public ModbusCmdWriteWords(DevCtrlPtBindInfo.BindInfoModbus bim,int[] vals)
//	{
//		this(-1,bim.getDevAddr(),bim.getRegAddr(),vals) ;
//	}
	
	
	public void setWriteVal(int[] vs)
	{
		wVals = vs ;
	}
	
	
//	public boolean[] getRetVals()
//	{
//		boolean[] rets = new boolean[ret_val_num] ;
//		System.arraycopy(ret_vals, 0, rets,0,ret_val_num) ;
//		return rets ;
//	}

	protected int reqRespRTU(
			OutputStream ous,InputStream ins)
		throws Exception
	{
		ret_val_num = 0 ;
		
		int bcount = wVals.length*2;
		byte[] pdata = new byte[9+bcount] ;
		pdata[0] = (byte)slaveAddr ;
	    pdata[1] = MODBUS_FC_WRITE_MULTI_REG ;
	    pdata[2] = (byte) ((regAddr >> 8) & 0xFF) ;
	    pdata[3] = (byte) (regAddr & 0xFF) ;
	    pdata[4] = (byte) ((wVals.length>>8) &0xFF) ;
	    pdata[5] = (byte) (wVals.length & 0xFF) ;
	    pdata[6] = (byte) bcount ;
	    
	    for(int i=0;i<wVals.length;i++)
	    {
	    	pdata[7+(i*2)] = (byte) ((wVals[i]>>8) & 0xFF) ;
	    	pdata[7+(i*2)+1] = (byte) (wVals[i] & 0xFF) ;
	    }
	    
	    int crc = modbus_crc16_check(pdata,pdata.length-2);
	    pdata[pdata.length-2] = (byte)((crc>>8) & 0xFF) ;
	    pdata[pdata.length-1] = (byte)(crc & 0xFF) ;
	    
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
	        //判断返回内容是否接收完整
	        if(mayrlen>0)
	        {//只需要判断接收长度就行
	            if(rlen>=mayrlen)
	                break ;//接收结束
	        }
	        else
	        {//判断地址和长度
	            if(mbuss_adu[0]!=(byte)slaveAddr)
	            {//接收格式错误
	                break ;
	            }
	            if(rlen<3)
	                continue ;
	            
	            if(mbuss_adu[1]!=(byte)MODBUS_FC_WRITE_MULTI_REG)
	            {//功能码错误
	                if(mbuss_adu[1]==(byte)(MODBUS_FC_WRITE_MULTI_REG+0x80))
	                {//设备返回错误
	                    //*perrc = mbuss_adu[2] ; 
	                }
	                break ;
	            }
	            else
	            {//
	                mayrlen = 8;//返回字节长度信息，前面3字节+包含crc
	            }
	        }
	        
	        //////////////
	        
	    }
	    
	    if(mayrlen<=0 || rlen<mayrlen)
	    {//接收错误信息 or time out
	        com_stream_end() ;
	        if(rlen<=0)
	        	return ERR_RECV_TIMEOUT ;//recvTimeout may be adjust
	        if(rlen<mayrlen)
	        	return ERR_RECV_END_TIMEOUT ;//recvEndTimeout may be adjust
	        else
	        	return 0 ;//err
	    }
	    
	    /////
//	  处理接收的内容，地址和功能码
	    //crc验证
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
		lastTcpCC ++ ;
		if(lastTcpCC>=65535)
			lastTcpCC = 1 ;
		
	    int bcount = wVals.length*2;
		byte[] pdata = new byte[13+bcount] ;
		
		ret_val_num = 0 ;
		//mbap
		pdata[0] = (byte) ((lastTcpCC >> 8) & 0xFF) ;
		pdata[1] = (byte) (lastTcpCC & 0xFF) ;
		pdata[2] = pdata[3] = 0 ;
		pdata[4] = 0 ;//后续字节数高位
		pdata[5] = (byte)(7+bcount) ;//后续字节数低位
		//pdu

		pdata[6] = (byte)slaveAddr ;
	    pdata[7] = MODBUS_FC_WRITE_MULTI_REG ;
	    pdata[8] = (byte) ((regAddr >> 8) & 0xFF) ;
	    pdata[9] = (byte) (regAddr & 0xFF) ;
	    pdata[10] = (byte) ((wVals.length>>8) &0xFF) ;
	    pdata[11] = (byte) (wVals.length & 0xFF) ;
	    pdata[12] = (byte) bcount ;
	    
	    for(int i=0;i<wVals.length;i++)
	    {
	    	pdata[13+(i*2)] = (byte) ((wVals[i]>>8) & 0xFF) ;
	    	pdata[13+(i*2)+1] = (byte) (wVals[i] & 0xFF) ;
	    }
	    
	    clearInputStream(ins) ;
	    ous.write(pdata) ;
	    ous.flush() ;
	    //读取前6个字节
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
        {//功能码错误
            //if(mbuss_adu[1]==(byte)(fc+0x80))
	    	return 0 ;
        }
	    
	    ret_val_num = 0 ;
	    
	    return 1 ;
	}
}
