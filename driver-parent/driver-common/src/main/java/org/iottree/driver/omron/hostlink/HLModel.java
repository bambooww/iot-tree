package org.iottree.driver.omron.hostlink;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.DevDriver;
import org.iottree.core.DevAddr.IAddrDef;
import org.iottree.core.DevDriver.Model;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.util.Convert;
import org.iottree.driver.omron.fins.FinsMode;

import kotlin.NotImplementedError;

public abstract class HLModel  extends DevDriver.Model
{
	private LinkedHashMap<String,HLAddrDef> prefix2addrdef = new LinkedHashMap<>() ;
	
	public HLModel(String name, String t)
	{
		super(name, t);
	}


	public void setAddrDef(HLAddrDef addr_def)
	{
		prefix2addrdef.put(addr_def.prefix, addr_def) ;
	}
	
	public List<String> listPrefix()
	{
		ArrayList<String> rets =new ArrayList<>() ;
		rets.addAll(prefix2addrdef.keySet()) ;
		return rets ;
	}
	
	public HLAddrDef getAddrDef(String prefix)
	{
		return this.prefix2addrdef.get(prefix) ;
	}
	
	HLAddr transAddr(String prefix,String addr_str,String bit_str,ValTP vtp,StringBuilder failedr)
	{
		int addr_n = Convert.parseToInt32(addr_str, -1) ;
		if(addr_n<0)
		{
			failedr.append("invalid addr "+addr_str) ;
			return null ;
		}
		int bit_n = Convert.parseToInt32(bit_str, -1) ;
		if(bit_n>15)
		{
			failedr.append("invalid bit_str "+bit_str) ;
			return null ;
		}
		
		if(bit_n>=0 && vtp!=ValTP.vt_bool)
		{
			failedr.append("address with bit must bool type") ;
			return null ;
		}
		return transAddr(prefix,addr_n, bit_n,vtp,failedr) ;
	}

