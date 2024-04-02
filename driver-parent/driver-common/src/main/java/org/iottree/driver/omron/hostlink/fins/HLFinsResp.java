package org.iottree.driver.omron.hostlink.fins;

import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.iottree.driver.omron.fins.FinsEndCode;
import org.iottree.driver.omron.fins.FinsMode;
import org.iottree.driver.omron.hostlink.HLException;
import org.iottree.driver.omron.hostlink.HLMsg;
import org.iottree.driver.omron.hostlink.HLMsgReq;
import org.iottree.driver.omron.hostlink.HLMsgResp;

public abstract class HLFinsResp extends HLMsgResp
{
	HLFinsReq hlFinsReq ;
	FinsMode mode ;
	
short icf = -1 ;
	
	short gct = -1 ;
	
	short dna = -1 ; //desction network addr 
	
	short da1 = -1 ;
	
	short da2 = -1 ;
	
	short sna = -1 ;
	
	short sa1 = -1 ;
	
	short sa2 = -1 ;
	
	short sid = -1 ;
	
	// FINS command code mr sr
	short finsMR = -1 ;
	short finsSR = -1 ;
	
	// FINS end code The end code is a 2-byte code
	FinsEndCode finsEndCode = null ;
	
	public HLFinsResp(HLMsgReq req)
	{
		super(req) ;
		
		this.hlFinsReq = (HLFinsReq)req ;
		this.mode = this.hlFinsReq.mode ;
	}
	
	@Override
	protected void parseHLTxt(String hl_txt) throws HLException
	{
		if(!"FA".equals(this.getHeadCode()))
			throw new HLException(0,"no FA Host link msg") ;
		
		String enc = this.getHLEndCode() ;
		if(!"00".equals(enc))
		{
			String tt = getEndCodeTitle(enc) ;
			if(Convert.isNullOrEmpty(tt))
				tt = enc ;
			throw new HLException(0,"resp end err:"+tt) ;
		}
		
		parseFinsTxt(hl_txt) ;
	}
	
	private void parseFinsTxt(String hl_txt) throws HLException
	{
		short main ;
		short sub  ;
		String fins_txt ;
		if(!this.hlFinsReq.bHeaderNet)
		{
			this.icf = hex2byte(hl_txt.substring(0,2)) ;
			this.da2 = hex2byte(hl_txt.substring(2,4)) ;
			this.sa2 = hex2byte(hl_txt.substring(4,6)) ;
			this.sid = hex2byte(hl_txt.substring(6,8)) ;

			finsMR = hex2byte(hl_txt.substring(8,10)) ;
			finsSR = hex2byte(hl_txt.substring(10,12)) ;
			
			main = hex2byte(hl_txt.substring(12,14)) ;
			sub = hex2byte(hl_txt.substring(14,16)) ;
			fins_txt = hl_txt.substring(16) ;
		}
		else
		{
			this.icf = hex2byte(hl_txt.substring(0,2)) ;
			//RSV  2 - 4
			this.gct = hex2byte(hl_txt.substring(4,6)) ;
			this.dna = hex2byte(hl_txt.substring(6,8)) ;
			this.da1 = hex2byte(hl_txt.substring(8,10)) ;
			this.da2 = hex2byte(hl_txt.substring(10,12)) ;
			this.sna = hex2byte(hl_txt.substring(12,14)) ;
			this.sa1 = hex2byte(hl_txt.substring(14,16)) ;
			this.sa2 = hex2byte(hl_txt.substring(16,18)) ;
			this.sid = hex2byte(hl_txt.substring(18,20)) ;
			
			finsMR = hex2byte(hl_txt.substring(20,22)) ;
			finsSR = hex2byte(hl_txt.substring(22,24)) ;
			
			main = hex2byte(hl_txt.substring(24,26)) ;
			sub = hex2byte(hl_txt.substring(26,28)) ;
			fins_txt = hl_txt.substring(28) ;
		}
		
		
		finsEndCode = new FinsEndCode(main,sub) ;

		if(finsEndCode.isNormal())
		{
			parseFinsRet(fins_txt) ;
		}
	}
	
	public FinsEndCode getFinsEndCode()
	{
		return this.finsEndCode ;
	}
	
	public boolean isFinsEndOk()
	{
		return this.finsEndCode.isNormal() ;
	}

	protected abstract void parseFinsRet(String fins_txt) throws HLException;
}
