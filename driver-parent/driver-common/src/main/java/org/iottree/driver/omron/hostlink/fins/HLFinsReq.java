package org.iottree.driver.omron.hostlink.fins;

import org.iottree.core.util.BSOutputBuf;
import org.iottree.core.util.IBSOutput;
import org.iottree.core.util.xmldata.DataUtil;
import org.iottree.driver.omron.fins.FinsMode;
import org.iottree.driver.omron.hostlink.HLMsg;
import org.iottree.driver.omron.hostlink.HLMsgReq;

public abstract class HLFinsReq extends HLMsgReq
{
	/**
	 * 0-F  with *10ms, e.g F=150ms
	 */
	short respWaitTime =0;
	
	boolean bHeaderNet = false;
	
	short icf = -1 ;
	
	short gct = -1 ;
	
	short dna = -1 ; //desction network addr 
	
	short da1 = -1 ;
	
	short da2 = -1 ;
	
	short sna = -1 ;
	
	short sa1 = -1 ;
	
	short sa2 = -1 ;
	
	short sid = -1 ;
	
	FinsMode mode ;
	
	public HLFinsReq(FinsMode mode)
	{
		this.mode = mode ;
	}
	
	public HLFinsReq asRespWaitTime(short wt)
	{
		if(wt<0||wt>0xF)
			throw new IllegalArgumentException("Response Wait Time must in [0,F]");
		this.respWaitTime = wt ;
		return this;
	}
	
	@Override
	public String getHeadCode()
	{
		return "FA";
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
	public HLFinsReq asFinsHeaderSerial(int da2,int sa2,int sid)
	{
		bHeaderNet=false;
		this.icf = 0 ;
		this.da2 = (short)da2 ;
		this.sa2 = (short)sa2 ;
		this.sid = (short)sid ;
		return this ;
	}
	
	public HLFinsReq asFinsHeaderSerial()
	{
		return asFinsHeaderSerial((short)0,(short)0,(short)0) ;
	}
	
	/**
	 * CPU Unit on a Network
	 * 
	 * @return
	 */
	public HLFinsReq asFinsHeaderNet(int icf,int gct,int dna,int da1,int da2,
			int sna,int sa1,int sa2,int sid)
	{
		bHeaderNet = true;
		this.icf = (short)icf ;
		this.gct = (short)gct ;
		this.dna = (short)dna ;
		this.da1 = (short)da1 ;
		this.da2 = (short)da2 ;
		this.sna = (short)sna ;
		this.sa1 = (short)sa1 ;
		this.sa2 = (short)sa2 ;
		this.sid = (short)sid ;
		return this ;
	}

	@Override
	protected final void packContent(StringBuilder sb)
	{
		sb.append(byte2hex(this.respWaitTime,false)) ;
		if(!bHeaderNet)
		{
			sb.append(byte2hex(this.icf,true)) ;
			sb.append(byte2hex(this.da2,true)) ;
			sb.append(byte2hex(this.sa2,true)) ;
			sb.append(byte2hex(this.sid,true)) ;
		}
		else
		{
			sb.append(byte2hex(this.icf,true)) ;
			sb.append(byte2hex(0,true)) ; //RSV
			sb.append(byte2hex(this.gct,true)) ;
			sb.append(byte2hex(this.dna,true)) ;
			sb.append(byte2hex(this.da1,true)) ;
			sb.append(byte2hex(this.da2,true)) ;
			sb.append(byte2hex(this.sna,true)) ;
			sb.append(byte2hex(this.sa1,true)) ;
			sb.append(byte2hex(this.sa2,true)) ;
			sb.append(byte2hex(this.sid,true)) ;
		}
		
		sb.append(byte2hex(this.getMR(),true)) ;
		sb.append(byte2hex(this.getSR(),true)) ;
		
		packCmdText(sb);
	}
	
	
	/**
	 * command code MR
	 * @return
	 */
	protected abstract short getMR() ;
	
	/**
	 * command code SR
	 * @return
	 */
	protected abstract short getSR() ;
	
	
	
	private void packCmdText(StringBuilder sb)
	{
		BSOutputBuf bso = new BSOutputBuf() ;
		packOutCmdParam(bso);
		sb.append(bso.toHexStr(true)) ;
	}
	
	protected abstract void packOutCmdParam(IBSOutput bso) ;
	
	
	protected static final byte[] int2bytes(int i)
	{
		return DataUtil.intToBytes(i) ;
	}
	
	protected static final void int2byte3(int i,byte[] bytes,int offset)
	{
		bytes[offset+3] = (byte) (i & 0xFF);
		i = i >>> 8;
		bytes[offset+2] = (byte) (i & 0xFF);
		i = i >>> 8;
		bytes[offset+1] = (byte) (i & 0xFF);
		//i = i >>> 8;
		//bytes[offset] = (byte) (i & 0xFF);
		
	}
	
	protected static final byte[] short2bytes(short i)
	{
		return DataUtil.shortToBytes(i) ;
	}
	
	protected static final void short2bytes(short i,byte[] bs,int offset)
	{
		DataUtil.shortToBytes(i,bs,offset) ;
	}
}
