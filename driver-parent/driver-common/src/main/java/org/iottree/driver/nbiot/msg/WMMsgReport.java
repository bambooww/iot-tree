package org.iottree.driver.nbiot.msg;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 
 A1 68 11 00 01 08 11 11 66 66 81 01 21 01 01 08 11 32 00 00 09 41 2D 96 72 95 96 72 95 00 18 94 96 72 95 94 
 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 
 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 
 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 7F FF 01 66 34 36 30 30 34 39 30 39 31 31 30 39 38 36 33 38 36 32 35 39 32 30 35 30 30 37 39 30 39 37 38 39 38 36 30 34 37 30 31 39 32 30 38 31 35 31 34 38 36 33 11 FF A9 00 12 9A 16
 * @author jason.zhu
 *
 */
public class WMMsgReport extends WMMsgDT
{
	/**
	 * 当前表读数
	 */
	long curMeterVal = -1 ;
	
	/**
	 * 表单位
	 */
	byte meterUnit = -1 ;
	
	/**
	 * 冻结起始时间
	 */
	byte[] fixStartDT = null ;
	
	/**
	 * 冻结采集间隔
	 */
	byte fixCollInt = -1 ;
	
	/**
	 * 冻结数据条目数
	 */
	int fixNum = -1 ;
	
	long[] fixData = null ;
	
	@Override
	protected ArrayList<byte[]> getMsgBody()
	{
//		ArrayList<byte[]> bbs = super. getMsgBody() ;
//		bbs.add(e)
		throw new RuntimeException("no impl") ;
	}
	
	public long getCurMeterVal()
	{
		return curMeterVal ;
	}
	
	public byte getMeterUnit()
	{
		return meterUnit ;
	}
	
	public float getMeterUnitVal()
	{
		switch(this.meterUnit)
		{
		case 0x2B:
			return 1.0f ;
		case 0x2C:
			return 0.1f;
		case 0x2D:
			return 0.01f;
		default:
			return 0 ;
		}
	}
	
	
	public boolean isTestReport()
	{
		return this.func[1]==0x10 ;
	}

	protected ArrayList<byte[]> parseMsgBody(InputStream inputs) throws IOException
	{
		ArrayList<byte[]> bbs = super.parseMsgBody(inputs) ;
		if(bbs==null)
			return null;
		
		if(inputs.available()<5)
			return null;
		
		byte[] bs = readLenTimeout(inputs,5);//new byte[13] ;
		bbs.add(bs) ;
		
		//inputs.read(bs) ;
		curMeterVal = bcd2long(bs,0,4) ;
		meterUnit = bs[4] ;
		
		if(!this.isTestReport())
		{
			bs = readLenTimeout(inputs, 8);
			bbs.add(bs) ;
			
			fixStartDT = new byte[6] ;
			System.arraycopy(bs, 0, fixStartDT, 0, 6);
			fixCollInt = bs[6] ;
			fixNum = bs[7] & 0xFF ;
			
			int av_len = inputs.available();
			if(av_len<fixNum*4)
				return null;
			
			byte[] fdbs = readLenTimeout(inputs,fixNum*4) ;
			fixData = new long[fixNum] ;
			//inputs.read(fdbs) ;
			
			for(int i = 0 ; i < fixNum ; i ++)
			{
				fixData[i] = bcd2long(fdbs,i*4,4) ;
			}
		
			bbs.add(fdbs) ;
		}
		
		byte[] z34 = readLenTimeout(inputs,9) ;
		bbs.add(z34) ;
		
		byte[] z35 = readLenTimeout(inputs,9) ;
		bbs.add(z35) ;
		
		byte[] z36 = readLenTimeout(inputs,2) ;
		bbs.add(z36) ;
		
		byte[] z37 = readLenTimeout(inputs,2) ; //模块电压
		bbs.add(z37) ;
		
		byte[] z38 = readLenTimeout(inputs,15) ;
		bbs.add(z38) ;
		
		byte[] z39 = readLenTimeout(inputs,15) ;
		bbs.add(z39) ;
		
		byte[] z40 = readLenTimeout(inputs,20) ;
		bbs.add(z40) ;
		
		byte[] z41 = readLenTimeout(inputs,1) ; //csq
		bbs.add(z41) ;
		
		byte[] z42 = readLenTimeout(inputs,2) ; //rsrp
		bbs.add(z42) ;
		
		byte[] z43 = readLenTimeout(inputs,2) ; //
		bbs.add(z43) ;
		
		return bbs ;
	}
	
	
	public WMMsgReceipt createReceipt(boolean bcontinue)
	{
		WMMsgReceipt receipt = new WMMsgReceipt() ;
		receipt.setMeterAddr(getMeterAddr());
		if(this.isTestReport())
			receipt.setReceiptTp(WMMsgReceipt.TP_TESTER);
		else
			receipt.setReceiptTp(WMMsgReceipt.TP_NOR);
		receipt.setContinue(bcontinue);
		receipt.setMsgDT(new Date());
		return receipt ;
	}
	
	public String toString()
	{
		String ret=super.toString() ;
		
		ret += " cur_val="+this.curMeterVal;
		ret += " unit="+getMeterUnitVal();
		
		return "Report:"+ret ;
	}
}
