package org.iottree.driver.s7.ppi;

/**
 * read Txx Cxx
 *  0   1   2   3   4  5   6   7   8   9  10 11 12 13 14 15 16 17 18 19 20 21    22 23  24  25  26  27  28 29 30   31    32
 * 68 1B 1B 68 02 00 6C 32 01 00 00 00 00 00 0E 00 00 04 01 12 0A 10    1F 00   03  00  00  1F  00 00 20 41 16  req
 * 68 24 24 68 00 02 08 32 03 00 00 00 00 00 02 00 13 00 00 04 01 FF      09 00   0F  02  00  00 7F FF 00 00 00 00 00 00 00 00 00 00 F0 16  resp
 * 
 * 68 1B 1B 68 02 00 6C 32 01 00 00 00 00 00 0E 00 00 04 01 12 0A 10    1F 00 04 00 00 1F 00  00 28 4A 16
 * 68 29 29 68 00 02 08 32 03 00 00  00 00 00 02 00 18 00 00 04 01 FF     09 00 14 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 7A 16
 * 68 29 29 68 00 02 08 32 03 00 00 00 00 00 02 00 18 00 00 04 01 FF      09 00 14 00 00 00 00 15 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 8F 16
 * sd  le      sd DA SA FC          CC              GU                                       VT     RC St  stv---------- st  stc ---------- st  stv---------   st stv----------  FCS  ED
 * 
 * RC read num (1byte)
 * st  Tx status (1byte)
 * stv Tx value  (4byte)
 * @author jason.zhu
 *
 */
public class PPIMsgReqRTC extends PPIMsgReq
{
	int offset ;
	short readNum =1;
	
	public PPIMsgReqRTC withTick(int offset,short readnum)
	{
		this.offset = offset;
		this.readNum = readnum ;
		return this;
	}
	
	public PPIMsgReqRTC withMemTp(PPIMemTp memtp)
	{
		if(memtp!=PPIMemTp.T && memtp!=PPIMemTp.C)
			throw new IllegalArgumentException("invalid mem tp") ;
		this.memTp = memtp ;
		return this ;
	}
	
	@Override
	protected short getStartD()
	{
		return SD_REQ;
	}
	
	public short getFC()
	{
		return 0x6C ;
	}
	
	
	public int getRetOffsetBytes()
	{
		if(this.memTp==PPIMemTp.T)
			return this.offset*4 ;
		else if(this.memTp==PPIMemTp.C)
			return this.offset*2 ;
		else
			return -1 ;
	}
	
	private short calLen()
	{
		return 27;
	}
	
	@Override
	public byte[] toBytes()
	{
		short le =  calLen() ;
		byte[] rets = new byte[le+6];
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
		rets[15] = 0x00;
		rets[16] = 0x00;
		rets[17] = 0x04;
		rets[18] = 0x01;
		rets[19] = 0x12;
		rets[20] = 0x0A;
		rets[21] = 0x10;
		//
		rets[22] = (byte)memTp.getVal() ;
		rets[23] = 0x0;
		rets[24] = (byte)this.readNum;
		rets[25] = 0x0;
		
		rets[26] = 0x0; //other
		rets[27] = (byte)memTp.getVal();
		
		int offaddr = offset;
		rets[28] = (byte)((offaddr >> 16) & 0xFF) ;
		rets[29] = (byte)((offaddr >> 8) & 0xFF) ;
		rets[30] = (byte)(offaddr & 0xFF) ;
		
		rets[31] = calChkSum(rets,4,27) ;
		rets[32] = 0x16 ;//end
		
		return rets ;
	}
}
