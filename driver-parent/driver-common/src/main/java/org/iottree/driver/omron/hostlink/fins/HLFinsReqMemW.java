package org.iottree.driver.omron.hostlink.fins;

import java.util.List;

import org.iottree.core.util.IBSOutput;
import org.iottree.core.util.xmldata.DataUtil;
import org.iottree.driver.omron.fins.FinsMode;
import org.iottree.driver.omron.hostlink.HLMsgResp;

public class HLFinsReqMemW extends HLFinsReq
{
	//short memAreaCode ;
	
	int beginAddr ;
	
	int itemNum ;
	
	List<Boolean> bitVals = null ;
	
	List<Short> wordVals = null ;
	
	FinsMode.AreaCode areaCode = null ; 
	
	boolean bBit = false;
	
	public HLFinsReqMemW(FinsMode mode)
	{
		super(mode);
	}

	@Override
	protected short getMR()
	{
		return 01;
	}

	@Override
	protected short getSR()
	{
		return 02;
	}
	
	
	public HLFinsReqMemW asReqWBit(String mem_area,int begin_addr,int item_num,List<Boolean> bitvals)
	{
		areaCode = mode.getAreaCodeBit(mem_area) ;
		
		if(areaCode==null)
			throw new IllegalArgumentException("no AreaCode found with mem area="+mem_area) ;
		
		//this.memAreaCode = ac.getCode() ;
		this.beginAddr = begin_addr ;
		this.itemNum = item_num ;
		this.bitVals = bitvals ;
		this.bBit = true ;
		return this ;
	}
	
	public HLFinsReqMemW asReqWWord(String mem_area,int begin_addr,int item_num,List<Short> wvals)
	{
		areaCode = mode.getAreaCodeWord(mem_area) ;
		if(areaCode==null)
			throw new IllegalArgumentException("no AreaCode found with mem area="+mem_area) ;
		
		//this.memAreaCode = ac.getCode() ;
		this.beginAddr = begin_addr ;
		this.itemNum = item_num ;
		this.wordVals = wvals;
		this.bBit = false ;
		return this ;
	}
	
	public FinsMode.AreaCode getAreaCode()
	{
		return this.areaCode ;
	}

	@Override
	protected void packOutCmdParam(IBSOutput outputs) 
	{
		byte[] bs = null;
		
		if(bBit)
		{
			bs = new byte[6+itemNum] ;
			bs[0] = (byte)areaCode.getCode();
			int2byte3(beginAddr,bs,1) ;
			short2bytes((short)itemNum,bs,4) ;
			for(int i = 0 ; i < itemNum ; i ++)
				bs[6+i] = (byte)(this.bitVals.get(i)?1:0) ; 
		}
		else
		{
			bs = new byte[6+itemNum*2] ;
			bs[0] = (byte)areaCode.getCode();
			int2byte3(beginAddr,bs,1) ;
			short2bytes((short)itemNum,bs,4) ;
			for(int i = 0 ; i < itemNum ; i ++)
			{
				DataUtil.shortToBytes(this.wordVals.get(i), bs, 6+i*2);
			}
		}
		outputs.write(bs);
	}

	@Override
	protected HLMsgResp newRespInstance()
	{
		return new HLFinsRespOnlyEnd(this);
	}

}
