package org.iottree.driver.common.modbus;

import java.io.*;
import java.util.*;


import org.w3c.dom.Element;

public class ModbusCmdReadWords extends ModbusCmd
{
	static ModbusCmdReadWords createReqMC(byte[] bs,int[] pl)
	{
//		byte[] pdata = new byte[8] ;
//		pdata[0] = (byte) slaveAddr ;
//	    pdata[1] = (byte) fc ;
//	    pdata[2] = (byte) ((regAddr >> 8) & 0xFF) ;
//	    pdata[3] = (byte) (regAddr & 0xFF) ;
//	    pdata[4] = (byte) (regNum >> 8) ;
//	    pdata[5] = (byte) (regNum & 0xFF) ;
//	    
//	    int crc = modbus_crc16_check(pdata,6);
//	    pdata[6] = (byte)((crc>>8) & 0xFF) ;
//	    pdata[7] = (byte)(crc & 0xFF) ;
	    
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
		
		int reg_num =  (int)(0xFF & bs[4]) ;
		reg_num <<= 8 ;
		reg_num += (int)(0xFF & bs[5]) ;
		
		if(bs.length>8)
			pl[0] = 8 ;
		else
			pl[0] = -1 ;
		
		return new ModbusCmdReadWords(fc,-1,
				addr,reg_addr,reg_num) ;
	}
	
	/**
	 * ���������fc����һ��������Ϣ��������
	 * @param req_fc
	 * @param err_code 1(fc not support),2(reg addr out),3(out num err),4(read err)
	 * @return
	 */
	public static int createRespErr(byte[] data,
			short addr,short req_fc,short err_code)
	{
		//byte[] r = new byte[5] ;
		data[0] = (byte)addr ;
		data[1] = (byte)(req_fc+0x80) ;
		data[2] = (byte)err_code ;
		return 3 ;
	}
	
	public static byte[] createResp(short addr,short req_fc,short[] wdata)
	{
		if(wdata==null||wdata.length>125)
			return null ;
		
		int dlen = wdata.length*2;//bdata.length/8 + ((bdata.length%8)>0?1:0);
	
		int rlen = 5+dlen ;
		byte[] data = new byte[rlen] ;
		
		
		data[0] = (byte)addr ;
		data[1] = (byte)(req_fc) ;
		data[2] = (byte)dlen ;
		
		
		for(int i=0;i<wdata.length;i++)
		{
			short w = wdata[i] ;
			data[3+i*2] = (byte)(0xFF & (w>>8)) ;
			data[3+i*2+1] = (byte)(0xFF & w) ;
		}
		
		int crc = modbus_crc16_check(data,rlen-2);
	    data[rlen-2] = (byte)((crc>>8) & 0xFF) ;
	    data[rlen-1] = (byte)(crc & 0xFF) ;
		
		return data ;
	}
	
	
	
	int regAddr = 0 ;
	int regNum = 0 ;
	//long timeoutMs = 10 ;
	
	int[] up_vals = null;
	boolean up_val_ok = false ;
	
	Integer[] last_vals = null ;
	
	short fc = -1 ;
	
	public ModbusCmdReadWords(short fc,long scan_inter_ms,
			int s_addr,int reg_addr,int reg_num)
	{
		super(scan_inter_ms,s_addr) ;
		
		switch(fc)
		{
		case MODBUS_FC_READ_HOLD_REG://rw
		case MODBUS_FC_READ_INPUT_REG:// readonly
			this.fc = fc ;
			break ;
		default:
			throw new IllegalArgumentException("invalid fc") ;
		}
		
		//this.slaveAddr = (short)(s_addr & 0xFF) ;
		this.regAddr = reg_addr ;
		this.regNum = reg_num ;
		//this.timeoutMs = timeoutms ;
		
		up_vals = new int[reg_num] ;
		last_vals = new Integer[reg_num] ;
	}
	
	public ModbusCmdReadWords(short fc,
			int s_addr,int reg_addr,int reg_num)
	{
		this(fc,-1,s_addr,reg_addr,reg_num) ;
	}
	
	public short getFC()
	{
		return fc ;
	}
	
	public int getRegAddr()
	{
		return regAddr ;
	}
	
