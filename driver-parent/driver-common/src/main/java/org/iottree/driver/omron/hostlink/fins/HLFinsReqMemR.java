package org.iottree.driver.omron.hostlink.fins;

import org.iottree.core.util.IBSOutput;
import org.iottree.driver.omron.fins.FinsCmdReqR;
import org.iottree.driver.omron.fins.FinsMode;
import org.iottree.driver.omron.hostlink.HLMsgResp;

/**
 * MEMORY AREA READ: 01 01
 * 
 * @author jason.zhu
 *
 */
public class HLFinsReqMemR extends HLFinsReq
{
	//short memAreaCode ;
	
	int beginAddr ;
	
	int beginBit ;
	
	int itemNum ;
	
	FinsMode.AreaCode areaCode = null ; 
	
	public HLFinsReqMemR(FinsMode mode)
	{
		super(mode);
	}
	
	public int getBeginAddr()
	{
		return this.beginAddr ;
	}
	
	public int getItemNum()
	{
		return this.itemNum ;
	}

	@Override
	protected short getMR()
	{
		return 01;
	}

	@Override
	protected short getSR()
	{
		return 01;
	}
	
	
	public HLFinsReqMemR asReqR(String mem_area,boolean b_bit,int begin_addr,int begin_bit,int item_num)
	{
		if(b_bit)
			areaCode = mode.getAreaCodeBit(mem_area) ;
		else
			areaCode = mode.getAreaCodeWord(mem_area) ;
		if(areaCode==null)
			throw new IllegalArgumentException("no AreaCode found with mem area="+mem_area) ;
		
		//this.memAreaCode = ac.getCode() ;
		this.beginAddr = begin_addr ;
		this.beginBit = begin_bit ;
		this.itemNum = item_num ;
		return this ;
	}
	
	public FinsMode.AreaCode getAreaCode()
	{
		return this.areaCode ;
	}

	@Override
	protected void packOutCmdParam(IBSOutput outputs) 
	{
		byte[] bs = new byte[6] ;
		bs[0] = (byte)areaCode.getCode();
		short2bytes((short)beginAddr,bs,1) ;
		bs[3] = (byte)beginBit;//short2bytes((short)beginAddr,bs,3) ;
		short2bytes((short)itemNum,bs,4) ;
		outputs.write(bs);
	}

	@Override
	protected HLMsgResp newRespInstance()
	{
		return new HLFinsRespMemR(this);
	}

}
