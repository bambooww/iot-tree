package org.iottree.driver.s7.ppi;

import org.iottree.core.UAVal.ValTP;

/**
 * 
 *                        0   1   2   3   4  5   6   7   8   9  10 11 12 13 14 15 16 17 18 19 20 21    22 23  24  25  26    27  28 29 30   31    32 33 34 35  --          FCS ED
 * QB0=FF           68 20 20 68 02 00 7C 32 01 00 00 00 00 00 0E 00 05 05 01 12 0A 10    02 00  01  00  00    82  00 00 00   00    04 00 08 FF 86 16
 * 	                      
 * VB100=12       68 20 20 68 02 00 7C 32 01 00 00 00 00 00 0E 00 05 05 01 12 0A 10    02 00  01 00   01    84  00 03 20    00    04 00 08 12 BF  16
 * VW100=1234   68 21 21 68 02 00 7C 32 01 00 00 00 00 00 0E 00 06 05 01 12 0A 10    04 00  01 00  01     84 00 03 20    00    04  00  10 12 34 FE 16
 *                       68 20 20 68 02 00 7C 32 01 00 00 00 00 00 0E 00 05 05 01 12 0A 10    01 00 01 00 00       82 00 00 00 00 03 00 01 01 7F 16
 *                       sd  le      sd DA SA FC          CC              GU                                       VT      RC     MT    MC  OFFSET                                    FCS  ED
 * 
 * 
 * @author jason.zhu
 *
 */
public class PPIMsgReqW extends PPIMsgReq
{
	PPIMemValTp memValTp = null ;
	
	int offsetBytes = 0 ;
	
	int inBit = -1 ;//-1 is null
	/**
	 * write val
	 * bit  true=wVal>0
	 * 
	 */
	int wVal = 0 ;
	
	public PPIMsgReqW withWriteVal(PPIMemValTp valtp,int wv)
	{
		this.memValTp = valtp ;
		this.wVal = wv ;
		return this ;
	}
	
	public PPIMsgReqW withWriteVal(PPIAddr addr,int wv)
	{
		this.memValTp = addr.getFitMemValTp() ; 
		if(this.memValTp==null)
			throw new IllegalArgumentException("no fit mem val tp with addr="+addr);
		this.wVal = wv ;
		return this ;
	}
	
	@Override
	protected short getStartD()
	{
		return 0x68;
	}
	
	public short getFC()
	{
		return 0x7C ;
	}
	

	public int getInBits()
	{
		return this.inBit ;
	}

	public int getOffsetBits()
	{
		return this.offsetBytes*8 + (this.inBit>=0?this.inBit:0) ;
	}
	
	public boolean isBitReq()
	{
		return this.inBit>=0;
	}
	
	public int getRetOffsetBytes()
	{
		return -1 ;
	}
	
	public PPIMsgReqW withAddr(String addr,ValTP vtp)
	{
		StringBuilder failedr = new StringBuilder() ;
		PPIAddr paddr = PPIAddr.parsePPIAddr(addr,vtp,failedr) ;
		if(paddr!=null)
		{
			this.memTp = paddr.getMemTp() ;
			this.offsetBytes = paddr.getOffsetBytes() ;
			this.inBit = paddr.getInBits() ;
			//this.readNum = paddr.getBytesNum() ;
		}
		return this;
	}
	
	public PPIMsgReq withAddrByte(PPIMemTp mtp, int byteoffsets,int inbit)
	{
		this.memTp = mtp ;
		this.offsetBytes = byteoffsets;
		this.inBit = inbit ;
		//this.readNum = bytesnum ;
		return this;
	}
	
	private short calLen()
	{
		PPIMemValTp vtp = this.memValTp;//ppiAddr.getMemValTp();
		int byten = vtp.getByteNum() ;
		return (short)(byten+31) ;
	}
	
	@Override
	public byte[] toBytes()
	{
		short le =  calLen() ;
		byte[] rets = new byte[le+6];
		PPIMemValTp vtp = memValTp;//ppiAddr.getMemValTp() ;
		PPIMemTp mtp = this.memTp;
		rets[0] = rets[3] = (byte)getStartD();
		rets[1] = rets[2] = (byte)le;
		rets[4] = (byte)da ;
		rets[5] = (byte)sa;
		rets[6] = (byte)getFC();
		rets[7] = 0x32;
		rets[8] = 0x01;
		rets[9] = rets[10] = rets[11] = rets[12] = 0x0;
		rets[13] = 0x0;
		rets[14] = 0x0E;
		rets[15] = 0x00; //dlen
		rets[16] = (byte)(0x04+vtp.getByteNum());
		rets[17] = 0x05;
		rets[18] = 0x01;
		rets[19] = 0x12;
		rets[20] = 0x0A;
		rets[21] = 0x10;
		//
		rets[22] = (byte)vtp.getVal() ;
		rets[23] = 0x0;
		rets[24] = (byte)0x01;
		rets[25] = 0x0;
		
		
		if(mtp==PPIMemTp.V)
			rets[26] = 0x01; //other
		else
			rets[26] = 0x0; //other
		rets[27] = (byte)mtp.getVal();
		
		int offaddr = this.getOffsetBits();
		rets[28] = (byte)((offaddr >> 16) & 0xFF) ;
		rets[29] = (byte)((offaddr >> 8) & 0xFF) ;
		rets[30] = (byte)(offaddr & 0xFF) ;
		
		
		rets[31] = 0x00 ;
		int dend = 35 ;
		if(mtp.hasBit() && isBitReq())
		{
			rets[32] = 0x03 ;
			rets[33] = 0x00 ;
			rets[34] = 0x01 ;
			rets[35] = (byte)this.wVal;//(byte)((this.wVal>0)?0xFF:0) ;//bit val
		}
		else
		{
			rets[32] = 0x04 ;
			rets[33] = 0x00 ;
			rets[34] = (byte)vtp.getBitNum() ;
			switch(vtp)
			{
			case B:
				rets[35] = (byte)(this.wVal & 0xFF);
				break ;
			case W:
				rets[35] = (byte) ((this.wVal>>8) & 0xFF) ;
				rets[36] = (byte)(this.wVal & 0xFF);
				dend = 36 ;
				break;
			case D:
				rets[35] = (byte) ((this.wVal>>24) & 0xFF) ;
				rets[36] = (byte) ((this.wVal>>16) & 0xFF) ;
				rets[37] = (byte) ((this.wVal>>8) & 0xFF) ;
				rets[38] = (byte)(this.wVal & 0xFF);
				dend = 38 ;
				break;
			}
		}

		rets[dend+1]= calChkSum(rets,4,dend-4+1) ;
		rets[dend+2] = 0x16 ;//end
		
		return rets ;
	}

}