	public int getRegNum()
	{
		return regNum ;
	}
//	public ModbusCmdReadWords(DevCtrlPtBindInfo.BindInfoModbus bim)
//	{
//		this(bim.canWrite()?MODBUS_FC_READ_HOLD_REG:MODBUS_FC_READ_INPUT_REG,
//				-1,bim.getDevAddr(),bim.getRegAddr(),1) ;
//		
//		int[] rfrom = bim.getRFrom() ;
//		if(rfrom!=null)
//			setRegAddrNum(rfrom[0],rfrom[1]);
//	}
	
	
	public static ModbusCmdReadWords createByElement(Element ele)
	{
		boolean b_w = "w".equalsIgnoreCase(ele.getAttribute("w")) ;
		int dev_addr = Integer.parseInt(ele.getAttribute("dev")) ;
		int reg_addr = Integer.parseInt(ele.getAttribute("reg")) ;
		int reg_n = Integer.parseInt(ele.getAttribute("reg_num")) ;
		
		return new ModbusCmdReadWords(b_w?MODBUS_FC_READ_COILS:MODBUS_FC_READ_DISCRETE_INPUT,
				-1,dev_addr,reg_addr,reg_n) ;
	}
	
	private void setRegAddrNum(int reg_addr,int reg_num)
	{
		this.regAddr = reg_addr ;
		this.regNum = reg_num ;
		//this.timeoutMs = timeoutms ;
		
		this.regAddr = reg_addr ;
		this.regNum = reg_num ;
		//this.timeoutMs = timeoutms ;
		
		up_vals = new int[reg_num] ;
		last_vals = new Integer[reg_num] ;
	}
	
	public boolean isReadCmd()
	{
		return true;
	}
	
	public int[] getRetVals()
	{
		if(!up_val_ok)
			return null;
		
		if(this.belongToRunner!=null)
		{
			if(!this.belongToRunner.isCmdRunning())
				return null ;
		}
		return up_vals ;
	}
	
	public Object[] getReadVals()
	{
		int[] r = getRetVals();
		if(r==null)
			return null ;
		Object[] rs = new Object[r.length];
		for(int k=0;k<rs.length;k++)
			rs[k] = r[k] ;
		return rs ;
	}

	protected int reqRespRTU(
			OutputStream ous,InputStream ins)
		throws Exception
	{
		
		byte[] pdata = new byte[8] ;
		pdata[0] = (byte) slaveAddr ;
	    pdata[1] = (byte) fc ;
	    pdata[2] = (byte) ((regAddr >> 8) & 0xFF) ;
	    pdata[3] = (byte) (regAddr & 0xFF) ;
	    pdata[4] = (byte) (regNum >> 8) ;
	    pdata[5] = (byte) (regNum & 0xFF) ;
	    
	    int crc = modbus_crc16_check(pdata,6);
	    pdata[6] = (byte)((crc>>8) & 0xFF) ;
	    pdata[7] = (byte)(crc & 0xFF) ;
	    
	    clearInputStream(ins) ;
	    //System.out.println("write len="+pdata.length) ;
	    //byte[] tmpbs = Convert.hexStr2ByteArray("01010300000002C40B") ;
	    ous.write(pdata) ;
	    ous.flush() ;
	    //Thread.sleep(10);
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
	            
	            
	            
	            if(mbuss_adu[1]!=(byte)fc)
	            {//���������
	                if(mbuss_adu[1]==(byte)(fc+0x80))
	                {//�豸���ش���
	                    //*perrc = mbuss_adu[2] ; 
	                }
	                break ;
	            }
	            else
	            {//
	                mayrlen = (((int)mbuss_adu[2]) & 0xFF)+5;//�����ֽڳ�����Ϣ��ǰ��3�ֽ�+����crc
	            }
	        }
	    }
	    
	    

	    if(mayrlen<=0 || rlen<mayrlen)
	    {//���մ�����Ϣ or time out
	        com_stream_end() ;
	        up_val_ok = false ;
	        if(rlen<=0)
	        	return ERR_RECV_TIMEOUT ;//recvTimeout may be adjust
	        if(rlen<mayrlen)
	        	return ERR_RECV_END_TIMEOUT ;//recvEndTimeout may be adjust
	        else
	        	return 0 ;//err
	    }
	    
	    
	    //������յ����ݣ���ַ�͹�����
	    //crc��֤
	    crc = modbus_crc16_check(mbuss_adu,mayrlen-2);
	    if((((byte)(crc>>8)) != (byte)mbuss_adu[mayrlen-2] || ((byte)(crc & 0xFF)) != mbuss_adu[mayrlen-1]))
	    {
	        com_stream_end() ;
	        return ERR_CRC ;//��֤ʧ��
	    }
	    
	    
	    HashMap<Integer,Object> addr2val = new HashMap<Integer,Object>() ;
	    
	    //pdata��4���ֽڿ�ʼ����mayrlen-3֮ǰ���������ؿ��������
	    for(i=0 ; i< regNum ; i ++)
	    {
	        int tmpv = ((int)mbuss_adu[3+i*2]) & 0xFF ;
	        tmpv <<= 8 ;
	        tmpv += ((int)mbuss_adu[3+i*2+1]) & 0xFF ;
	        up_vals[i] = tmpv;
	        
	        if(!((Object)tmpv).equals(last_vals[i]))
	        	addr2val.put(regAddr+i,tmpv) ;
	        
	        last_vals[i] = tmpv ;
	    }
	    
	    if(addr2val.size()>0 && 
	    		this.belongToRunner!=null &&
	    		this.belongToRunner.runLis!=null)
	    {
	    	this.belongToRunner.runLis.onModbusReadChanged(this, addr2val) ;
	    }
	    
	    com_stream_end() ;
	    up_val_ok = true ;
	    
