package org.iottree.driver.omron.fins.eth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.iottree.core.util.xmldata.DataUtil;
import org.iottree.driver.omron.fins.FinsMsg;

public abstract class FinsEthMsg extends FinsMsg
{
	private static final byte[] HEADER = new byte[] {'F','I','N','S'} ;
	
	/**
	 * target client last ip value
	 * @param outputs
	 * @throws IOException
	 */
	short tarClientId = -1 ;
	
	/**
	 * source client last ip value
	 */
	short sorClientId = -1 ;
	
	public FinsEthMsg(short tar_clientid,short sor_clientid)
	{
		this.tarClientId = tar_clientid ;
		this.sorClientId = sor_clientid ;
	}
	
	public void writeOut(OutputStream outputs) throws IOException
	{
		outputs.write(HEADER);
		
		int len = 20 + this.getParamBytesNum() ;
		byte[] bs = int2bytes(len) ;
		outputs.write(bs);
		
		bs = int2bytes(2) ; //command
		outputs.write(bs);
		bs[0]=bs[1]=bs[2]=bs[3] = 0 ;// error code
		outputs.write(bs);
		
		bs = new byte[12] ;
		
		bs[0] = (byte)getICF() ; //ICF
		bs[1] = 0; //RSV
		bs[2] = 2; //GCT
		bs[3] = 0; //DNA
		bs[4] = (byte)tarClientId; //DA1  target client_last_ip  PLC/PC
		bs[5] = 0 ; //DA2
		bs[6] = 0 ;//SNA
		bs[7] = (byte)sorClientId; //SA1 source client last ip  PLC/PC
		bs[8] = 0; //SA2
		bs[9] = 0; //SID
		bs[10] = (byte)getMRC();  // MRC  main req code
		bs[11] = (byte)getSRC();  //SRC  second req code
		
		outputs.write(bs);
		
		this.writeParam(outputs);
	}
	
	/**
	 * get main request code
	 * @return
	 */
	protected abstract short getMRC() ;
	
	/**
	 * get second request code
	 * @return
	 */
	protected abstract short getSRC() ;
	
//	protected abstract boolean isNeedResp() ;
//	/**
//	 * true=request  false=response
//	 * @return
//	 */
//	protected abstract boolean isReqOrResp() ;
	//public abstract int getCommand() ;
	
	/**
	 * int tmpi = isNeedResp()?0:1 ;
		if(!isReqOrResp())
			tmpi |= 0x80 ;
	 * @return
	 */
	protected abstract short getICF() ;
	
	protected abstract int getParamBytesNum() ;
	
	protected abstract void writeParam(OutputStream outputs) throws IOException ;
	
	// -- util func
	
	protected static final byte[] int2bytes(int i)
	{
		return DataUtil.intToBytes(i) ;
	}
	
	protected static final byte[] short2bytes(short i)
	{
		return DataUtil.shortToBytes(i) ;
	}
	
	protected static final void short2bytes(short i,byte[] bs,int offset)
	{
		DataUtil.shortToBytes(i,bs,offset) ;
	}
	
	//----  Handshake support
	
	public static byte[] Handshake_createReq(short client_pc_last_ip)
	{
		byte[] rets = new byte[20] ;
		rets[0]='F' ;
		rets[1]='I' ;
		rets[2]='N' ;
		rets[3]='S' ;
		for(int i = 4; i< 20 ; i ++)
			rets[i]=0 ;
		rets[7]=0x0C;
		rets[19] = (byte)client_pc_last_ip ;
		return rets ;
	}
	
	/**
	 * failed may time out IOException.or return null ;
	 */
	public static Short Handshake_checkResp(InputStream inputs,short client_pc_last_ip,long timeout) throws IOException
	{
		FinsMsg.checkStreamLenTimeout(inputs, 24, timeout);
		
		byte[] bs = new byte[24] ;
		inputs.read(bs) ;
		
		if(bs[0]!='F' || bs[1]!='I' || bs[2] != 'N' || bs[3]!='S')
			return null ;
		if(bs[7]!=0x10)
			return null ;
		if( (0xFF & bs[19])!=client_pc_last_ip)
			return null ;
		return (short)(0xFF & bs[23]) ;
	}
}
