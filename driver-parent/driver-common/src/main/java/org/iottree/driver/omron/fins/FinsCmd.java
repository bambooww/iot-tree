package org.iottree.driver.omron.fins;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.iottree.core.util.IBSOutput;
import org.iottree.core.util.xmldata.DataUtil;

public abstract class FinsCmd
{
	private static final byte[] HEADER = new byte[] {'F','I','N','S'} ;
	
	protected FinsMode mode ; 

	short dna = -1 ; //desction network addr 
	
	short da1 = -1 ;
	
	short da2 = -1 ;
	
	short sna = -1 ;
	
	short sa1 = -1 ;
	
	short sa2 = -1 ;
	
	
	public FinsCmd(FinsMode fins_mode)
	{
		this.mode = fins_mode ;
	}
	
	/**
	 * dna - network address
	 * 
		 * 1 to 127 (01 to 7F Hex)
			Local node address: 00 Hex
	 * 
	 * da1  node address
	 * 
	 * 		1 to 254 (01 to FE Hex) (See note.)
			Note The node addresses differ for each
			network.
			Internal Communications in PLC: 00 Hex
			For Controller Link: 01 to 3E Hex (1 to 62)
			For Ethernet Units with model numbers
			ending in ETN21: 01 to FE Hex (1 to 254)
			For Ethernet Units with other model
			numbers: 01 to 7E Hex (1 to 126)
	 * 
	 * da2 unit address:
	 * 
		  	CPU Unit: 00 Hex
			•CPU Bus Unit: Unit No.+ 10 Hex
			•Special I/O Unit: Unit No.+ 20 Hex
			•Inner Board: E1 Hex
			•Computer: 01 Hex
			•Unit connected to network: FE Hex
	 * @param dna
	 * @param da1
	 * @param da2
	 * @return
	 */
	public FinsCmd asDest(short dna,short da1,short da2)
	{
		this.dna = dna ;
		this.da1 = da1 ;
		this.da2 = da2 ;
		return this ;
	}
	
	public FinsCmd asSor(short sna,short sa1,short sa2)
	{
		this.sna = sna ;
		this.sa1 = sa1 ;
		this.sa2 = sa2 ;
		return this ;
	}
	
	public void writeOut(IBSOutput outputs) throws IOException
	{
		//outputs.write(HEADER);
		
		int len = 20 ;//+ this.getParamBytesNum() ;
		byte[] bs = int2bytes(len) ;
		outputs.write(bs);
		
		bs = int2bytes(2) ; //command
		outputs.write(bs);
		bs[0]=bs[1]=bs[2]=bs[3] = 0 ;// error code
		outputs.write(bs);
		
		bs = new byte[12] ;
		
		bs[0] = (byte)getICF() ; //ICF
		bs[1] = 0; //RSV
		bs[2] = 2; //GCT
		bs[3] = (byte)this.dna; //DNA
		bs[4] = (byte)this.da1; //DA1  target client_last_ip  PLC/PC
		bs[5] = (byte)this.da2 ; //DA2
		bs[6] = (byte)this.sna ;//SNA
		bs[7] = (byte)this.sa1; //SA1 source client last ip  PLC/PC
		bs[8] = (byte)this.sa2 ;
		bs[9] = 0; //SID
		
		outputs.write(bs);
		
		
		this.writeParam(outputs);
	}
	
	/**
	 * int tmpi = isNeedResp()?0:1 ;
		if(!isReqOrResp())
			tmpi |= 0x80 ;
	 * @return
	 */
	protected abstract short getICF() ;
	
	/**
	 * get main request code
	 * @return
	 */
	protected abstract short getMRC() ;
	
	/**
	 * get second request code
	 * @return
	 */
	protected abstract short getSRC() ;
	
//	protected abstract boolean isNeedResp() ;
//	/**
//	 * true=request  false=response
//	 * @return
//	 */
//	protected abstract boolean isReqOrResp() ;
	//public abstract int getCommand() ;
	
	
	
