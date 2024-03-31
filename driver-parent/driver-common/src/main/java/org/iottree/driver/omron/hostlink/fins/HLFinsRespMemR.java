package org.iottree.driver.omron.hostlink.fins;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.xmldata.DataUtil;
import org.iottree.driver.omron.fins.FinsMode;
import org.iottree.driver.omron.hostlink.HLMsgReq;

public class HLFinsRespMemR extends HLFinsResp
{
	private HLFinsReqMemR myReq = null ;
	
	byte[] retBS = null ;
	
	public HLFinsRespMemR(HLMsgReq req)
	{
		super(req);
		
		myReq = (HLFinsReqMemR)req ;
	}

	@Override
	protected void parseFinsRet(String fins_ret) throws Exception
	{
		FinsMode.AreaCode ac = myReq.getAreaCode() ;
		retBS = hex2bytes(fins_ret) ;
		if(ac.isBit())
		{
			if(retBS.length!=myReq.itemNum)
				throw new Exception("response data num is not match to request") ;
			
		}
		else
		{
			if(retBS.length!=myReq.itemNum*2)
				throw new Exception("response data num is not match to request") ;
		}
	}

	public String getBitStr()
	{
		if(retBS==null)
			return "" ;
		StringBuilder sb = new StringBuilder() ;
		for(byte b:retBS)
		{
			sb.append((b==0?"0":"1")) ;
		}
		return sb.toString() ;
	}
	
	public List<Boolean> getBitList()
	{
		if(retBS==null)
			return null ;
		ArrayList<Boolean> rets = new ArrayList<>(retBS.length) ;
		for(byte b:retBS)
		{
			rets.add(b!=0) ;
		}
		return rets ;
	}
	
	public List<Short> getWordList()
	{
		if(retBS==null)
			return null ;
		int n = retBS.length/2 ;
		ArrayList<Short> rets = new ArrayList<>(retBS.length/2) ;
		for(int i = 0 ; i < n ; i ++)
		{
			short s = DataUtil.bytesToShort(retBS, i*2) ;
			rets.add(s) ;
		}
		return rets ;
	}
	
	public String getWordStr()
	{
		List<Short> ws = getWordList() ;
		if(ws==null)
			return "" ;
		
		StringBuilder sb = new StringBuilder() ;
		boolean bfirst = true;
		for(Short s:ws)
		{
			if(bfirst) bfirst=false;
			else sb.append(',') ;
			sb.append(s) ;
		}
		return sb.toString() ;
	}
	
	/**
	 * if bit , one byte for bit
	 * if word ,two bytes for one word
	 * @return
	 */
	public byte[] getReturnBytes()
	{
		return retBS ;
	}
}
