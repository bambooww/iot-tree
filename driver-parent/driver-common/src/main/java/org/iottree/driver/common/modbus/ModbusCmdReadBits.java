package org.iottree.driver.common.modbus;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.util.HashMap;

import org.iottree.core.util.Convert;
import org.iottree.driver.common.modbus.ModbusParserResp.RespRet;
import org.iottree.driver.common.modbus.ModbusParserResp.RespRetReadBits;
import org.w3c.dom.Element;


public class ModbusCmdReadBits extends ModbusCmdRead
{
	
	int regAddr = 0 ;
	int regNum = 0 ;
	//long timeoutMs = 10 ;
	
	boolean[] ret_vals = null;
	boolean ret_val_ok = false ;
	
	Boolean[] last_vals = null ;
	
	short fc = MODBUS_FC_READ_COILS ;
	
	
	ModbusCmdReadBits(int dev_addr,short fc)
	{
		super(1000,dev_addr) ;
		switch(fc)
		{
		case MODBUS_FC_READ_COILS://rw
		case MODBUS_FC_READ_DISCRETE_INPUT:// readonly
			this.fc = fc ;
			break ;
		default:
			throw new IllegalArgumentException("invalid fc") ;
		}
	}
	
	public ModbusCmdReadBits(short fc,long scan_inter_ms,
			int dev_addr,int reg_addr,int reg_num)
	{
		super(scan_inter_ms,dev_addr) ;
		
		switch(fc)
		{
		case MODBUS_FC_READ_COILS://rw
		case MODBUS_FC_READ_DISCRETE_INPUT:// readonly
			this.fc = fc ;
			break ;
		default:
			throw new IllegalArgumentException("invalid fc") ;
		}
		
		setRegAddrNum(reg_addr,reg_num) ;
	}
	
	public ModbusCmdReadBits(short fc,
			int s_addr,int reg_addr,int reg_num)
	{
		this(fc,-1,s_addr,reg_addr,reg_num) ;
	}
	
	public short getFC()
	{
		return fc ;
	}
	
	
	static ModbusCmdReadBits createReqMC(byte[] bs,int[] pl)
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
		
		int reg_num =  (int)(0xFF & bs[4]) ;
		reg_num <<= 8 ;
		reg_num += (int)(0xFF & bs[5]) ;
		
		if(bs.length>8)
			pl[0] = 8 ;
		else
			pl[0] = -1 ;//end
		
