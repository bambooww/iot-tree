package org.iottree.driver.gb.szy;

public abstract class SZYMsg
{
	short len ;
	
	byte[] data ;
	
	public SZYMsg(byte[] data)
	{
		if(data.length>255)
			throw new IllegalArgumentException("data len too long") ;
		this.len = (short)data.length ;
		this.data = data ;
	}
	
	public short getLen()
	{
		return this.len ;
	}
	
	public byte[] getData()
	{
		return data ;
	}
	
	//protected abstract boolean parseData(byte[] bs) ;
	
	public byte[] packTo()
	{
		byte[] ret = new byte[len+5] ;
		ret[0] = ret[2] =  0x68 ;
		ret[1] = (byte)(len & 0xFF) ;
		
		System.arraycopy(data, 0, ret, 3, len);
		ret[len+3] = calcCrc8(data,0,data.length) ;
		ret[len+4] = 0x16 ;
		return ret ;
	}
	
	
	/**
	 * 多项式 X^7+X^6+X^5+X^2+1 (0xE5)
	 * 
	 * @param str
	 * @return
	 */
	static byte calcCrc8(byte[] data,int offset,int len)
	{
		int crc = 0x00;
		int dxs = 0xE5;
		// int hibyte;
		int sbit;
		for (int i = 0 ; i < len ; i ++)
		{
			byte datum = data[i+offset] ;
			crc = crc ^ datum;
			for (int j = 0; j < 8; j++)
			{
				sbit = crc & 0x80;
				crc = crc << 1;
				if (sbit != 0)
				{
					crc ^= dxs;
				}
			}
		}
		return (byte)crc;
	}

	
	static String transBCD2Str(byte[] bcd,int offset,int len)
	{
		StringBuffer sb = new StringBuffer();
		
		for (int i = offset; i < len; i++)
		{
			sb.append((bcd[i] & 0XF0) >> 4);
			sb.append(bcd[i] & 0X0F);
		}
		
//		// 如果转化后的字符串首字母为0，那么去掉
//		if (sb.charAt(0) == '0') {
//			decStr = sb.substring(1);
//		}else {
//			decStr = sb.toString();
//		}
		
		return sb.toString();
	}
}
