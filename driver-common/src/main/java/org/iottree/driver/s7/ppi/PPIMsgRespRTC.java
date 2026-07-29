package org.iottree.driver.s7.ppi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.iottree.core.util.Convert;

/**
 * read T/C write response
 * 
 * 
 * @author jason.zhu
 */
public class PPIMsgRespRTC extends PPIMsgResp
{
	public static class TItem
	{
		public int st = -1 ;
		
		public long val = -1 ;
	}
	
	public static class CItem
	{
		public int st = -1 ;
		
		public int val = -1 ;
	}
	
	PPIMemTp memTp ;
	
	short da; //destination address  1byte
	
	short sa=0; //soruce address  1byte
	
	short fc = 0x08; 

	ArrayList<TItem> t_items = null;
	
	ArrayList<CItem> c_items = null;
	
	private byte[] retData = null ;
	
	public PPIMsgRespRTC(PPIMemTp mtp)//(byte[] bs)
	{
		this.memTp = mtp ;
	}
	
	public static PPIMsgRespRTC parseFromBS(PPIMemTp memtp,byte[] bs,StringBuilder failedr)
	{
		PPIMsgRespRTC ret = new PPIMsgRespRTC(memtp) ;
		ret.da = (short)((bs[4]) & 0xFF) ;
		ret.sa = (short)((bs[5]) & 0xFF) ;
		if(ret.fc!= (short)((bs[6]) & 0xFF))
		{
			failedr.append("fucntion code err") ;
			return null ;
		}
		
		
		if(bs[22]!=0x09)
		{
			failedr.append("not read bytes") ;
			return null ;
		}
		
		int retnum = bs[24] & 0xFF;
		
		
		
		if(memtp==PPIMemTp.T)
		{
			retnum /= 5 ;
			ret.t_items = new ArrayList<>(retnum);
			ret.retData = new byte[retnum*4] ;
			for(int i = 0 ; i < retnum ; i ++)
			{
				TItem ti = new TItem() ;
				ti.st = bs[25+i*5] & 0xFF;
				long v = ((long)(bs[25+i*5+1] << 24)) & 0xFFFFFFFF ;
				v += (bs[25+i*5+2] << 16) & 0xFFFFFF ;
				v += (bs[25+i*5+3] << 8) & 0xFFFF ;
				v += (bs[25+i*5+4]) & 0xFF ;
				ti.val = v ;
				System.arraycopy(bs,25+i*5+1,ret.retData,i*4,4) ;
				ret.t_items.add(ti) ;
			}
		}
		else if(memtp==PPIMemTp.C)
		{
			retnum /= 3 ;
			ret.c_items = new ArrayList<>(retnum);
			ret.retData = new byte[retnum*2] ;
			for(int i = 0 ; i < retnum ; i ++)
			{
				CItem ti = new CItem() ;
				ti.st = bs[25+i*5] & 0xFF;
				int v = ((bs[25+i*5+1] << 8)) & 0xFFFF ;
				v += (bs[25+i*5+2] ) & 0xFF ;
				ti.val = v ;
				ret.retData[i*4] = bs[25+i*5+1];
				ret.retData[i*4+1] = bs[25+i*5+2];
				ret.c_items.add(ti) ;
			}
		}
		else
			return null ;
		
		return ret ;
	}
	
	public static PPIMsgRespRTC parseFromStream(PPIMemTp memtp,InputStream inputs,long timeout,StringBuilder failedr) throws IOException
	{
		byte[] bs = readFromStream(inputs, timeout) ;
		if(bs==null)
		{
			failedr.append("read ppi msg err") ;
			return null ;
		}
		
		if(bs.length==1)
			return null ;// may be 0xE5 ;
		
		if(log.isTraceEnabled())
		{
			String tmps = Convert.byteArray2HexStr(bs, " ") ;
			log.trace("resp <-"+tmps);
		}
		return parseFromBS(memtp,bs,failedr) ;
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

	@Override
	public byte[] toBytes()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public byte[] getRetData()
	{
		return retData;
	}
	
	public String toString()
	{
		String r = sa+"->"+da+" [";
		if(memTp==PPIMemTp.T)
		{
			for(TItem ti:t_items)
			{
				r += ti.val+" " ;
			}
		}
		else if(memTp==PPIMemTp.C)
		{
			for(CItem ti:c_items)
			{
				r += ti.val+" " ;
			}
		}
		r +="]" ;
		return r;
	}
}
