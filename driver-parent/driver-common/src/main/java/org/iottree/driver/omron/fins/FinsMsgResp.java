package org.iottree.driver.omron.fins;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.iottree.core.util.IBSOutput;

public abstract class FinsMsgResp extends FinsMsg
{
	int dna = -1 ; //desction network addr 
	
	int da1 = -1 ;
	
	int da2 = -1 ;
	
	int sna = -1 ;
	
	int sa1 = -1 ;
	
	int sa2 = -1 ;
	
	int mrc = -1 ;
	
	int src = -1 ;
	
	transient FinsMsgReq req  ;
	
	public FinsMsgResp(FinsMsgReq req)
	{
		this.req = req ;
	}
	
	public FinsMsgReq getReq()
	{
		return this.req ;
	}
	
	public final boolean readFromAsTcp(InputStream inputs,long timeout,int max_len,StringBuilder failedr) throws IOException
	{
		checkStreamLenTimeout(inputs, 16, timeout);
		
		byte[] bs = new byte[16] ;
		inputs.read(bs) ;
		
		if(bs[0]!='F' || bs[1]!='I' || bs[2] != 'N' || bs[3]!='S')
			return false ;
		int len = bytes2int(bs,4)-8 ;
		int cmd = bytes2int(bs,8) ;
		if(cmd!=2)
			return false;
		if(len<=0 || len>max_len)
			return false;
		
		return readFinsFrame(inputs,timeout,len,failedr);
	}
	
	protected boolean readFinsFrame(InputStream inputs,long timeout,int len,StringBuilder failedr) throws IOException
	{
		if(len<12)
			return false;
		checkStreamLenTimeout(inputs, 12, timeout);
		byte[] f_h = new byte[12] ;
		inputs.read(f_h) ;
		
		this.dna = f_h[3] & 0xFF;
		this.da1 = f_h[4] & 0xFF;
		this.da2 = f_h[5] & 0xFF;
		this.sna = f_h[6] & 0xFF;
		this.sa1 = f_h[7] & 0xFF;
		this.sa2 = f_h[8] & 0xFF;
		
		this.mrc = f_h[10] & 0xFF;
		this.src = f_h[11] & 0xFF;
		
		int leftn = len-12 ;
		if(leftn<=0)
			return true ;
		checkStreamLenTimeout(inputs, leftn, timeout);
		byte[] param_bs =new byte[leftn] ;
		inputs.read(param_bs) ;
		
		return parseParam(this.mrc,this.src,param_bs,failedr) ;
	}
	
	protected abstract boolean parseParam(int mrc,int src,byte[] param_bs,StringBuilder failedr) ;
}