		return new ModbusCmdReadBits(fc,-1,
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
	
	public static byte[] createResp(short addr,short req_fc,boolean[] bdata)
	{
		
		int dlen = bdata.length/8 + ((bdata.length%8)>0?1:0);
	
		int rlen = 5+dlen ;
		byte[] data = new byte[rlen] ;
		
		
		data[0] = (byte)addr ;
		data[1] = (byte)(req_fc) ;
		data[2] = (byte)dlen ;
		for(int i = 0 ; i < dlen ; i ++)
		{
			data[3+i] = 0 ;
		}
		
		for(int i=0;i<bdata.length;i++)
		{
			int k = 3+i/8 ;
			int bk = i%8 ;
			if(bdata[i])
				data[k] |= (byte)(1<<bk) ;
		}
		
		int crc = modbus_crc16_check(data,rlen-2);
	    data[rlen-2] = (byte)((crc>>8) & 0xFF) ;
	    data[rlen-1] = (byte)(crc & 0xFF) ;
		
		return data ;
	}
	
//	public ModbusCmdReadBits(
//			DevCtrlPtBindInfo bim)
//	{
//		this(bim.canWrite()?MODBUS_FC_READ_COILS:MODBUS_FC_READ_DISCRETE_INPUT,
//				-1,bim.getDevAddr(),bim.getRegAddr(),1) ;
//		
//		int[] rfrom = bim.getRFrom() ;
//		if(rfrom!=null)
//			setRegAddrNum(rfrom[0],rfrom[1]);
//	}
	
	
	
	private void setRegAddrNum(int reg_addr,int reg_num)
	{
		this.regAddr = reg_addr ;
		this.regNum = reg_num ;
		//this.timeoutMs = timeoutms ;
		ret_vals = new boolean[reg_num] ;
		last_vals = new Boolean[reg_num] ;
		//
		for(int i = 0 ; i < reg_num ; i ++)
			last_vals[i] = null ;
	}
	
	public int getRegAddr()
	{
		return regAddr ;
	}
	
	public int getRegNum()
	{
		return regNum ;
	}
	
	public boolean isReadCmd()
	{
		return true;
	}
	
	public boolean[] getRetVals()
	{
		if(!ret_val_ok)
			return null ;
		
		if(this.belongToRunner!=null)
		{
			if(!this.belongToRunner.isCmdRunning())
				return null ;
		}
		//
		
		return ret_vals;
	}
	
	
	public Object[] getReadVals()
	{
		boolean[] r = getRetVals();
		if(r==null)
			return null ;
		Object[] rs = new Object[r.length];
		for(int k=0;k<rs.length;k++)
			rs[k] = r[k] ;
		return rs ;
	}
	
	public int calRespLenRTU()
	{
		return 3+regNum/8+((regNum%8)>0?1:0)+2 ;
	}

	protected int reqRespRTU1(
			OutputStream ous,InputStream ins)
		throws Exception
	{
		byte[] pdata = new byte[8] ;
		pdata[0] = (byte)slaveAddr ;
		
	    pdata[1] = (byte)fc ;
	    pdata[2] = (byte) ((regAddr >> 8) & 0xFF) ;
	    pdata[3] = (byte) (regAddr & 0xFF) ;
	    pdata[4] = (byte) (regNum >> 8) ;
	    pdata[5] = (byte) (regNum & 0xFF) ;
	    
	    int crc = modbus_crc16_check(pdata,6);
	    pdata[6] = (byte)((crc>>8) & 0xFF) ;
	    pdata[7] = (byte)(crc & 0xFF) ;
	    //System.out.println("req>>"+Convert.byteArray2HexStr(pdata)) ;
	    clearInputStream(ins) ;
	    ous.write(pdata) ;
	    ous.flush() ;
	    
	    //////////////////////////////////////
	    //
	    
	    ModbusParserResp mp_resp = new ModbusParserResp() ;
	    mp_resp.initDevFC(slaveAddr, fc, regNum) ;
	    RespRet rr = mp_resp.parseRespCmdRTU(ins) ;
	    if(rr.isDiscard())
	    {
	    	return ERR_CRC ;
	    }
	    if(rr.isErrRet())
	    	return ERR_RET ;
	    
	    RespRetReadBits rrrb = (RespRetReadBits)rr ;
	    boolean[] bvs = rrrb.getReadVals() ;
	    HashMap<Integer,Object> addr2val = new HashMap<Integer,Object>() ;
	    
	    for(int i=0 ; i< regNum ; i ++)
	    {
	        boolean bv = bvs[i];
	        ret_vals[i] = bv ;
	        
	        if(!((Object)bv).equals(last_vals[i]))
	        	addr2val.put(regAddr+i,bv) ;
	        
	        last_vals[i] = bv ;
	    }
	    
	    //
	    
	    if(addr2val.size()>0 && 
	    		this.belongToRunner!=null &&
	    		this.belongToRunner.runLis!=null)
	    {
	    	this.belongToRunner.runLis.onModbusReadChanged(this, addr2val) ;
	    }
	    
	    ret_val_ok = true;
	    return regNum ;
	    
	}
	
	protected int reqRespRTU(
			OutputStream ous,InputStream ins)
		throws Exception
	{
		byte[] pdata = new byte[8] ;
		pdata[0] = (byte)slaveAddr ;
		
	    pdata[1] = (byte)fc ;
	    pdata[2] = (byte) ((regAddr >> 8) & 0xFF) ;
	    pdata[3] = (byte) (regAddr & 0xFF) ;
	    pdata[4] = (byte) (regNum >> 8) ;
	    pdata[5] = (byte) (regNum & 0xFF) ;
	    
	    int crc = modbus_crc16_check(pdata,6);
	    pdata[6] = (byte)((crc>>8) & 0xFF) ;
	    pdata[7] = (byte)(crc & 0xFF) ;
	    //System.out.println("req>>"+Convert.byteArray2HexStr(pdata)) ;
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
	        //
	        if(mayrlen>0)
	        {//ֻ
	            if(rlen>=mayrlen)
	                break ;//
	        }
	        else
	        {
	            if(mbuss_adu[0]!=(byte)slaveAddr)
	            {//
	                break ;
	            }
	            if(rlen<3)
	                continue ;
	            
	            if(mbuss_adu[1]!=(byte)fc)
	            {//
	                if(mbuss_adu[1]==(byte)(fc+0x80))
	                {//
	                    //*perrc = mbuss_adu[2] ;
	                }
	                break ;
	            }
	            else
	            {//
	                mayrlen = (((int)mbuss_adu[2]) & 0xFF)+5;//
	            }
	        }
	    }
	    
	    
	    if(mayrlen<=0 || rlen<mayrlen)
	    {//
	        com_stream_end() ;
	        ret_val_ok = false;
	        
	        if(rlen<=0)
	        {
	        	//System.out.println("   modbuscmd read bits == recvTimeout="+recvTimeout+" fix="+bFixTO);
	        	return ERR_RECV_TIMEOUT ;//recvTimeout may be adjust
	        }
	        if(rlen<mayrlen)
	        	return ERR_RECV_END_TIMEOUT ;//recvEndTimeout may be adjust
	        else
	        	return 0 ;//err
	    }
	    
	    crc = modbus_crc16_check(mbuss_adu,mayrlen-2);
	    if((((byte)(crc>>8)) != (byte)mbuss_adu[mayrlen-2] || ((byte)(crc & 0xFF)) != mbuss_adu[mayrlen-1]))
	    {
	        com_stream_end() ;
	        return ERR_CRC ;
	    }
	    
	    HashMap<Integer,Object> addr2val = new HashMap<Integer,Object>() ;
	    
	    for(i=0 ; i< regNum ; i ++)
	    {
	        int tmpv = ((int)mbuss_adu[3+i/8]) & 0xFF ;
	        
	        boolean bv = (tmpv & (1<<(i%8)))>0?true:false ;
	        ret_vals[i] = bv ;
	        
	        if(!((Object)bv).equals(last_vals[i]))
	        	addr2val.put(regAddr+i,bv) ;
	        
	        last_vals[i] = bv ;
	    }
	    
	    //
	    
	    if(addr2val.size()>0 && 
	    		this.belongToRunner!=null &&
	    		this.belongToRunner.runLis!=null)
	    {
	    	this.belongToRunner.runLis.onModbusReadChanged(this, addr2val) ;
	    }
	    
	    com_stream_end() ;
	    
	    ret_val_ok = true;
	    return regNum ;
	    
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
		pdata[4] = 0 ;//length hi
		pdata[5] = 6 ;//length lo
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
	    
	    //System.out.println("hex="+Convert.byteArray2HexStr(pdata)) ;
	    clearInputStream(ins) ;
	    ous.write(pdata) ;
	    ous.flush() ;
	    //
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
	    	ret_val_ok = false ;
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
	    	ret_val_ok = false ;
	    	return 0 ;//err
	    }
	    //read pdu ok
	    if(recvpdu[0]!=(byte)slaveAddr)
	    {
	    	ret_val_ok = false ;
	    	return 0 ;//err
	    }
	    if(recvpdu[1]!=(byte)fc)
        {//reponse error
            //if(mbuss_adu[1]==(byte)(fc+0x80))
	    	ret_val_ok = false ;
	    	return 0 ;
        }
	    
	    //
	    HashMap<Integer,Object> addr2val = new HashMap<Integer,Object>() ;
	    
	    for(i=0 ; i< regNum ; i ++)
	    {
	        int tmpv = ((int)recvpdu[3+i/8]) & 0xFF ;
	        
	        boolean bv = (tmpv & (1<<(i%8)))>0?true:false ;
	        ret_vals[i] = bv ;
	        
	        if(!((Object)bv).equals(last_vals[i]))
	        	addr2val.put(regAddr+i,bv) ;
	        
	        last_vals[i] = bv ;
	    }
	    
	    //
	    
	    if(addr2val.size()>0 && 
	    		this.belongToRunner!=null &&
	    		this.belongToRunner.runLis!=null)
	    {
	    	this.belongToRunner.runLis.onModbusReadChanged(this, addr2val) ;
	    }
	    
	    
	    ret_val_ok = true;

	    return regNum ;
	}
	
	public String toString()
	{
		return super.toString()+"| bit dev_addr="+this.getDevAddr()+" reg_addr="+regAddr+" reg_num="+regNum ;
	}
}
