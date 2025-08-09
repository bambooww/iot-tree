package org.iottree.driver.s7.eth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.DevDriver;
import org.iottree.core.DevAddr.IAddrDef;
import org.iottree.core.UAVal.ValTP;

/**
 * TODO will use it later
 * @author jason.zhu
 *
 */
public abstract class S7Model extends DevDriver.Model
{
	private LinkedHashMap<String,S7AddrDef> prefix2addrdef = new LinkedHashMap<>() ;
	
	public S7Model(String name, String t)
	{
		super(name, t);
	}
	
	public void setAddrDef(S7AddrDef addr_def)
	{
		prefix2addrdef.put(addr_def.prefix, addr_def) ;
	}
	
	public abstract List<S7MemTp> listSupMemTps();
	
	public List<String> listPrefix()
	{
		ArrayList<String> rets =new ArrayList<>() ;
		rets.addAll(prefix2addrdef.keySet()) ;
		return rets ;
	}
	
	public S7AddrDef getAddrDef(String prefix)
	{
		return this.prefix2addrdef.get(prefix) ;
	}

	public S7Addr transAddr(String prefix,String num_str,String bit_num,ValTP vtp,StringBuilder failedr)
	{
		S7AddrDef def = this.prefix2addrdef.get(prefix) ;
		if(def==null)
		{
			failedr.append("no S7AddrDef found with prefix="+prefix) ;
			return null ;
		}
		
		S7AddrSeg addrseg = null ;
		//def.findSeg(vtp, num_str) ;
		Integer iv = null ;
		if(vtp!=null)
		{
			for(S7AddrSeg seg:def.segs)
			{
				if(seg.matchValTP(vtp))
				{
					iv = seg.matchAddr(num_str) ;
					if(iv!=null)
					{
						addrseg = seg ;
						break ;
					}
				}
			}
			if(addrseg==null)
			{
				failedr.append("no AddrSeg match with ValTP="+vtp.name()) ;
				return null ;
			}
		}
		else
		{
			for(S7AddrSeg seg:def.segs)
			{
				iv = seg.matchAddr(num_str) ;
				if(iv!=null)
				{
					addrseg = seg ;
					break ;
				}
			}
			if(addrseg==null)
			{
				failedr.append("no AddrSeg match with num str="+num_str) ;
				return null ;
			}
			vtp = addrseg.valTPs[0];
		}
		
		if(iv==null)
		{
			failedr.append("no AddrSeg match with ValTP="+vtp.name()) ;
			return null ;
		}
		int bitnum = Integer.parseInt(bit_num) ;
		if(bitnum>=0)
		{
			if(!addrseg.bBitPos)
			{
				failedr.append("not support bit access with prefix="+prefix) ;
				return null ;
			}
			switch(vtp)
			{
			case vt_int16:
			case vt_uint16:
				if(bitnum>15)
				{
					failedr.append("bit access must in [0,15]") ;
					return null ;
				}
				break ;
			case vt_int32:
			case vt_uint32:
				if(bitnum>31)
				{
					failedr.append("bit access must in [0,31]") ;
					return null ;
				}
				break ;
			default:
				break ;
			}
			
			//vtp = ValTP.vt_bool ;
		}
		String addrstr = prefix+num_str ;
		if(bitnum>=0)
			addrstr += bitnum<9?(".0"+bitnum):("."+bitnum) ;
			
		S7Addr ret = S7Addr.parseS7Addr(addrstr, vtp, failedr) ;
//		S7Addr ret = new S7Addr(addrstr,vtp,this,prefix,iv,addrseg.digitNum,bitnum)
//				.asDef(def, addrseg);
		//ret.setWritable(addrseg.bWrite) ;
		return ret ;
	}
	
	
	public HashMap<S7AddrSeg,List<S7Addr>> filterAndSortAddrs(String prefix,List<S7Addr> addrs)
	{
		S7AddrDef def = this.getAddrDef(prefix) ;
		if(def==null)
			return null ;
		HashMap<S7AddrSeg,List<S7Addr>> rets = new HashMap<>() ;
		//ArrayList<FxAddr> r = new ArrayList<>() ;
		for(S7Addr ma:addrs)
		{
			if(!prefix.equals(ma.getMemTp().name())) //.getPrefix()))
				continue ;
			
			S7AddrSeg seg = def.findSeg(ma) ;
			if(seg==null)
				continue ;
			
			List<S7Addr> ads = rets.get(seg) ;
			if(ads==null)
			{
				ads = new ArrayList<>() ;
				rets.put(seg, ads) ;
			}
			
			ads.add(ma) ;
		}
		for(List<S7Addr> ads:rets.values())
			Collections.sort(ads);
		return rets ;
	}
	
	@Override
	public List<IAddrDef> getAddrDefs()
	{
		ArrayList<IAddrDef> rets = new ArrayList<>() ;
		rets.addAll(prefix2addrdef.values()) ;
		return rets ;
	}
	
	/**
	 * not null will has owner addr help page
	 * @return
	 */
	@Override
	public String getAddrHelpUrl(String lan)
	{
		return S7EthDriver.DRIVER_NAME+".s7_eth."+lan+".jsp";
	}
}


class S7Model_200 extends S7Model
{
	static List<S7MemTp> MEM_TPS = Arrays.asList(S7MemTp.I,S7MemTp.Q,S7MemTp.M,S7MemTp.DB,S7MemTp.C,S7MemTp.T,
			S7MemTp.V,S7MemTp.AI,S7MemTp.AQ) ;
	// read write test ok
	public S7Model_200(String name,String tt)
	{
		super(name, tt);

//		setAddrDef(new S7AddrDef("I","Inputs")
//				.asValTpSeg(new S7AddrSeg(0,0x3fff,4,new ValTP[] {ValTP.vt_bool}).asHex(true).asValBit(true))
//				.asValTpSeg(new S7AddrSeg(0,0x3ff0,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(true))
//				.asValTpSeg(new S7AddrSeg(0,0x3fe0,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32}).asHex(true))
//				) ;
		
	}
	
	@Override
	public List<S7MemTp> listSupMemTps()
	{
		return MEM_TPS;
	}
	
	@Override
	public String getAddrHelpUrl(String lan)
	{
		return S7EthDriver.DRIVER_NAME+".s7_200."+lan+".jsp";
	}
}

class S7Model_300__1500 extends S7Model
{
	static List<S7MemTp> MEM_TPS = Arrays.asList(S7MemTp.I,S7MemTp.Q,S7MemTp.M,S7MemTp.DB,S7MemTp.C,S7MemTp.T) ;
	// read write test ok
	public S7Model_300__1500(String name,String tt)
	{
		super(name, tt);

//		setAddrDef(new S7AddrDef("I","Inputs")
//				.asValTpSeg(new S7AddrSeg(0,0x3fff,4,new ValTP[] {ValTP.vt_bool}).asHex(true).asValBit(true))
//				.asValTpSeg(new S7AddrSeg(0,0x3ff0,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(true))
//				.asValTpSeg(new S7AddrSeg(0,0x3fe0,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32}).asHex(true))
//				) ;
//		setAddrDef(new S7AddrDef("Q","Outputs")
//				.asValTpSeg(new S7AddrSeg(0,0x3fff,4,new ValTP[] {ValTP.vt_bool}).asHex(true).asValBit(true))
//				.asValTpSeg(new S7AddrSeg(0,0x3ff0,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(true))
//				.asValTpSeg(new S7AddrSeg(0,0x3fe0,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32}).asHex(true))
//				) ;
	}
	
	@Override
	public List<S7MemTp> listSupMemTps()
	{
		return MEM_TPS;
	}
	
}