package org.iottree.driver.s7.ppi;

import java.io.IOException;
import java.io.InputStream;

import org.iottree.core.util.Convert;

/**
 * read write response
 * 
 * SD  LE  LER SD DA SA FC CC GU DU FCS ED
 * 
 * 0   1   2   3   4  5   6   7   8   9  10 11 12 13 14 15 16 17 18 19 20 21    22 23  24  25  26   27  28  29  30   31    32
 * 68 18 18 68 00 02 08 32 03  00 00 00 00 00 02 00 07 00 00 04 01 FF    04 00  18  99  34   56  8B  16
 * 68 1B 1B 68 00 02 08 32 03 00 00 00 00 00 02 00 0A 00 00 04 01 FF    04 00  30  00  00 00 00 00 00 83 16
 * 68 17  17 68 00 02 08 32 03 00 00 00 00 00 02 00 06 00 00 04 01 FF    04 00  10  3F 03                     A1 16
 * 68 16 16 68 00 02 08 32 03 00 00 00 00 00 02 00 05 00 00 04 01  FF    03 00   01 00 4E 16   read q0.1
 * 68 16 16 68 00 02 08 32 03 00 00 00 00 00 02 00 05 00 00 04 01 FF     03 00   01 01 4F 16
 * sd  le      sd DA SA FC          CC                           L                            VT bitnum  d------------------  FCS  ED

 * L data len [21-(FCS-1)]
 * VT (value type)  Bit=01  B=02 W=04 D=06
 * 
 * @author jason.zhu
 */
public class PPIMsgRespR extends PPIMsg
{
	short da; //destination address  1byte
	
	short sa=0; //soruce address  1byte
	
	
	short fc = 0x08; 
	
	int byteNum ;
	
	int bitNum ;
	
	byte[] respBS = null ;
	
	public PPIMsgRespR(byte[] bs)
	{
		
	}
	
	public static PPIMsgRespR parseFromBS(byte[] bs,StringBuilder failedr)
	{
		PPIMsgRespR ret = new PPIMsgRespR(bs) ;
		ret.da = (short)((bs[4]) & 0xFF) ;
		ret.sa = (short)((bs[5]) & 0xFF) ;
		if(ret.fc!= (short)((bs[6]) & 0xFF))
		{
			failedr.append("fucntion code err") ;
			return null ;
		}
		
		if(bs[22]!=0x04)
		{
			failedr.append("not read bytes") ;
			return null ;
		}
		
		int byte_num = (bs[16] & 0xFF) - 4 ;
		if(byte_num<=0)
		{
			failedr.append("read bytes number is <=0") ;
			return null ;
		}
		
		int bit_num = (bs[23] & 0xFF)<<8 ;
		bit_num +=  (bs[24] & 0xFF) ;
		
		ret.byteNum = byte_num ;
		ret.bitNum = bit_num ;
		ret.respBS = new byte[byte_num] ;
		System.arraycopy(bs, 25, ret.respBS, 0, byte_num);
		return ret ;
	}
	
	public static PPIMsgRespR parseFromStream(InputStream inputs,long timeout,StringBuilder failedr) throws IOException
	{
		byte[] bs = readFromStream(inputs, timeout) ;
		if(bs==null)
		{
			failedr.append("read ppi msg err") ;
			return null ;
		}
		
		if(bs.length==1)
			return null ;// may be 0xE5 ;
		
		//String tmps = Convert.byteArray2HexStr(bs, " ") ;
		//System.out.println("<-"+tmps);
		return parseFromBS(bs,failedr) ;
	}


	@Override
	protected short getStartD()
	{
		return SD_REQ;
	}
	
	public short getDestAddr()
	{
		return da ;
	}

	public short getSorAddr()
	{
		return sa ;
	}

	public int getByteNum()
	{
		return this.byteNum ;
	}
	
	public int getBitNum()
	{
		return this.bitNum ;
	}
	
	public byte[] getRespData()
	{
		return respBS;
	}
	
	@Override
	public byte[] toBytes()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public String toString()
	{
		return sa+"->"+da+" ["+Convert.byteArray2HexStr(respBS, " ")+"]" ;
	}
}
