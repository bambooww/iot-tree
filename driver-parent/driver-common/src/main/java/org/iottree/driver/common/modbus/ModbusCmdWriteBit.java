package org.iottree.driver.common.modbus;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;


public class ModbusCmdWriteBit extends ModbusCmd
{
	int regAddr = 0 ;
	//int regNum = 0 ;
	//long timeoutMs = 10 ;
	
	boolean[] ret_vals = new boolean[300] ;
	int ret_val_num = 0 ;
	
	boolean bwVal = false;
	
	public ModbusCmdWriteBit(long scan_inter_ms,
			int s_addr,int reg_addr,boolean bval)
	{
		super(scan_inter_ms,s_addr) ;
		
		
		this.regAddr = reg_addr ;
		bwVal = bval ;
		//this.regNum = reg_num ;
		//this.timeoutMs = timeoutms ;
	}
	
	public ModbusCmdWriteBit(
			int s_addr,int reg_addr,boolean bval)
	{
		this(-1,s_addr,reg_addr,bval) ;
	}
	

//	public ModbusCmdWriteBit(DevCtrlPtBindInfo.BindInfoModbus bim,boolean bval)
//	{
//		this(-1,bim.getDevAddr(),bim.getRegAddr(),bval) ;
//	}
	
	
	public void setWriteVal(boolean b)
	{
		bwVal = b ;
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
		
		byte[] pdata = new byte[8] ;
		pdata[0] = (byte)slaveAddr ;
	    pdata[1] = MODBUS_FC_WRITE_SINGLE_COIL ;
	    pdata[2] = (byte) ((regAddr >> 8) & 0xFF) ;
	    pdata[3] = (byte) (regAddr & 0xFF) ;
	    if(bwVal)
	    	pdata[4] = (byte) 0xFF ;
	    else
	    	pdata[4] = (byte) 0x0 ;
	    pdata[5] = 0 ;
	    
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
	        //�жϷ��������Ƿ��������
	        if(mayrlen>0)
	        {//ֻ��Ҫ�жϽ��ճ��Ⱦ���
	            if(rlen>=mayrlen)
	                break ;//���ս���
	        }
	        else
	        {//�жϵ�ַ�ͳ���
	            if(mbuss_adu[0]!=(byte)slaveAddr)
	            {//���ո�ʽ����
	                break ;
	            }
	            if(rlen<3)
	                continue ;
	            
	            if(mbuss_adu[1]!=(byte)MODBUS_FC_WRITE_SINGLE_COIL)
	            {//���������
	                if(mbuss_adu[1]==(byte)(MODBUS_FC_WRITE_SINGLE_COIL+0x80))
	                {//�豸���ش���
	                    //*perrc = mbuss_adu[2] ; 
	                }
	                break ;
	            }
	            else
	            {//
	                mayrlen = 8;//�����ֽڳ�����Ϣ��ǰ��3�ֽ�+����crc
	            }
	        }
	        
	        //////////////
	        
	    }
	    
	    if(mayrlen<=0 || rlen<mayrlen)
	    {//���մ�����Ϣ or time out
	        com_stream_end() ;
	        if(rlen<=0)
	        	return ERR_RECV_TIMEOUT ;//recvTimeout may be adjust
	        if(rlen < mayrlen)
	        	return ERR_RECV_END_TIMEOUT ;//recvEndTimeout may be adjust
	        else
	        	return 0 ;//err
	    }
	    
	    /////
//	  ������յ����ݣ���ַ�͹�����
	    //crc��֤
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
		//mbap
		pdata[0] = (byte) ((lastTcpCC >> 8) & 0xFF) ;
		pdata[1] = (byte) (lastTcpCC & 0xFF) ;
		pdata[2] = pdata[3] = 0 ;
		pdata[4] = 0 ;//�����ֽ�����λ
		pdata[5] = 6 ;//�����ֽ�����λ
		//pdu

	    ret_val_num = 0 ;

		pdata[6] = (byte)slaveAddr ;
	    pdata[7] = MODBUS_FC_WRITE_SINGLE_COIL ;
	    pdata[8] = (byte) ((regAddr >> 8) & 0xFF) ;
	    pdata[9] = (byte) (regAddr & 0xFF) ;
	    if(bwVal)
	    	pdata[10] = (byte) 0xFF ;
	    else
	    	pdata[10] = (byte) 0x0 ;
	    pdata[11] = 0 ;
	    
	    //int crc = modbus_crc16_check(pdata,6);
	    //pdata[6] = (byte)((crc>>8) & 0xFF) ;
	    //pdata[7] = (byte)(crc & 0xFF) ;
	    
	    clearInputStream(ins) ;
	    ous.write(pdata) ;
	    ous.flush() ;
	    //��ȡǰ6���ֽ�
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
        {//���������
            //if(mbuss_adu[1]==(byte)(fc+0x80))
	    	return 0 ;
        }
	    
	    ret_val_num = 1 ;
	    
	    return 1 ;
	}
	
}
