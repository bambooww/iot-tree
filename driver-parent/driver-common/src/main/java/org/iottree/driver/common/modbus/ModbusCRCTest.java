package org.iottree.driver.common.modbus;


public class ModbusCRCTest
{
	public static String byteArray2HexStr(byte[] bs,int offset,int len)
	{
		if (bs == null)
			return null;

		if (bs.length == 0 || len<=0)
			return "";

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++)
		{
			int tmpi = 255 ;
			tmpi = tmpi & bs[i+offset] ;
			String s = Integer.toHexString(tmpi);
			if (s.length() == 1)
				s = "0" + s;
			sb.append(s);
		}
		return sb.toString().toUpperCase();
	}
	
	public static void main(String[] args)
		throws Exception
	{
		//if(args.length<1)
		//	return ;
		
//		for(String tmps:args)
//		{
//			System.out.println(">>"+tmps) ;
//		}
		System.out.println("********** 讯源科技 Modbus CRC Creator ***********") ;
	
		
		if(args.length<=0)
		{
			System.out.println("输入命令的格式例子，16进制表示输入值用\"\"包含，并且用空格分开") ;
			System.out.println("如例子  mcrc \"01 03 02 05\"") ;
			return ;
		}
		String[] ss = args[0].split(" ") ;
		byte[] bs = new byte[ss.length+3];
		for(int i=0;i<ss.length;i++)
		{
			bs[i] = (byte)Integer.parseInt(ss[i], 16) ;
		}
		
		
		System.out.println("你的输入值:"+byteArray2HexStr(bs,0,ss.length)) ;
		ModbusCmd.addCRC(bs, ss.length) ;
		
		
		System.out.println("带2字节CRC:"+byteArray2HexStr(bs,0,ss.length+2).toUpperCase()) ;
//		for(int i=0;i<args.length+2;i++)
//		{
//			System.out.print(false)
//		}
	}
}
