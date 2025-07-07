package org.iottree.driver.omron.fins;

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

import kotlin.NotImplementedError;

public abstract class FinsModel  extends DevDriver.Model
{
	private LinkedHashMap<String,FinsAddrDef> prefix2addrdef = new LinkedHashMap<>() ;
	
	public FinsModel(String name, String t)
	{
		super(name, t);
	}


	public void setAddrDef(FinsAddrDef addr_def)
	{
		prefix2addrdef.put(addr_def.prefix, addr_def) ;
	}
	
	public List<String> listPrefix()
	{
		ArrayList<String> rets =new ArrayList<>() ;
		rets.addAll(prefix2addrdef.keySet()) ;
		return rets ;
	}
	
	public FinsAddrDef getAddrDef(String prefix)
	{
		return this.prefix2addrdef.get(prefix) ;
	}
	
	FinsAddr transAddr(String prefix,String addr_str,String bit_str,ValTP vtp,StringBuilder failedr)
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

	public FinsAddr transAddr(String prefix,int addr_num,int bit_num,ValTP vtp,StringBuilder failedr)
	{
		FinsAddrDef def = this.prefix2addrdef.get(prefix) ;
		if(def==null)
		{
			failedr.append("no FinsAddrDef found with prefix="+prefix) ;
			return null ;
		}
		
		FinsAddrSeg addrseg = null ;
		//def.findSeg(vtp, num_str) ;
		//Integer iv = null ;
		if(vtp!=null)
		{
			for(FinsAddrSeg seg:def.segs)
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
			for(FinsAddrSeg seg:def.segs)
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
		
		FinsAddr ret = new FinsAddr(vtp,prefix,addr_num,bit_num,addrseg.digitNum)
				.asDef(def, addrseg);
		ret.setWritable(addrseg.bWrite) ;
		return ret ;
	}
	
	
	public HashMap<FinsAddrSeg,List<FinsAddr>> filterAndSortAddrs(String prefix,List<FinsAddr> addrs)
	{
		FinsAddrDef def = this.getAddrDef(prefix) ;
		if(def==null)
			return null ;
		HashMap<FinsAddrSeg,List<FinsAddr>> rets = new HashMap<>() ;
		//ArrayList<FinsAddr> r = new ArrayList<>() ;
		for(FinsAddr ma:addrs)
		{
			if(!prefix.equals(ma.getPrefix()))
				continue ;
			
			FinsAddrSeg seg = def.findSeg(ma) ;
			if(seg==null)
				continue ;
			
			List<FinsAddr> ads = rets.get(seg) ;
			if(ads==null)
			{
				ads = new ArrayList<>() ;
				rets.put(seg, ads) ;
			}
			
			ads.add(ma) ;
		}
		for(List<FinsAddr> ads:rets.values())
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
		
		models.add(new FinsModel_CS1()) ;
		models.add(new FinsModel_CJ1()) ;
		models.add(new FinsModel_CJ2()) ;
		//models.add(new FinsModel_C200H()) ;
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
class FinsModel_CJ1 extends FinsModel
{
	// read write test ok
	public FinsModel_CJ1()
	{
		super("cj1", "CJ1,CP1");

		setAddrDef(new FinsAddrDef("A")
				.asValTpSeg(new FinsAddrSeg("R",0,447,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(false).asHasSubBit(true))
				.asValTpSeg(new FinsAddrSeg("R",0,446,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(false).asHasSubBit(false))
				.asValTpSeg(new FinsAddrSeg("RW",448,959,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new FinsAddrSeg("RW",448,958,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		
		setAddrDef(new FinsAddrDef("CIO")
				.asValTpSeg(new FinsAddrSeg("IO",0,6143,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new FinsAddrSeg("IO",0,6142,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new FinsAddrDef("C")
				.asValTpSeg(new FinsAddrSeg("IO",0,4095,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new FinsAddrDef("CS")
				.asValTpSeg(new FinsAddrSeg("IO",0,4095,4,new ValTP[] {ValTP.vt_bool}).asWrite(true).asHasSubBit(false))
				);
		
		setAddrDef(new FinsAddrDef("D")
				.asValTpSeg(new FinsAddrSeg("IO",0,32767,5,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new FinsAddrSeg("IO",0,32766,5,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new FinsAddrDef("DR")
				.asValTpSeg(new FinsAddrSeg("IO",0,15,2,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(false))
				.asValTpSeg(new FinsAddrSeg("IO",0,14,2,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new FinsAddrDef("H")
				.asValTpSeg(new FinsAddrSeg("IO",0,1535,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new FinsAddrSeg("IO",0,1534,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new FinsAddrDef("IR")
				.asValTpSeg(new FinsAddrSeg("IO",0,15,2,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new FinsAddrDef("TK")
				.asValTpSeg(new FinsAddrSeg("IO",0,31,2,new ValTP[] {ValTP.vt_bool}).asWrite(false).asHasSubBit(false))
				);
		
		setAddrDef(new FinsAddrDef("T")
				.asValTpSeg(new FinsAddrSeg("IO",0,4095,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new FinsAddrDef("TS")
				.asValTpSeg(new FinsAddrSeg("IO",0,4095,4,new ValTP[] {ValTP.vt_bool}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new FinsAddrDef("W")
				.asValTpSeg(new FinsAddrSeg("IO",0,511,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new FinsAddrSeg("IO",0,510,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
	}
	
	public FinsMode getFinsMode()
	{
		return FinsMode.getMode_CS_CJ1();
	}
}

class FinsModel_CJ2 extends FinsModel
{
	// read write test ok
	public FinsModel_CJ2()
	{
		super("cj2", "CJ2");

		setAddrDef(new FinsAddrDef("A")
				.asValTpSeg(new FinsAddrSeg("R",0,447,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(false).asHasSubBit(true))
				.asValTpSeg(new FinsAddrSeg("R",0,446,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(false).asHasSubBit(false))
				.asValTpSeg(new FinsAddrSeg("RW",448,1471,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new FinsAddrSeg("RW",448,1470,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				.asValTpSeg(new FinsAddrSeg("R",10000,11535,5,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(false).asHasSubBit(true))
				.asValTpSeg(new FinsAddrSeg("R",10000,11534,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(false).asHasSubBit(false))
				);
		
		setAddrDef(new FinsAddrDef("CIO")
				.asValTpSeg(new FinsAddrSeg("IO",0,6143,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new FinsAddrSeg("IO",0,6142,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new FinsAddrDef("C")
				.asValTpSeg(new FinsAddrSeg("IO",0,4095,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new FinsAddrDef("CS")
				.asValTpSeg(new FinsAddrSeg("IO",0,4095,4,new ValTP[] {ValTP.vt_bool}).asWrite(true).asHasSubBit(false))
				);
		
		setAddrDef(new FinsAddrDef("D")
				.asValTpSeg(new FinsAddrSeg("IO",0,32767,5,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new FinsAddrSeg("IO",0,32766,5,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new FinsAddrDef("DR")
				.asValTpSeg(new FinsAddrSeg("IO",0,15,2,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(false))
				.asValTpSeg(new FinsAddrSeg("IO",0,14,2,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new FinsAddrDef("H")
				.asValTpSeg(new FinsAddrSeg("IO",0,1535,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new FinsAddrSeg("IO",0,1534,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new FinsAddrDef("IR")
				.asValTpSeg(new FinsAddrSeg("IO",0,15,2,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new FinsAddrDef("TK")
				.asValTpSeg(new FinsAddrSeg("IO",0,127,3,new ValTP[] {ValTP.vt_bool}).asWrite(false).asHasSubBit(false))
				);
		
		setAddrDef(new FinsAddrDef("T")
				.asValTpSeg(new FinsAddrSeg("IO",0,4095,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new FinsAddrDef("TS")
				.asValTpSeg(new FinsAddrSeg("IO",0,4095,4,new ValTP[] {ValTP.vt_bool}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new FinsAddrDef("W")
				.asValTpSeg(new FinsAddrSeg("IO",0,511,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new FinsAddrSeg("IO",0,510,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
	}
	
	public FinsMode getFinsMode()
	{
		return FinsMode.getMode_CJ2();
	}
}

class FinsModel_CS1 extends FinsModel
{
	// read write test ok
	public FinsModel_CS1()
	{
		super("cs1", "CS1");

		setAddrDef(new FinsAddrDef("A")
				.asValTpSeg(new FinsAddrSeg("R",0,447,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(false).asHasSubBit(true))
				.asValTpSeg(new FinsAddrSeg("R",0,446,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(false).asHasSubBit(false))
				.asValTpSeg(new FinsAddrSeg("RW",448,959,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new FinsAddrSeg("RW",448,958,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		
		setAddrDef(new FinsAddrDef("CIO")
				.asValTpSeg(new FinsAddrSeg("IO",0,6143,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new FinsAddrSeg("IO",0,6142,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new FinsAddrDef("C")
				.asValTpSeg(new FinsAddrSeg("IO",0,4095,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new FinsAddrDef("CS")
				.asValTpSeg(new FinsAddrSeg("IO",0,4095,4,new ValTP[] {ValTP.vt_bool}).asWrite(true).asHasSubBit(false))
				);
		
		setAddrDef(new FinsAddrDef("D")
				.asValTpSeg(new FinsAddrSeg("IO",0,32767,5,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new FinsAddrSeg("IO",0,32766,5,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new FinsAddrDef("DR")
				.asValTpSeg(new FinsAddrSeg("IO",0,15,2,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(false))
				.asValTpSeg(new FinsAddrSeg("IO",0,14,2,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new FinsAddrDef("H")
				.asValTpSeg(new FinsAddrSeg("IO",0,1535,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new FinsAddrSeg("IO",0,1534,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new FinsAddrDef("IR")
				.asValTpSeg(new FinsAddrSeg("IO",0,15,2,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new FinsAddrDef("TK")
				.asValTpSeg(new FinsAddrSeg("IO",0,31,2,new ValTP[] {ValTP.vt_bool}).asWrite(false).asHasSubBit(false))
				);
		
		setAddrDef(new FinsAddrDef("T")
				.asValTpSeg(new FinsAddrSeg("IO",0,4095,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new FinsAddrDef("TS")
				.asValTpSeg(new FinsAddrSeg("IO",0,4095,4,new ValTP[] {ValTP.vt_bool}).asWrite(true).asHasSubBit(false))
				);
		setAddrDef(new FinsAddrDef("W")
				.asValTpSeg(new FinsAddrSeg("IO",0,511,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new FinsAddrSeg("IO",0,510,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
	}
	
	public FinsMode getFinsMode()
	{
		return FinsMode.getMode_CS_CJ1();
	}
}

class FinsModel_C200H extends FinsModel
{
	// read write test ok
	public FinsModel_C200H()
	{
		super("c200h", "C200H");

		setAddrDef(new FinsAddrDef("AR")
				.asValTpSeg(new FinsAddrSeg("AR",0,27,2,new ValTP[] {ValTP.vt_bool,ValTP.vt_int16,ValTP.vt_uint16}).asWrite(true).asHasSubBit(true))
				.asValTpSeg(new FinsAddrSeg("AR",0,26,2,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float}).asWrite(true).asHasSubBit(false))
				);
	}

	@Override
	public FinsMode getFinsMode()
	{
		throw new NotImplementedError() ;
	}
	
	
}