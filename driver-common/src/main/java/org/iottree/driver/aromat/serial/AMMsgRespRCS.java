package org.iottree.driver.aromat.serial;

public class AMMsgRespRCS extends AMMsgResp
{
	Boolean bOffOn = null ;  
			
	public AMMsgRespRCS(AMMsgReq req)
	{
		super(req);
	}

	@Override
	protected void parseRespTxt(String resp_txt) throws Exception
	{
		char c = resp_txt.charAt(0) ;
		if(c=='1')
			bOffOn = true ;
		else if(c=='0')
			bOffOn=false ;
	}

	public Boolean getRespOffOn()
	{
		return bOffOn ;
	}
}
