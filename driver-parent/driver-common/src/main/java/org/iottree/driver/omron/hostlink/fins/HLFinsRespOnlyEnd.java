package org.iottree.driver.omron.hostlink.fins;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.xmldata.DataUtil;
import org.iottree.driver.omron.fins.FinsMode;
import org.iottree.driver.omron.hostlink.HLMsgReq;

public class HLFinsRespOnlyEnd extends HLFinsResp
{
	private HLFinsReqMemW myReq = null ;
	
	
	public HLFinsRespOnlyEnd(HLMsgReq req)
	{
		super(req);
		
		myReq = (HLFinsReqMemW)req ;
	}

	@Override
	protected void parseFinsRet(String fins_ret) throws Exception
	{
		
	}

}
