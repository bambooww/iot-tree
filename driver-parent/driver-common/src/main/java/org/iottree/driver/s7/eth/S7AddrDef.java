package org.iottree.driver.s7.eth;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.DevAddr.IAddrDef;
import org.iottree.core.DevAddr.IAddrDefSeg;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.util.ILang;

public class S7AddrDef implements IAddrDef,ILang
{
	/**
	 * X Y M
	 */
	String prefix = null ;
	
	String title ;
	
	ArrayList<S7AddrSeg> segs = new ArrayList<>() ; 
		
	public S7AddrDef(String prefix,String title)
	{
		this.prefix = prefix ;
		this.title = title ;
	}
	
	public S7AddrDef asValTpSeg(S7AddrSeg seg)
	{
		seg.belongTo = this ;
		this.segs.add(seg) ;
		return this ;
	}
	
	public String getPrefix()
	{
		return this.prefix ;
	}
	

	public String getTitle()
	{
		return this.title ;
	}

	
	public List<S7AddrSeg> getSegs()
	{
		return this.segs ;
	}
	
	/**
	 * @param vtp
	 * @return
	 */
	public List<S7AddrSeg> findSegs(ValTP vtp)
	{
		ArrayList<S7AddrSeg> rets = new ArrayList<>() ;
		for(S7AddrSeg seg:this.segs)
		{
			if(seg.matchValTP(vtp))
			{
				rets.add(seg) ;
			}
		}
		return rets ;
	}
	
	public S7AddrSeg findSeg(ValTP vtp,String addr)
	{
		//ArrayList<FxAddrSeg> rets = new ArrayList<>() ;
		for(S7AddrSeg seg:this.segs)
		{
			if(seg.matchValTP(vtp))
			{
				if(seg.matchAddr(addr)!=null)
					return seg ;
			}
		}
		return null ;
	}
	
	public S7AddrSeg findSeg(S7Addr fxaddr)
	{
		ValTP vtp = fxaddr.getValTP() ;
		if(vtp==null)
			return null ;
		
		for(S7AddrSeg seg:this.segs)
		{
			if(seg.matchValTP(vtp))
			{
				if(seg.matchAddr(fxaddr))
					return seg ;
			}
		}
		return null ;
	}
	//public FxAddr trans

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
		
		return rets;
	}
}