	public HLAddr transAddr(String prefix,int addr_num,int bit_num,ValTP vtp,StringBuilder failedr)
	{
		HLAddrDef def = this.prefix2addrdef.get(prefix) ;
		if(def==null)
		{
			failedr.append("no HLAddrDef found with prefix="+prefix) ;
			return null ;
		}
		
		HLAddrSeg addrseg = null ;
		//def.findSeg(vtp, num_str) ;
		//Integer iv = null ;
		if(vtp!=null)
		{
			for(HLAddrSeg seg:def.segs)
			{
				if(seg.matchValTP(vtp))
				{
					if(seg.matchAddr(addr_num,bit_num) )
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
			for(HLAddrSeg seg:def.segs)
			{
				if(seg.matchAddr(addr_num,bit_num))
				{
					addrseg = seg ;
					break ;
				}
			}
			if(addrseg==null)
			{
				failedr.append("no AddrSeg match with num str="+addr_num) ;
				return null ;
			}
			vtp = addrseg.valTPs[0];
		}
		
		HLAddr ret = new HLAddr(vtp,prefix,addr_num,bit_num,addrseg.digitNum)
				.asDef(def, addrseg);
		ret.setWritable(addrseg.bWrite) ;
		return ret ;
	}
	
	
	public HashMap<HLAddrSeg,List<HLAddr>> filterAndSortAddrs(String prefix,List<HLAddr> addrs)
	{
		HLAddrDef def = this.getAddrDef(prefix) ;
		if(def==null)
			return null ;
		HashMap<HLAddrSeg,List<HLAddr>> rets = new HashMap<>() ;
		//ArrayList<HLAddr> r = new ArrayList<>() ;
		for(HLAddr ma:addrs)
		{
			if(!prefix.equals(ma.getPrefix()))
				continue ;
			
			HLAddrSeg seg = def.findSeg(ma) ;
			if(seg==null)
				continue ;
			
			List<HLAddr> ads = rets.get(seg) ;
			if(ads==null)
			{
				ads = new ArrayList<>() ;
				rets.put(seg, ads) ;
			}
			
			ads.add(ma) ;
		}
		for(List<HLAddr> ads:rets.values())
			Collections.sort(ads);
		return rets ;
	}
	
	public List<IAddrDef> getAddrDefs()
	{
		ArrayList<IAddrDef> rets = new ArrayList<>() ;
		rets.addAll(prefix2addrdef.values()) ;
		return rets ;
	}
	
	public abstract FinsMode getFinsMode() ;
	
	static ArrayList<Model> models = new ArrayList<>() ;
	
	static
	{
		
		models.add(new HLModel_CS1()) ;
		models.add(new HLModel_CJ1()) ;
		models.add(new HLModel_CJ2()) ;
		//models.add(new HLModel_C200H()) ;
	}
	
	public static List<Model> getModelsAll()
	{
		return models ;
	}
}

/**
 * CP1X PLC , like CP1H CP1E CP1L etc
 * @author jason.zhu
 *
 */
class HLModel_CJ1 extends HLModel
{
	// read write test ok
	public HLModel_CJ1()
	{
		super("cj1", "CJ1,CP1");

		setAddrDef(new HLAddrDef("A")
				.asValTpSeg(new HLAddrSeg("R",0,447,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(false).asHasSubBit(true))
				.asValTpSeg(new HLAddrSeg("R",0,446,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(false).asHasSubBit(false))
				.asValTpSeg(new HLAddrSeg("RW",448,959,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new HLAddrSeg("RW",448,958,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		
		setAddrDef(new HLAddrDef("CIO")
				.asValTpSeg(new HLAddrSeg("IO",0,6143,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new HLAddrSeg("IO",0,6142,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new HLAddrDef("C")
				.asValTpSeg(new HLAddrSeg("IO",0,4095,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new HLAddrDef("CS")
				.asValTpSeg(new HLAddrSeg("IO",0,4095,4,new ValTP[] {ValTP.vt_bool}).asWrite(true).asHasSubBit(false))
				);
		
		setAddrDef(new HLAddrDef("D")
				.asValTpSeg(new HLAddrSeg("IO",0,32767,5,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new HLAddrSeg("IO",0,32766,5,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new HLAddrDef("DR")
				.asValTpSeg(new HLAddrSeg("IO",0,15,2,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(false))
				.asValTpSeg(new HLAddrSeg("IO",0,14,2,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new HLAddrDef("H")
				.asValTpSeg(new HLAddrSeg("IO",0,1535,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new HLAddrSeg("IO",0,1534,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new HLAddrDef("IR")
				.asValTpSeg(new HLAddrSeg("IO",0,15,2,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new HLAddrDef("TK")
				.asValTpSeg(new HLAddrSeg("IO",0,31,2,new ValTP[] {ValTP.vt_bool}).asWrite(false).asHasSubBit(false))
				);
		
		setAddrDef(new HLAddrDef("T")
				.asValTpSeg(new HLAddrSeg("IO",0,4095,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new HLAddrDef("TS")
				.asValTpSeg(new HLAddrSeg("IO",0,4095,4,new ValTP[] {ValTP.vt_bool}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new HLAddrDef("W")
				.asValTpSeg(new HLAddrSeg("IO",0,511,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new HLAddrSeg("IO",0,510,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
	}
	
	public FinsMode getFinsMode()
	{
		return FinsMode.getMode_CS_CJ1();
	}
}

class HLModel_CJ2 extends HLModel
{
	// read write test ok
	public HLModel_CJ2()
	{
		super("cj2", "CJ2");

		setAddrDef(new HLAddrDef("A")
				.asValTpSeg(new HLAddrSeg("R",0,447,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(false).asHasSubBit(true))
				.asValTpSeg(new HLAddrSeg("R",0,446,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(false).asHasSubBit(false))
				.asValTpSeg(new HLAddrSeg("RW",448,1471,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new HLAddrSeg("RW",448,1470,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				.asValTpSeg(new HLAddrSeg("R",10000,11535,5,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(false).asHasSubBit(true))
				.asValTpSeg(new HLAddrSeg("R",10000,11534,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(false).asHasSubBit(false))
				);
		
		setAddrDef(new HLAddrDef("CIO")
				.asValTpSeg(new HLAddrSeg("IO",0,6143,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new HLAddrSeg("IO",0,6142,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new HLAddrDef("C")
				.asValTpSeg(new HLAddrSeg("IO",0,4095,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new HLAddrDef("CS")
				.asValTpSeg(new HLAddrSeg("IO",0,4095,4,new ValTP[] {ValTP.vt_bool}).asWrite(true).asHasSubBit(false))
				);
		
		setAddrDef(new HLAddrDef("D")
				.asValTpSeg(new HLAddrSeg("IO",0,32767,5,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new HLAddrSeg("IO",0,32766,5,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new HLAddrDef("DR")
				.asValTpSeg(new HLAddrSeg("IO",0,15,2,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(false))
				.asValTpSeg(new HLAddrSeg("IO",0,14,2,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new HLAddrDef("H")
				.asValTpSeg(new HLAddrSeg("IO",0,1535,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new HLAddrSeg("IO",0,1534,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new HLAddrDef("IR")
				.asValTpSeg(new HLAddrSeg("IO",0,15,2,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new HLAddrDef("TK")
				.asValTpSeg(new HLAddrSeg("IO",0,127,3,new ValTP[] {ValTP.vt_bool}).asWrite(false).asHasSubBit(false))
				);
		
		setAddrDef(new HLAddrDef("T")
				.asValTpSeg(new HLAddrSeg("IO",0,4095,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new HLAddrDef("TS")
				.asValTpSeg(new HLAddrSeg("IO",0,4095,4,new ValTP[] {ValTP.vt_bool}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new HLAddrDef("W")
				.asValTpSeg(new HLAddrSeg("IO",0,511,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new HLAddrSeg("IO",0,510,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
	}
	
	public FinsMode getFinsMode()
	{
		return FinsMode.getMode_CJ2();
	}
}

class HLModel_CS1 extends HLModel
{
	// read write test ok
	public HLModel_CS1()
	{
		super("cs1", "CS1");

		setAddrDef(new HLAddrDef("A")
				.asValTpSeg(new HLAddrSeg("R",0,447,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(false).asHasSubBit(true))
				.asValTpSeg(new HLAddrSeg("R",0,446,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(false).asHasSubBit(false))
				.asValTpSeg(new HLAddrSeg("RW",448,959,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new HLAddrSeg("RW",448,958,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		
		setAddrDef(new HLAddrDef("CIO")
				.asValTpSeg(new HLAddrSeg("IO",0,6143,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new HLAddrSeg("IO",0,6142,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new HLAddrDef("C")
				.asValTpSeg(new HLAddrSeg("IO",0,4095,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new HLAddrDef("CS")
				.asValTpSeg(new HLAddrSeg("IO",0,4095,4,new ValTP[] {ValTP.vt_bool}).asWrite(true).asHasSubBit(false))
				);
		
		setAddrDef(new HLAddrDef("D")
				.asValTpSeg(new HLAddrSeg("IO",0,32767,5,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new HLAddrSeg("IO",0,32766,5,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new HLAddrDef("DR")
				.asValTpSeg(new HLAddrSeg("IO",0,15,2,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(false))
				.asValTpSeg(new HLAddrSeg("IO",0,14,2,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new HLAddrDef("H")
				.asValTpSeg(new HLAddrSeg("IO",0,1535,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new HLAddrSeg("IO",0,1534,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new HLAddrDef("IR")
				.asValTpSeg(new HLAddrSeg("IO",0,15,2,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new HLAddrDef("TK")
				.asValTpSeg(new HLAddrSeg("IO",0,31,2,new ValTP[] {ValTP.vt_bool}).asWrite(false).asHasSubBit(false))
				);
		
		setAddrDef(new HLAddrDef("T")
				.asValTpSeg(new HLAddrSeg("IO",0,4095,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new HLAddrDef("TS")
				.asValTpSeg(new HLAddrSeg("IO",0,4095,4,new ValTP[] {ValTP.vt_bool}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new HLAddrDef("W")
				.asValTpSeg(new HLAddrSeg("IO",0,511,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new HLAddrSeg("IO",0,510,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
	}
	
	public FinsMode getFinsMode()
	{
		return FinsMode.getMode_CS_CJ1();
	}
}

class HLModel_C200H extends HLModel
{
	// read write test ok
	public HLModel_C200H()
	{
		super("c200h", "C200H");

		setAddrDef(new HLAddrDef("AR")
				.asValTpSeg(new HLAddrSeg("AR",0,27,2,new ValTP[] {ValTP.vt_bool,ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new HLAddrSeg("AR",0,26,2,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
	}
	
	public FinsMode getFinsMode()
	{
		throw new NotImplementedError() ;
	}
}
