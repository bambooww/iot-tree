package org.iottree.driver.gb.szy;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 监听接收到的数据，并解析成对应的数据帧
 * @author jason.zhu
 *
 */
public class SZYListener
{
	
	private SZYFrame curSingleF = null ;
	
	private ArrayList<SZYFrame> curMultiFs = null ;
	
	private SZYRecvBufferFix sniBuf = new SZYRecvBufferFix(256) ;
	
	private int curFrameL = -1 ; 
	
	//private SZYFrame curFrame = null ;
	
	public SZYListener()
	{
	}
	
	/**
	 * state machine to parse modbus data
	 * 
	 * @param bs
	 * @throws Exception 
	 */
	public void onRecvedData(byte[] bs,IRecvCallback cb) //throws Exception
	{
		if(bs==null||bs.length<=0)
			return ;
		
		//System.out.println("on sniffed data len="+bs.length) ;
		try
		{
			sniBuf.addData(bs);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		int buflen ;//= sniBuf.getBufLen() ;
		
		while((buflen = sniBuf.getBufLen())>0)
		{
			//baos.
			if(curFrameL<=0)
			{
				if((buflen = sniBuf.getBufLen())<3)
					return ;
				byte[] tmpbs = new byte[3] ;
				sniBuf.peekData(tmpbs, 0, 3) ;
				if(tmpbs[0]!=0x68)
				{
					sniBuf.readNextChar() ;
					continue ;
				}
				if(tmpbs[2]!=0x68)
				{
					sniBuf.readNextChar() ;
					continue ;
				}
				curFrameL = tmpbs[1] & 0xFF ;
				sniBuf.skipLen(3) ;
				continue ;
			}
			else
			{
				if(buflen<curFrameL+2)
				{
					return ;
				}
				byte[] tmpbs = new byte[curFrameL] ;
				byte[] endbs = new byte[2] ;
				sniBuf.readData(tmpbs, 0, curFrameL) ;
				sniBuf.readData(endbs, 0, 2) ;
				if(endbs[1]!=0x16)
					return ;
				byte crc = SZYMsg.calcCrc8(tmpbs,0,curFrameL) ;
				if(crc!=endbs[0])
					return ;//crc error ;
				SZYFrame f = new SZYFrame(tmpbs) ;
				if(f.parseData())
				{
					if(cb!=null)
						cb.onRecvFrame(f);
					//onSnifferCmdFound(f) ;
					//sniBuf.skipLen(resplen) ;
				}
				curFrameL = -1;
			}
		}
	}
	
	
	
}
