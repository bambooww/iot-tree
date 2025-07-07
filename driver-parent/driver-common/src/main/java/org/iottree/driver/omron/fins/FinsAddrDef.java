package org.iottree.driver.omron.fins;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.DevAddr.IAddrDef;
import org.iottree.core.DevAddr.IAddrDefSeg;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ILang;

public class FinsAddrDef implements IAddrDef,ILang
{
	String prefix = null ;
	
	
	ArrayList<FinsAddrSeg> segs = new ArrayList<>() ; 
		
	public FinsAddrDef(String prefix)
	{
		this.prefix = prefix ;
		//this.title = title ;
	}
	
	
	
	public FinsAddrDef asValTpSeg(FinsAddrSeg seg)
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
	
	public List<FinsAddrSeg> getSegs()
	{
		return this.segs ;
	}
	
	/**
	 * @param vtp
	 * @return
	 */
	public List<FinsAddrSeg> findSegs(ValTP vtp)
	{
		ArrayList<FinsAddrSeg> rets = new ArrayList<>() ;
		for(FinsAddrSeg seg:this.segs)
		{
			if(seg.matchValTP(vtp))
			{
				rets.add(seg) ;
			}
		}
		return rets ;
	}
	
	public FinsAddrSeg findSeg(ValTP vtp,String addr)
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
		
		for(FinsAddrSeg seg:this.segs)
		{
			if(seg.matchValTP(vtp))
			{
				if(seg.matchAddr(addr_n,bit_n))
					return seg ;
			}
		}
		return null ;
	}
	
	public FinsAddrSeg findSeg(FinsAddr fxaddr)
	{
		ValTP vtp = fxaddr.getValTP() ;
		if(vtp==null)
			return null ;
		
		for(FinsAddrSeg seg:this.segs)
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
		for(FinsAddrSeg seg:segs)
		{
			if(seg.isHasBit())
				rets.add(new FinsAddrSegSubBit(seg)) ;
		}
		return rets;
	}
	
}
