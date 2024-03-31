package org.iottree.driver.omron.fins;

import org.iottree.core.util.IBSOutput;

public class FinsCmdReqR extends FinsCmdReq
{
	short memAreaCode ;
	
	int beginAddr ;
	
	int itemNum ;
	
	public FinsCmdReqR(FinsMode fins_mode)
	{
		super(fins_mode);
	}


	
	public FinsCmdReqR asReqR(String mem_area,int begin_addr,int item_num)
	{
		FinsMode.AreaCode ac = mode.getAreaCodeBit(mem_area) ;
		if(ac==null)
			throw new IllegalArgumentException("no AreaCode found with mem area="+mem_area) ;
		
		this.memAreaCode = ac.getCode() ;
		this.beginAddr = begin_addr ;
		this.itemNum = item_num ;
		return this ;
	}
	
	@Override
	protected void writeParam(IBSOutput outputs) // throws IOException
	{
		byte[] bs = new byte[6] ;
		bs[0] = (byte)memAreaCode;
		int2byte3(beginAddr,bs,1) ;
		short2bytes((short)itemNum,bs,4) ;
		
		outputs.write(bs);
	}
	


	@Override
	protected int getParamBytesNum()
	{
		return 6;
	}

}