//	    for(int k=0;k<regNum;k++)
//	    {
//	    	System.out.print(" "+ret_vals[k]) ;
//	    }
//	    System.out.println("") ;
	    return regNum ;
	}
	
	
	
	//private transient byte[] lastReqPk = new byte[12];
	
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
		pdata[6] = (byte) slaveAddr ;
	    pdata[7] = (byte) fc ;
	    pdata[8] = (byte) ((regAddr >> 8) & 0xFF) ;
	    pdata[9] = (byte) (regAddr & 0xFF) ;
	    pdata[10] = (byte) (regNum >> 8) ;
	    pdata[11] = (byte) (regNum & 0xFF) ;
	    
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
	    	up_val_ok = false ;
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
	    	up_val_ok = false ;
	    	return 0 ;//err
	    }
	    //read pdu ok
	    if(recvpdu[0]!=(byte)slaveAddr)
	    {
	    	up_val_ok = false ;
	    	return 0 ;//err
	    }
	    if(recvpdu[1]!=(byte)fc)
        {//���������
            //if(mbuss_adu[1]==(byte)(fc+0x80))
	    	up_val_ok = false ;
	    	return 0 ;
        }
	    
	    //recvpdu[2]+3==pdulen

	    HashMap<Integer,Object> addr2val = new HashMap<Integer,Object>() ;
	    
	    //pdata��4���ֽڿ�ʼ����mayrlen-3֮ǰ���������ؿ��������
	    for(i=0 ; i< regNum ; i ++)
	    {
	        int tmpv = ((int)recvpdu[3+i*2]) & 0xFF ;
	        tmpv <<= 8 ;
	        tmpv += ((int)recvpdu[3+i*2+1]) & 0xFF ;
	        up_vals[i] = tmpv;
	        
	        if(!((Object)tmpv).equals(last_vals[i]))
	        	addr2val.put(regAddr+i,tmpv) ;
	        
	        last_vals[i] = tmpv ;
	    }
	    
	    if(addr2val.size()>0 && 
	    		this.belongToRunner!=null &&
	    		this.belongToRunner.runLis!=null)
	    {
	    	this.belongToRunner.runLis.onModbusReadChanged(this, addr2val) ;
	    }
	    
	    up_val_ok = true ;
	    
	    return regNum ;
	}

	public String toString()
	{
		return super.toString()+"| word dev_addr="+this.getDevAddr()+" reg_addr="+regAddr+" reg_num="+regNum ;
	}
}

	