	protected abstract int getParamBytesNum() ;
	
	protected abstract void writeParam(IBSOutput outputs) ;//throws IOException ;
	
	// -- util func
	
	protected static final byte[] int2bytes(int i)
	{
		return DataUtil.intToBytes(i) ;
	}
	
	protected static final void int2byte3(int i,byte[] bytes,int offset)
	{
		bytes[offset+3] = (byte) (i & 0xFF);
		i = i >>> 8;
		bytes[offset+2] = (byte) (i & 0xFF);
		i = i >>> 8;
		bytes[offset+1] = (byte) (i & 0xFF);
		//i = i >>> 8;
		//bytes[offset] = (byte) (i & 0xFF);
		
	}
	
	protected static final byte[] short2bytes(short i)
	{
		return DataUtil.shortToBytes(i) ;
	}
	
	protected static final void short2bytes(short i,byte[] bs,int offset)
	{
		DataUtil.shortToBytes(i,bs,offset) ;
	}
	
	//----  Handshake support
	
	public static byte[] Handshake_createReq(short client_pc_last_ip)
	{
		byte[] rets = new byte[20] ;
		rets[0]='F' ;
		rets[1]='I' ;
		rets[2]='N' ;
		rets[3]='S' ;
		for(int i = 4; i< 20 ; i ++)
			rets[i]=0 ;
		rets[7]=0x0C;
		rets[19] = (byte)client_pc_last_ip ;
		return rets ;
	}
	
	/**
	 * failed may time out IOException.or return null ;
	 */
	public static Short Handshake_checkResp(InputStream inputs,short client_pc_last_ip,long timeout) throws IOException
	{
		checkStreamLenTimeout(inputs, 24, timeout);
		
		byte[] bs = new byte[24] ;
		inputs.read(bs) ;
		
		if(bs[0]!='F' || bs[1]!='I' || bs[2] != 'N' || bs[3]!='S')
			return null ;
		if(bs[7]!=0x10)
			return null ;
		if( (0xFF & bs[19])!=client_pc_last_ip)
			return null ;
		return (short)(0xFF & bs[23]) ;
	}
	
	protected static void checkStreamLenTimeout(InputStream inputs,int len,long timeout) throws IOException
	{
		long lastt = System.currentTimeMillis() ;
		int lastlen = inputs.available() ;
		long curt ;
		while((curt=System.currentTimeMillis())-lastt<timeout)
		{
			int curlen = inputs.available();
			if(curlen>=len)
				return  ;
			
			if(curlen>lastlen)
			{
				lastlen = curlen ;
				lastt = curt;
				continue ;
			}
			
			try
			{
				Thread.sleep(1);
			}
			catch(Exception ee) {}
			
			continue ;
		}
		throw new IOException("time out") ;
	}
	
	public static int readCharTimeout(InputStream inputs,long timeout) throws IOException
	{
		long curt = System.currentTimeMillis() ;
		
		while(System.currentTimeMillis()-curt<timeout)
		{
			if(inputs.available()<1)
			{
				try
				{
					Thread.sleep(1);
				}
				catch(Exception ee) {}
				
				continue ;
			}
			
			return inputs.read() ;
		}
		
		throw new IOException("time out "+timeout+"ms") ;
	}
	
	
	
	public static void clearInputStream(InputStream inputs,long timeout) throws IOException
	{
		int lastav = inputs.available() ;
		long lastt = System.currentTimeMillis() ;
		long curt = lastt; 
		while((curt=System.currentTimeMillis())-lastt<timeout)
		{
			try
			{
			Thread.sleep(1);
			}
			catch(Exception e) {}
			
			int curav = inputs.available() ;
			if(curav!=lastav)
			{
				lastt = curt ;
				lastav = curav ;
				continue ;
			}
		}
		
		if(lastav>0)
			inputs.skip(lastav) ;
	}
	
}
