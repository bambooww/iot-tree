package org.iottree.driver.s7.eth;

import java.util.ArrayList;
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
public class S7Model extends DevDriver.Model
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
	
	public List<IAddrDef> getAddrDefs()
	{
		ArrayList<IAddrDef> rets = new ArrayList<>() ;
		rets.addAll(prefix2addrdef.values()) ;
		return rets ;
	}
	
	
}


//class S7Model_Q extends S7Model
//{
//	// read write test ok
//	public S7Model_Q()
//	{
//		super("q", "Q Series");
//
//		setAddrDef(new S7AddrDef("X","Inputs")
//				.asValTpSeg(new S7AddrSeg(0,0x3fff,4,new ValTP[] {ValTP.vt_bool}).asHex(true).asValBit(true))
//				.asValTpSeg(new S7AddrSeg(0,0x3ff0,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(true))
//				.asValTpSeg(new S7AddrSeg(0,0x3fe0,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32}).asHex(true))
//				) ;
//		setAddrDef(new S7AddrDef("DX","Direct Inputs")
//				.asValTpSeg(new S7AddrSeg(0,0x3fff,4,new ValTP[] {ValTP.vt_bool}).asHex(true).asValBit(true))
//				.asValTpSeg(new S7AddrSeg(0,0x3ff0,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(true))
//				.asValTpSeg(new S7AddrSeg(0,0x3fe0,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32}).asHex(true))
//				) ;
//		
//		setAddrDef(new S7AddrDef("Y","Outputs")
//				.asValTpSeg(new S7AddrSeg(0,0x3fff,4,new ValTP[] {ValTP.vt_bool}).asHex(true).asValBit(true))
//				.asValTpSeg(new S7AddrSeg(0,0x3ff0,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(true))
//				.asValTpSeg(new S7AddrSeg(0,0x3fe0,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32}).asHex(true))
//				) ;
//		setAddrDef(new S7AddrDef("DY","Direct Outputs")
//				.asValTpSeg(new S7AddrSeg(0,0x3fff,4,new ValTP[] {ValTP.vt_bool}).asHex(true).asValBit(true))
//				.asValTpSeg(new S7AddrSeg(0,0x3ff0,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(true))
//				.asValTpSeg(new S7AddrSeg(0,0x3fe0,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32}).asHex(true))
//				) ;
//		
//		setAddrDef(new S7AddrDef("B","Link Relays")
//				.asValTpSeg(new S7AddrSeg(0,0x3fff,4,new ValTP[] {ValTP.vt_bool}).asHex(true).asValBit(true))
//				.asValTpSeg(new S7AddrSeg(0,0x3ff0,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(true))
//				.asValTpSeg(new S7AddrSeg(0,0x3fe0,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32}).asHex(true))
//				) ;
//		
//		setAddrDef(new S7AddrDef("SB","Special Link Relays")
//				.asValTpSeg(new S7AddrSeg(0,0x07ff,4,new ValTP[] {ValTP.vt_bool}).asHex(true).asValBit(true))
//				.asValTpSeg(new S7AddrSeg(0,0x07f0,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(true))
//				.asValTpSeg(new S7AddrSeg(0,0x07e0,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32}).asHex(true))
//				) ;
//		
//		setAddrDef(new S7AddrDef("M","Internal Relays")
//				.asValTpSeg(new S7AddrSeg(0,16383,5,new ValTP[] {ValTP.vt_bool}).asHex(false).asValBit(true))
//				.asValTpSeg(new S7AddrSeg(0,16368,5,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(false))
//				.asValTpSeg(new S7AddrSeg(0,16352,5,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32}).asHex(false))
//				) ;
//		
//		setAddrDef(new S7AddrDef("SM","Special Int. Relays")
//				.asValTpSeg(new S7AddrSeg(0,2047,4,new ValTP[] {ValTP.vt_bool}).asHex(false).asValBit(true))
//				.asValTpSeg(new S7AddrSeg(0,2032,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(false))
//				.asValTpSeg(new S7AddrSeg(0,2016,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32}).asHex(false))
//				) ;
//		
//		setAddrDef(new S7AddrDef("L","Latch Relays")
//				.asValTpSeg(new S7AddrSeg(0,16383,5,new ValTP[] {ValTP.vt_bool}).asHex(false).asValBit(true))
//				.asValTpSeg(new S7AddrSeg(0,16368,5,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(false))
//				.asValTpSeg(new S7AddrSeg(0,16352,5,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32}).asHex(false))
//				) ;
//		setAddrDef(new S7AddrDef("F","Annunciator Relays")
//				.asValTpSeg(new S7AddrSeg(0,2047,4,new ValTP[] {ValTP.vt_bool}).asHex(false).asValBit(true))
//				.asValTpSeg(new S7AddrSeg(0,2032,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(false))
//				.asValTpSeg(new S7AddrSeg(0,2016,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32}).asHex(false))
//				) ;
//		setAddrDef(new S7AddrDef("V","Edge Relays")
//				.asValTpSeg(new S7AddrSeg(0,2047,4,new ValTP[] {ValTP.vt_bool}).asHex(false).asValBit(true))
//				.asValTpSeg(new S7AddrSeg(0,2032,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(false))
//				.asValTpSeg(new S7AddrSeg(0,2016,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32}).asHex(false))
//				) ;
//		
//		// may no in 3E
////		setAddrDef(new S7AddrDef("S","Step Relays")
////				.asValTpSeg(new S7AddrSeg(0,16383,5,new ValTP[] {ValTP.vt_bool}).asHex(false).asValBit(true))
////				.asValTpSeg(new S7AddrSeg(0,16368,5,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(false))
////				.asValTpSeg(new S7AddrSeg(0,16352,5,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32}).asHex(false))
////				) ;
//		setAddrDef(new S7AddrDef("TS","Timer Contacts")
//				.asValTpSeg(new S7AddrSeg(0,2047,4,new ValTP[] {ValTP.vt_bool}).asHex(false).asValBit(true))
//				.asValTpSeg(new S7AddrSeg(0,2032,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(false))
//				.asValTpSeg(new S7AddrSeg(0,2016,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32}).asHex(false))
//				) ;
//		setAddrDef(new S7AddrDef("TC","Timer Coils")
//				.asValTpSeg(new S7AddrSeg(0,2047,4,new ValTP[] {ValTP.vt_bool}).asHex(false).asValBit(true))
//				.asValTpSeg(new S7AddrSeg(0,2032,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(false))
//				.asValTpSeg(new S7AddrSeg(0,2016,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32}).asHex(false))
//				) ;
//		setAddrDef(new S7AddrDef("SS","Integrating Timer Contacts")
//				.asValTpSeg(new S7AddrSeg(0,2047,4,new ValTP[] {ValTP.vt_bool}).asHex(false).asValBit(true))
//				.asValTpSeg(new S7AddrSeg(0,2032,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(false))
//				.asValTpSeg(new S7AddrSeg(0,2016,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32}).asHex(false))
//				) ;
//		setAddrDef(new S7AddrDef("SC","Integrating Timer Coils")
//				.asValTpSeg(new S7AddrSeg(0,2047,4,new ValTP[] {ValTP.vt_bool}).asHex(false).asValBit(true))
//				.asValTpSeg(new S7AddrSeg(0,2032,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(false))
//				.asValTpSeg(new S7AddrSeg(0,2016,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32}).asHex(false))
//				) ;
//		
//		setAddrDef(new S7AddrDef("CS","Counter Contacts")
//				.asValTpSeg(new S7AddrSeg(0,1023,4,new ValTP[] {ValTP.vt_bool}).asHex(false).asValBit(true))
//				.asValTpSeg(new S7AddrSeg(0,1008,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(false))
//				.asValTpSeg(new S7AddrSeg(0,992,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32}).asHex(false))
//				) ;
//		setAddrDef(new S7AddrDef("CC","Counter Coils")
//				.asValTpSeg(new S7AddrSeg(0,1023,4,new ValTP[] {ValTP.vt_bool}).asHex(false).asValBit(true))
//				.asValTpSeg(new S7AddrSeg(0,1008,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(false))
//				.asValTpSeg(new S7AddrSeg(0,992,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32}).asHex(false))
//				) ;
//		
//		setAddrDef(new S7AddrDef("TN","Timer Value")
//				.asValTpSeg(new S7AddrSeg(0,2047,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(false))
//				) ;
//		
//		setAddrDef(new S7AddrDef("SN","Integrating Timer Value")
//				.asValTpSeg(new S7AddrSeg(0,2047,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(false))
//				) ;
//		setAddrDef(new S7AddrDef("CN","Counter Value")
//				.asValTpSeg(new S7AddrSeg(0,2047,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(false))
//				) ;
//		setAddrDef(new S7AddrDef("D","Data Registers")
//				.asValTpSeg(new S7AddrSeg(0,12287,5,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(false).asBitPos(true))
//				.asValTpSeg(new S7AddrSeg(0,12286,5,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asHex(false).asBitPos(true))
//				) ;
//		
//		setAddrDef(new S7AddrDef("SD","Special Data Registers")
//				.asValTpSeg(new S7AddrSeg(0,2047,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(false).asBitPos(true))
//				.asValTpSeg(new S7AddrSeg(0,2046,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asHex(false).asBitPos(true))
//				) ;
//		
//		setAddrDef(new S7AddrDef("W","Link Registers")
//				.asValTpSeg(new S7AddrSeg(0,0x3fff,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(true).asBitPos(true))
//				.asValTpSeg(new S7AddrSeg(0,0x3ffe,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asHex(true).asBitPos(true))
//				) ;
//		setAddrDef(new S7AddrDef("SW","Special Link Registers")
//				.asValTpSeg(new S7AddrSeg(0,0x07ff,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(true).asBitPos(true))
//				.asValTpSeg(new S7AddrSeg(0,0x07fe,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32}).asHex(true).asBitPos(true))
//				) ;
//		setAddrDef(new S7AddrDef("R","File Registers")
//				.asValTpSeg(new S7AddrSeg(0,32767,5,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(false).asBitPos(true))
//				.asValTpSeg(new S7AddrSeg(0,32766,5,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asHex(false).asBitPos(true))
//				) ;
//		setAddrDef(new S7AddrDef("Z","Index Registers")
//				.asValTpSeg(new S7AddrSeg(0,15,2,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16,}).asHex(false).asBitPos(true))
//				.asValTpSeg(new S7AddrSeg(0,14,2,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asHex(false).asBitPos(true))
//				) ;
//	}
//	
//}