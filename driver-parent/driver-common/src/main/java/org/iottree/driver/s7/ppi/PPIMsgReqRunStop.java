package org.iottree.driver.s7.ppi;

/**
 *             sd  le      sd DA SA FC          CC              GU                                       VT     RC     MT  MC  OFFSET     FCS  ED
 *              0   1   2   3   4  5   6   7   8   9  10 11 12 13 14 15 16 17 18 19 20 21 22 23  24  25  26    27  28 29 30   31    32
 * stop      68 1D 1D 68 02 00 6C 32 01 00 00 00 00 00 10 00 00 29 00 00 00 00 00 09 50 5F 50 52 4F 47 52 41 4D AA 16
 *             E5
 *             10 02 00 5C 5E 16
 *  resp     68 10 10 68 00 02 08 32 03 00 00 00 00 00 01 00 00 00 00 29 69 16
 *  
 *  run       68 21 21 68 02 00 6C 32 01 00 00 00 00 00 14 00 00 28 00 00 00 00 00 00 FD 00 00 09 50 5F 50 52 4F 47 52 41 4D AA 16
 *              E5
 *              10 02 00 5C 5E 16
 *              68 10 10 68 00 02 08 32 03 00 00 00 00 00 01 00 00 00 00 29 69 16
 * @author jason.zhu
 *
 */
public class PPIMsgReqRunStop extends PPIMsgReq
{

	@Override
	public short getFC()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRetOffsetBytes()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected short getStartD()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] toBytes()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
