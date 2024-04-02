package org.iottree.driver.omron.hostlink;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.DevAddr.IAddrDef;
import org.iottree.core.DevAddr.IAddrDefSeg;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ILang;

public class HLAddrDef implements IAddrDef,ILang
{
	/**
	 * X Y M
	 */
	String prefix = null ;
	
	
	ArrayList<HLAddrSeg> segs = new ArrayList<>() ; 
		
	public HLAddrDef(String prefix)
	{
		this.prefix = prefix ;
		//this.title = title ;
	}
	
	
	
	public HLAddrDef asValTpSeg(HLAddrSeg seg)
	{
		seg.belongTo = this ;
		this.segs.add(seg) ;
		return this ;
	}
	
	public String getPrefix()
	{
		return this.prefix ;
	}
	
//	public String getTitle()
//	{
//		return this.title ;
//	}
	
	public List<HLAddrSeg> getSegs()
	{
		return this.segs ;
	}
	
	/**
	 * @param vtp
	 * @return
	 */
	public List<HLAddrSeg> findSegs(ValTP vtp)
	{
		ArrayList<HLAddrSeg> rets = new ArrayList<>() ;
		for(HLAddrSeg seg:this.segs)
		{
			if(seg.matchValTP(vtp))
			{
				rets.add(seg) ;
			}
		}
		return rets ;
	}
	
	public HLAddrSeg findSeg(ValTP vtp,String addr)
	{
		//ArrayList<FxAddrSeg> rets = new ArrayList<>() ;
		int k =addr.indexOf('.') ;
		String bit_str = null ;
		if(k>0)
		{
			bit_str = addr.substring(k+1) ;
			addr = addr.substring(0,k) ;
		}
		
		int addr_n = Convert.parseToInt32(addr, -1) ;
		if(addr_n<0)
			return null ;
		int bit_n = Convert.parseToInt32(bit_str, -1) ;
		
		for(HLAddrSeg seg:this.segs)
		{
			if(seg.matchValTP(vtp))
			{
				if(seg.matchAddr(addr_n,bit_n))
					return seg ;
			}
		}
		return null ;
	}
	
	public HLAddrSeg findSeg(HLAddr fxaddr)
	{
		ValTP vtp = fxaddr.getValTP() ;
		if(vtp==null)
			return null ;
		
		for(HLAddrSeg seg:this.segs)
		{
			if(seg.matchValTP(vtp))
			{
				if(seg.matchAddr(fxaddr))
					return seg ;
			}
		}
		return null ;
	}

	@Override
	public String getDefTypeForDoc()
	{
		return g("deftp_"+this.prefix);
	}

	@Override
	public List<IAddrDefSeg> getSegsForDoc()
	{
		ArrayList<IAddrDefSeg> rets = new ArrayList<>() ; 
		rets.addAll(segs) ;
		for(HLAddrSeg seg:segs)
		{
			if(seg.isHasBit())
				rets.add(new HLAddrSegSubBit(seg)) ;
		}
		return rets;
	}
}
