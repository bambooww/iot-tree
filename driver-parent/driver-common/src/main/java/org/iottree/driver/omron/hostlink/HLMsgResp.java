package org.iottree.driver.omron.hostlink;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.iottree.core.dict.DataNode;
import org.iottree.core.util.Lan;

public abstract class HLMsgResp extends HLMsg
{
	protected HLMsgReq hlMsgReq	= null ;
	
	String endCode = null ;
	
	String retTxt = null ;
	
	String headCode = null ;
	
	public HLMsgResp(HLMsgReq req)
	{
		this.hlMsgReq = req ;
	}
	
	public String getHLEndCode()
	{
		return endCode ;
	}
	
	
	@Override
	public String getHeadCode()
	{
		return headCode;
	}

	
	protected void parseFrom(String str) throws Exception
	{
		int len = str.length() ;
		if('@'!=str.charAt(0))
			throw new IllegalArgumentException("no start @") ;
		this.plcUnit = bcd2_to_byte(str.charAt(1),str.charAt(2)) ;
		this.headCode = str.substring(3,5) ;
		this.endCode = str.substring(5,7) ;
		
		String hl_txt = str.substring(7) ;
		
		this.parseHLTxt(hl_txt);
	}

	protected abstract void parseHLTxt(String hl_txt) throws Exception;
	
	
	public static String getEndCodeTitle(String end_code)
	{
		Lan lan = Lan.getLangInPk(HLMsgResp.class) ;
		DataNode dn = lan.gn("encode_"+end_code);
		if(dn==null)
			return "" ;
		return dn.getNameByLang("en") ;
	}
}
