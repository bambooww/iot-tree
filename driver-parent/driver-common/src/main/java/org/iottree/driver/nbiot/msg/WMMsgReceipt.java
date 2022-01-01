package org.iottree.driver.nbiot.msg;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.iottree.core.util.Convert;

/**
 * receipt
 * 
 * @author jason.zhu
 */
public class WMMsgReceipt extends WMMsgDT
{
	public static final int TP_NOR = 0 ;
	public static final int TP_ALERT = 1 ;
	public static final int TP_TESTER = 2 ;
	
	int receiptTp = TP_NOR;
	
	boolean bContinue = false;
	
	public WMMsgReceipt()
	{
		this.func = new byte[] {0x01,0x01} ;
	}
//	public void setMsgBasic(byte[] addr,byte[] func)
//	{
//		super.setMsgBasic(addr,func) ;
//		
//		//if(func[0]!=0x01 || func[1]!=0x01)
//		//	throw new IllegalArgumentException("no receipt msg") ;
//	}
	
	public void setReceiptTp(int tp)
	{
		switch(tp)
		{
		case TP_NOR:
			this.func = new byte[] {0x01,0x01} ;
			break ;
		case TP_ALERT:
			this.func = new byte[] {0x07,0x07} ;
			break ;
		case TP_TESTER:
			this.func = new byte[] {0x10,0x10} ;
			break ;
		default:
			throw new IllegalArgumentException("invalid receipt tp") ;
		}
		
		this.receiptTp = tp ;
	}
	
	
	
	public boolean isReceiptAlert()
	{
		if(this.func==null)
			return false;
		return this.func[0]==0x07 && this.func[1] == 0x07 ;
	}
	
	public void setContinue(boolean b)
	{
		bContinue = b ;
	}
	
	public boolean isContinue()
	{
		return this.bContinue ;
	}
	
	@Override
	protected ArrayList<byte[]> getMsgBody()
	{
		ArrayList<byte[]> bbs = super.getMsgBody() ;
		if(bContinue)
			bbs.add(new byte[] {(byte)0xaa}) ;
		else
			bbs.add(new byte[] {(byte)0x00}) ;
		return bbs ;
	}
	
//	protected ArrayList<byte[]> parseMsgBody(InputStream inputs) throws IOException
//	{
//		ArrayList<byte[]> bbs = super.parseMsgBody(inputs) ;
//		
//		return bbs ;
//	}
	
	public String toString()
	{
		String ret=super.toString() ;
		
		//ret += " dt="+Convert.toFullYMDHMS(this.getMsgDTDate());
		
		return "Receipt:"+ret ;
	}
}
