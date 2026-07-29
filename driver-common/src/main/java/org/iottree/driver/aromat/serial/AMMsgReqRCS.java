package org.iottree.driver.aromat.serial;

public class AMMsgReqRCS extends AMMsgReqRC
{
	char contactC  ;
	
	int contanctN ;

	public AMMsgReqRCS asContactCode(char cc,int num)
	{
		if(num>9999)
			throw new IllegalArgumentException("num is too big") ;
		
		this.contactC = cc ;
		this.contanctN = num ;
		return this;
	}
	
	@Override
	protected void packContent(StringBuilder sb)
	{
		sb.append('S') ;
		sb.append(contactC) ;
		sb.append(byte_to_bcd4(contanctN)) ;
	}
	
	@Override
	protected AMMsgResp newRespInstance()
	{
		return new AMMsgRespRCS(this);
	}

}
