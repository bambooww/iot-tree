package org.iottree.driver.aromat.serial;

import org.iottree.core.dict.DataNode;
import org.iottree.core.util.Lan;

public abstract class AMMsgResp extends AMMsg
{
	protected AMMsgReq msgReq	= null ;
	
	char respMark  =0;
	String respCode = null ;
	
	private String retTxt = null ;
	
	public AMMsgResp(AMMsgReq req)
	{
		this.msgReq = req ;
	}
	
	public char getRespMark()
	{
		return respMark ;
	}
	
	public boolean isRespErr()
	{
		return respMark=='!' ;
	}
	
	public boolean isRespNor()
	{
		return respMark=='$' ;
	}
	
	public String getRespCode()
	{
		return respCode ;
	}
	
	

	public String getRetTxt()
	{
		return retTxt ;
	}
	
	protected void parseFrom(String str) throws Exception
	{
		this.retTxt = str ;
		//int len = str.length() ;
		if('@'!=str.charAt(0))
			throw new IllegalArgumentException("no start @") ;
		this.plcAddr = hex2byte(str.substring(1,3)) ;
		respMark = str.charAt(3) ;
		this.respCode = str.substring(4,6) ;
		
		if(respMark=='!')
		{// error resp
			return ;
		}
		
		if(respMark!='$')
			throw new Exception("unknown resp mark="+respMark) ;
		
		String hl_txt = str.substring(6) ;
		
		this.parseRespTxt(hl_txt);
	}

	protected abstract void parseRespTxt(String resp_txt) throws Exception;
	
	
	public static String getRespCodeTitle(String code)
	{
		Lan lan = Lan.getLangInPk(AMMsgResp.class) ;
		DataNode dn = lan.gn("encode_"+code);
		if(dn==null)
			return "" ;
		return dn.getNameByLang("en") ;
	}
}
