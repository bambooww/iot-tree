package org.iottree.driver.aromat.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public abstract class AMMsgReq extends AMMsg
{
	public abstract String getCmdCode() ;
	
	public final String packToStr()
	{
		StringBuilder sb = new StringBuilder() ;
		sb.append(this.head) ;
		//sb.append(byte2hex(this.plcAddr, true));
		sb.append(byte_to_bcd2(plcAddr));
		sb.append('#') ;
		sb.append(getCmdCode()) ;
		
		packContent(sb) ; 
		
		String fcs = calBCC(sb.toString()) ;
		sb.append(fcs).append("\r") ;
		return sb.toString() ;
	}
	
	private String packAckStr()
	{
		StringBuilder sb = new StringBuilder() ;
		sb.append(this.head) ;
		//sb.append(byte2hex(this.plcAddr, true));
		sb.append(byte_to_bcd2(plcAddr));
		String fcs = calBCC(sb.toString()) ;
		sb.append(fcs).append("&\r") ;
		return sb.toString() ;
	}
	
	
	public final String writeTo(OutputStream outputs) throws IOException
	{
		//StringBuilder sb = new StringBuilder() ;
		String str = this.packToStr();
		byte[] bs = str.getBytes() ;
		outputs.write(bs);
		outputs.flush();
		return str ;
	}
	
	protected abstract void packContent(StringBuilder sb) ;
	
	
	protected abstract AMMsgResp newRespInstance() ;
	
	public AMMsgResp readRespFrom(InputStream inputs,OutputStream outputs,long read_to,int retry_c) throws Exception
	{
		String txt = readFrom(inputs,outputs,read_to,retry_c) ;
		return parseFromTxt(txt);
		
	}
	
	AMMsgResp parseFromTxt(String txt) throws Exception
	{
		AMMsgResp ret = newRespInstance() ;
		ret.parseFrom(txt) ;
		return ret ;
	}
	
	
	private String readFrom(InputStream inputs,OutputStream outputs,long read_to,int retry_c) throws Exception
	{
		String firstpk = readRespPk(true,inputs, read_to,retry_c) ;
		if(!firstpk.endsWith("&"))
		{// single pk
			int len = firstpk.length() ;
			String fcs = calBCC(firstpk.substring(0,len-2)) ;
			if(fcs.charAt(0)!=firstpk.charAt(len-2) || fcs.charAt(1)!=firstpk.charAt(len-1))
				return null ;
			
			return firstpk.substring(0,len-2) ;
		}
		
		//multi-frame
		byte[] ackpk= this.packAckStr().getBytes() ;
		ArrayList<String> more_pks = new ArrayList<>() ;
		more_pks.add(firstpk) ;
		String mstr ;
		do
		{
			outputs.write(ackpk);
			mstr = readRespPk(false,inputs, read_to,retry_c) ;
			more_pks.add(mstr) ;
		}while(mstr.endsWith("&")) ;
		
		StringBuilder sb = new StringBuilder() ;
		
		int len ;//= firstpk.length() ;
		String fcs ;//= calBCC(firstpk.substring(0,len-2)) ;
//		if(fcs.charAt(0)!=firstpk.charAt(len-2) || fcs.charAt(1)!=firstpk.charAt(len-1))
//			return null ;
//		 sb.append(firstpk.substring(0,len-2)) ;
		
		 int n = more_pks.size() ;
		 for(int i = 0 ; i < n -1 ; i ++)
		 {
			 String morepk = more_pks.get(i) ;
			 len = morepk.length() ;
			 fcs = calBCC(morepk.substring(0,len-3)) ;
			if(fcs.charAt(0)!=morepk.charAt(len-3) || fcs.charAt(1)!=morepk.charAt(len-2))
				return null ;
			sb.append(morepk.substring(0,len-3)) ;
		 }
		 
		 String lastpk = more_pks.get(n-1) ;
		 len = lastpk.length() ;
		fcs = calBCC(lastpk.substring(0,len-2)) ;
			if(fcs.charAt(0)!=lastpk.charAt(len-2) || fcs.charAt(1)!=lastpk.charAt(len-1))
				return null ;
			
		sb.append(lastpk.substring(0,len-2)) ;
		return sb.toString() ;
	}
	
	
	private transient int readToCC = 0 ;

	private int readTo(InputStream inputs,long timeout,int retry_c) throws AMException,IOException
	{
		long st = System.currentTimeMillis() ;
		
		while(inputs.available()<=0)
		{
			if(System.currentTimeMillis()-st>timeout)
			{
				readToCC ++ ;
				if(readToCC>retry_c)
				{
					readToCC = 0 ;
					throw new AMException(AMException.ERR_TIMEOUT_SERIOUS,"time out "+timeout+"ms. may be this value is too small!") ;
				}
				else
					throw new AMException(AMException.ERR_TIMEOUT_NOR,"time out "+timeout+"ms. may be this value is too small!") ;
			}
			
			try
			{
			Thread.sleep(1);
			}
			catch(Exception e) {}
		}
		
		readToCC = 0 ;
		
		return inputs.read() ;
	}
	
	/**
	 * read string start with @ and end with \r 
	 * @param inputs
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	private String readRespPk(boolean bfirst,InputStream inputs,long timeout,int retry_c) throws Exception
	{
		boolean in_pk = !bfirst ;
		char tmph ;
		int max_len = 2048 ;
		int c ;
		StringBuilder sb = new StringBuilder() ;
		while(true)
		{
			c = readTo(inputs,timeout,retry_c);
			if(!in_pk)
			{
				if(c!='%' && c!='<')
					continue ;
				in_pk = true ;
				tmph = (char)c ;
				max_len = (tmph=='%'?118:2048) ;
				sb.append((char)c) ;
				continue ;
			}
			
			
			if(c!='\r')
			{
				sb.append((char)c) ;
				if(sb.length()>max_len)
					throw new IOException("read txt len >"+max_len) ;
				continue ;
			}
			
			return sb.toString() ;
		}
	}
}
