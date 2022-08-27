package org.iottree.core.basic;

import java.util.*;

import org.iottree.core.util.xmldata.DataUtil;
//import org.iottree.core.DevAddr;
import org.iottree.core.UAVal;

/**
 * A memory table may be used to map a device's data space
 * 1)by address,memory table can set data ,and update some address mem space
 * 2)   can be read by outer,with default value which never be writed
 * 
 * @author jason.zhu
 */
public class MemTable<T extends MemSeg>
{
	int addrBits = 8 ;
	int addrBS ;
	long maxSize = 1024;//default 1k
	
	LinkedList<T> segs = new LinkedList<>();
	
	/**
	 * 
	 * @param addr_bits 8 16 32 64 address access bits number
	 * @param maxsize  addr_bits*maxsize/8
	 */
	public MemTable(int addr_bits,long maxsize)
	{
		if(addr_bits<8||addr_bits>64)
			throw new IllegalArgumentException("invalid addr_bits") ;
		addrBits = addr_bits ;
		addrBS = addr_bits/8 ;
		maxSize = maxsize ;
	}
	
	private List<T> findHitSegs(long idx,int len,int[] ahead_tail)
	{
		ArrayList<T> r = new ArrayList<>() ;
		
		int ahead = -1 ;
		int tail = -1 ;
		int curidx = -1 ;
		for(T ms:segs)
		{
			curidx ++ ;
			if(r.size()<=0)
			{//not find yet
				if(ms.idx+ms.len<idx)
				{
					ahead = curidx;
					continue ;
				}
				//maybe find or end
				if(idx+len<ms.idx)
				{
					tail = curidx ;
					break;//end
				}
				r.add(ms) ;
			}
			else
			{// git
				//maybe find or end
				if(idx+len<ms.idx)
				{
					tail = curidx ;
					break ;//end
				}
				r.add(ms) ;
			}
		}
		ahead_tail[0] = ahead;
		ahead_tail[1] = tail ;
		return r ;
	}
	
	/**
	 * get seg and write
	 * so it may assign mem or merge
	 * @param idx
	 * @param len
	 * @return
	 */
	synchronized private T acquireMemSeg(long idx,int len)
	{
		if(idx<0||idx+len>maxSize)
			throw new IllegalArgumentException("out of memory table boundary");
		
		int[] ahead_tail = new int[] {-1,-1} ;
		List<T> hitsegs = findHitSegs(idx,len,ahead_tail);
		T s0 = null;
		if(hitsegs.size()==1)
		{
			s0 = hitsegs.get(0) ;
			if(s0.idx<=idx&&s0.idx+s0.len>=idx+len)
				return s0 ;//contained by seg
		}
		
		if(hitsegs.size()<=0)
		{//create new seg
			@SuppressWarnings("unchecked")
			T seg = (T)MemSeg.createInstance(idx, len, addrBits);//new T(idx,len) ;
			if(ahead_tail[1]>=0)
				segs.add(ahead_tail[1], seg);
			else
				segs.add(seg) ;
			return seg;
		}
		
		s0 = hitsegs.get(0) ;
		//merge hits segs with new idx len
		
		if(idx>s0.idx)
		{
			len += idx-s0.idx ;
			idx = s0.idx ;
		}
		T se = hitsegs.get(hitsegs.size()-1) ;
		if(idx+len<se.idx+se.len)
			len = (int)(se.idx+se.len - idx) ;
		@SuppressWarnings("unchecked")
		T seg = (T)MemSeg.createInstance(idx, len, addrBits);//new MemSeg8(idx,len) ;
		//merge to new seg
		for(T hitseg:hitsegs)
		{
			System.arraycopy(hitseg.getBuf(),0, seg.getBuf(),(int)(hitseg.idx-seg.idx), hitseg.len);
		}
		//replace new seg to hit segs
		if(ahead_tail[1]>=0)
			segs.add(ahead_tail[1], seg);
		else
			segs.add(seg) ;
		for(T hitseg:hitsegs)
		{
			segs.remove(hitseg) ;
		}
		return seg ;
	}
	
	private T acquireMemSeg(UAVal.ValTP tp,long idx)
	{
		int len = 1 ;
		switch(tp)
		{
		case vt_byte:
		case vt_char:
		case vt_uint8:
			break ;
		case vt_int16:
		case vt_uint16:
			if(addrBits<16)
				len = 2 ;
			break ;
		case vt_int32:
		case vt_uint32:
		case vt_float:
			len = 32/addrBits ;
			if(len==0)
				len = 1 ;
			break ;
		
		case vt_int64:
		case vt_uint64:
		case vt_double:
		case vt_date:
			len = 64/addrBits ;
			if(len==0)
				len = 1 ;
			break ;
		
		case vt_str:
		
		default:
			throw new IllegalArgumentException("not support vt="+tp.getStr());
		}
		return acquireMemSeg(idx,len) ;
	}
	
	
	public void setValBlock(long idx,int len,byte[] bsdata,int bs_offset)
	{
		T ms = acquireMemSeg(idx,len);
		ms.setValBlock(idx, bsdata, bs_offset, len);
	}
	
	public void setValBool(long idx,int bitpos,boolean v)
	{
		T ms = acquireMemSeg(idx,1) ;
		ms.setValBool(idx,bitpos, v);
	}
	
	public boolean getValBool(long idx,int bitpos)
	{
		T ms = acquireMemSeg(idx,1) ;
		return ms.getValBool(idx,bitpos);
	}
	
	public void setValNumber(UAVal.ValTP tp,long idx,Number v)
	{
		T ms = acquireMemSeg(tp,idx) ;
		ms.setValNumber(tp,idx, v,ByteOrder.LittleEndian);
	}
	
	public Number getValNumber(UAVal.ValTP tp,long idx)
	{
		T ms = acquireMemSeg(tp,idx) ;
		return ms.getValNumber(tp,idx,ByteOrder.LittleEndian);
	}
	
	public int getValInt32(UAVal.ValTP tp,long idx)
	{
		Number n = getValNumber(tp,idx);
		return n.intValue() ;
	}
	
	public String toSegsStr()
	{
		StringBuilder sb=  new StringBuilder() ;
		for(MemSeg s:segs)
			sb.append(s);
		return sb.toString() ;
	}
}
