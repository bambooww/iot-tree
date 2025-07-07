package org.iottree.driver.omron.fins;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.iottree.core.util.BSOutputBuf;
import org.iottree.core.util.IBSOutput;
import org.iottree.core.util.xmldata.DataUtil;

public abstract class FinsMsgReq extends FinsMsg
{
	public FinsMsgReq()
	{

	}

	/**
	 * 0-F  with *10ms, e.g F=150ms
	 */
	short respWaitTime =0;
	
	//boolean bHeaderTcp = false;
	
	//short icf = -1 ;
	
	//short gct = -1 ;
	
	short dna = -1 ; //desction network addr 
	
	short da1 = -1 ;
	
	short da2 = -1 ;
	
	short sna = -1 ;
	
	short sa1 = -1 ;
	
	short sa2 = -1 ;
	
	//short sid = -1 ;
	
	protected FinsMode mode ;
	
	public FinsMsgReq(FinsMode mode)
	{
		this.mode = mode ;
	}
	
	public FinsMsgReq asRespWaitTime(short wt)
	{
		if(wt<0||wt>0xF)
			throw new IllegalArgumentException("Response Wait Time must in [0,F]");
		this.respWaitTime = wt ;
		return this;
	}
	
	/**
	 * CPU Unit Directly Connected to the Host Computer
	 *  
	 * @param icf
	 * @param da2
	 * @param sa2
	 * @param sid
	 * @return
	 */
	public FinsMsgReq asFinsHeaderSerial(int da2,int sa2)
	{
		//bHeaderTcp=false;
		//this.icf = 0 ;
		this.da2 = (short)da2 ;
		this.sa2 = (short)sa2 ;
		//this.sid = (short)sid ;
		return this ;
	}
	
	public FinsMsgReq asFinsHeaderSerial()
	{
		return asFinsHeaderSerial((short)0,(short)0) ;
	}
	
	/**
	 * CPU Unit on a Network
	 * 
	 * @return
	 */
	public FinsMsgReq asFinsHeader(int dna,int da1,int da2,
			int sna,int sa1,int sa2)
	{
		//bHeaderTcp = true;
		//this.icf = (short)icf ;
		//this.gct = (short)gct ;
		this.dna = (short)dna ;
		this.da1 = (short)da1 ;
		this.da2 = (short)da2 ;
		this.sna = (short)sna ;
		this.sa1 = (short)sa1 ;
		this.sa2 = (short)sa2 ;
		//this.sid = (short)sid ;
		return this ;
	}
	
//	public FinsMsgReq asFinsHeaderUDP(int icf,int gct,int dna,int da1,int da2,
//			int sna,int sa1,int sa2,int sid)
//	{
//		bHeaderTcp = false;
//		this.icf = (short)icf ;
//		//this.gct = (short)gct ;
//		this.dna = (short)dna ;
//		this.da1 = (short)da1 ;
//		this.da2 = (short)da2 ;
//		this.sna = (short)sna ;
//		this.sa1 = (short)sa1 ;
//		this.sa2 = (short)sa2 ;
//		this.sid = (short)sid ;
//		return this ;
//	} 
	

	
	public final void writeOutTCP(OutputStream outputs) throws IOException
	{
		byte[] bs = null ;
		outputs.write(FINS); //fixed FINS
		int len = this.calcFinsFrameLen() +8;
		bs = int2bytes(len) ;
		outputs.write(bs);
		bs = int2bytes(2) ;//getTcpHeadCmd()) ; // command req=2
		outputs.write(bs);
		bs = int2bytes(0) ;//err code
		outputs.write(bs);
		
		this.writeFinsFrame(outputs);
	}
	
	protected int calcFinsFrameLen()
	{
		return 12+calcParamBytesNum();
	}
	
	protected void writeFinsFrame(OutputStream outputs) throws IOException
	{
		byte[] bs = new byte[12] ;
		
		bs[0] = (byte)getICF() ; //ICF
		bs[1] = 0; //RSV
		bs[2] = 2; //GCT
		bs[3] = (byte)this.dna; //DNA
		bs[4] = (byte)this.da1; //DA1  target client_last_ip  PLC/PC
		bs[5] = (byte)this.da2 ; //DA2
		bs[6] = (byte)this.sna ;//SNA
		bs[7] = (byte)this.sa1; //SA1 source client last ip  PLC/PC
		bs[8] = (byte)this.sa2 ;
		bs[9] = 0; //SID
		bs[10] = (byte)this.getMRC() ;
		bs[11] = (byte)this.getSRC() ;
		
		outputs.write(bs);
		
		
		this.writeParam(outputs);
	}
	

	protected final short getICF()
	{
		if(isNeedResp())
			return 0x80;
		else
			return 0x81 ;
	}
	
	protected boolean isNeedResp()
	{
		return true ;
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
	
	protected abstract int calcParamBytesNum() ;
	
	protected abstract void writeParam(OutputStream outputs) throws IOException ;
	

	
//	private void packCmdText(StringBuilder sb)
//	{
//		BSOutputBuf bso = new BSOutputBuf() ;
//		packOutCmdParam(bso);
//		sb.append(bso.toHexStr(true)) ;
//	}
//	
//	protected abstract void packOutCmdParam(IBSOutput bso) ;
	
	
}
