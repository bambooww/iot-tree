package org.iottree.driver.mitsubishi.fx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.DevDriver;
import org.iottree.core.DevAddr.IAddrDef;
import org.iottree.core.UAVal.ValTP;

public class FxModel extends DevDriver.Model
{
	public static final int TP_X_START = 0x80 ; //128
	public static final int TP_S_START = 0x00 ; //S 为状态元件 
			
	public static final int TP_Y_START = 0xA0 ; //160
	
	public static final int TP_X_FORCE_ONOFF = 0x400 ;
	public static final int TP_Y_FORCE_ONOFF = 0x500 ;
	public static final int TP_S_FORCE_ONOFF = 0x00 ;
	
	public static final int TP_TC_START = 0xC0 ; // timer contacts
	public static final int TP_TCOIL_START = 0x2C0 ; // timer coil
	
	public static final int TP_TV_START = 0x800;// time value
	public static final int TP_TR_START = 0x4C0 ; //Time reset
	
	public static final int TP_T_FORCE_ONOFF = 0x600 ;
	
	public static final int TP_CC_START = 0x1C0 ; // counter contaces ;
	public static final int TP_CCOIL_START = 0x3C0 ; //counter coil
	public static final int TP_CR_START = 0x5C0 ;  // Counter Reset
	
	public static final int TP_C_FORCE_ONOFF = 0xE00 ;
	
	public static final int TP_CV16_START = 0xA00 ; //counter value
	public static final int TP_CV32_START = 0xC00 ;//3072;
	
	public static final int TP_MC_START = 0x100 ; // M Contanct Auxiliary Relays
	public static final int TP_MS_START = 0x1E0 ; // M specila Special Aux. Relays
	public static final int TP_PM_START = 0x300 ;//768
	
	public static final int TP_M_FORCE_ONOFF = 0x800 ;
	public static final int TP_MS_FORCE_ONOFF = 0xF00 ;
	
	public static final int TP_OC_START = 0x3C0 ;//960
	public static final int TP_RC_START = 0x5C0 ;//1472 
	public static final int TP_OT_START = 0x2C0; //704
	public static final int TP_RT_START = 0x4C0;//1216
	
	public static final int TP_PY_START = 0x2A0; //672
	public static final int TP_D_SPEC_START = 0x0E00 ; //3584
	public static final int TP_D_START = 0x1000 ; //4096
	
	private LinkedHashMap<String,FxAddrDef> prefix2addrdef = new LinkedHashMap<>() ;
	
	public FxModel(String name, String t)
	{
		super(name, t);
	}
	
	public void setAddrDef(FxAddrDef addr_def)
	{
		prefix2addrdef.put(addr_def.prefix, addr_def) ;
	}
	
	public List<String> listPrefix()
	{
		ArrayList<String> rets =new ArrayList<>() ;
		rets.addAll(prefix2addrdef.keySet()) ;
		return rets ;
	}
	
	public FxAddrDef getAddrDef(String prefix)
	{
		return this.prefix2addrdef.get(prefix) ;
	}

	public FxAddr transAddr(String prefix,String num_str,ValTP vtp,StringBuilder failedr)
	{
		FxAddrDef def = this.prefix2addrdef.get(prefix) ;
		if(def==null)
		{
			failedr.append("no FxAddrDef found with prefix="+prefix) ;
			return null ;
		}
		
		FxAddrSeg addrseg = null ;
		//def.findSeg(vtp, num_str) ;
		Integer iv = null ;
		if(vtp!=null)
		{
			for(FxAddrSeg seg:def.segs)
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
			for(FxAddrSeg seg:def.segs)
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
		FxAddr ret = new FxAddr(prefix+num_str,vtp,this,prefix,iv,addrseg.bValBit,addrseg.digitNum,addrseg.bOctal)
				.asDef(def, addrseg);
		ret.setWritable(addrseg.bWrite) ;
		return ret ;
	}
	
	
	public HashMap<FxAddrSeg,List<FxAddr>> filterAndSortAddrs(String prefix,List<FxAddr> addrs)
	{
		FxAddrDef def = this.getAddrDef(prefix) ;
		if(def==null)
			return null ;
		HashMap<FxAddrSeg,List<FxAddr>> rets = new HashMap<>() ;
		//ArrayList<FxAddr> r = new ArrayList<>() ;
		for(FxAddr ma:addrs)
		{
			if(!prefix.equals(ma.getPrefix()))
				continue ;
			
			FxAddrSeg seg = def.findSeg(ma) ;
			if(seg==null)
				continue ;
			
			List<FxAddr> ads = rets.get(seg) ;
			if(ads==null)
			{
				ads = new ArrayList<>() ;
				rets.put(seg, ads) ;
			}
			
			ads.add(ma) ;
		}
		for(List<FxAddr> ads:rets.values())
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

class FxModel_FX3U extends FxModel
{
	// read write test ok
	public FxModel_FX3U()
	{
		super("fx3u", "FX3U");

		setAddrDef(new FxAddrDef("X").asValTpSeg(new FxAddrSeg(FxModel.TP_X_START,"Inputs",0,0xff,3,new ValTP[] {ValTP.vt_bool},false).EXT_asBaseValStart(0x8CA0).asOctal(true).asValBit(true))) ;
		setAddrDef(new FxAddrDef("Y").asValTpSeg(new FxAddrSeg(TP_Y_START,"Outputs",0,0xff,3,new ValTP[] {ValTP.vt_bool},true).EXT_asBaseValStart(0x8BC0).EXT_asBaseAddrForceOnOff(0x5E00).asOctal(true).asValBit(true).asBaseAddrForceOnOff(TP_Y_FORCE_ONOFF))) ;
		setAddrDef(new FxAddrDef("M").asValTpSeg(new FxAddrSeg(TP_MC_START,"Auxiliary Relays",0,7679,4,new ValTP[] {ValTP.vt_bool},true).EXT_asBaseValStart(0x8800).EXT_asBaseAddrForceOnOff(0x4000).asValBit(true).asBaseAddrForceOnOff(TP_M_FORCE_ONOFF))
				.asValTpSeg(new FxAddrSeg(TP_MS_START,"Special Aux. Relays",8000,8511,4,new ValTP[] {ValTP.vt_bool},true).EXT_asBaseValStart(0x8C00).EXT_asBaseAddrForceOnOff(0x6000).asValBit(true).asBaseValStart(8000).asBaseAddrForceOnOff(TP_MS_FORCE_ONOFF))
				) ;
		
		setAddrDef(new FxAddrDef("S").asValTpSeg(new FxAddrSeg(FxModel.TP_S_START,"States",0,4095,4,new ValTP[] {ValTP.vt_bool},true).EXT_asBaseValStart(0x8CE0).EXT_asBaseAddrForceOnOff(0x6700).asValBit(true)) 
				) ;
		
		setAddrDef(new FxAddrDef("TS").asValTpSeg(new FxAddrSeg(TP_TC_START,"Timer Contacts",0,511,3,new ValTP[] {ValTP.vt_bool},false).EXT_asBaseValStart(0x8C60).asValBit(true))) ; 
		setAddrDef(new FxAddrDef("CS").asValTpSeg(new FxAddrSeg(TP_CC_START,"Counter Contacts",0,255,3,new ValTP[] {ValTP.vt_bool},false).EXT_asBaseValStart(0x8C40).asValBit(true))) ;
		

		setAddrDef(new FxAddrDef("TR").asValTpSeg(new FxAddrSeg(TP_TR_START,"Timer Reset",0,511,3,new ValTP[] {ValTP.vt_bool},true).EXT_asBaseValStart(0x9300).EXT_asBaseAddrForceOnOff(0x9800).asValBit(true))) ;
		setAddrDef(new FxAddrDef("CR").asValTpSeg(new FxAddrSeg(TP_CR_START,"Counter Reset",0,255,3,new ValTP[] {ValTP.vt_bool},true).EXT_asBaseValStart(0x92E0).EXT_asBaseAddrForceOnOff(0x9700).asValBit(true))) ;
		
		setAddrDef(new FxAddrDef("T").asValTpSeg(new FxAddrSeg(TP_TV_START,"Timer Value",0,511,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true).EXT_asBaseValStart(0x1000))) ;
		setAddrDef(new FxAddrDef("C").asValTpSeg(new FxAddrSeg(TP_CV16_START,"Counter Value",0,199,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true).EXT_asBaseValStart(0x0A00))
				.asValTpSeg(new FxAddrSeg(TP_CV32_START,"Counter Value 32Bit",200,255,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32},true).EXT_asBaseValStart(0x0C00).asAddrStepInt32(true).asBaseValStart(200))
				) ;
		
		setAddrDef(new FxAddrDef("D").asValTpSeg(new FxAddrSeg(TP_D_START,"Data Registers",0,7999,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true).EXT_asBaseValStart(0x4000))
				.asValTpSeg(new FxAddrSeg(TP_D_START,"Data Registers",0,7998,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float},true).EXT_asBaseValStart(0x4000).asAddrStepInt32(false)) 
				
				.asValTpSeg(new FxAddrSeg(TP_D_SPEC_START,"Special Data Registers",8000,8511,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true).EXT_asBaseValStart(0x8000).asBaseValStart(8000)) 
				.asValTpSeg(new FxAddrSeg(TP_D_SPEC_START,"Special Data Registers",8000,8510,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float},true).EXT_asBaseValStart(0x8000).asAddrStepInt32(false).asBaseValStart(8000))
				) ;
	}
//	setAddrDef(new FxAddrDef("TC").asValTpSeg(new FxAddrSeg(TP_TCOIL_START,"Timer Coil",0,511,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true))) ;  //
//	setAddrDef(new FxAddrDef("CC").asValTpSeg(new FxAddrSeg(TP_CCOIL_START,"Counter Coil",0,255,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true))) ; //test ok
	
}

//not test
class FxModel_FX2N extends FxModel
{

	public FxModel_FX2N()
	{
		super("fx2n","FX2N");
		
		setAddrDef(new FxAddrDef("S").asValTpSeg(new FxAddrSeg(FxModel.TP_S_START,"States",0,999,3,new ValTP[] {ValTP.vt_bool},true).EXT_asBaseValStart(0x0280).EXT_asBaseAddrForceOnOff(0x1400).asValBit(true)) 
				) ;
		
		setAddrDef(new FxAddrDef("X").asValTpSeg(new FxAddrSeg(FxModel.TP_X_START,"Inputs",0,0xff,3,new ValTP[] {ValTP.vt_bool},false).EXT_asBaseValStart(0x0240).asOctal(true).asValBit(true))) ;
		setAddrDef(new FxAddrDef("Y").asValTpSeg(new FxAddrSeg(TP_Y_START,"Outputs",0,0xff,3,new ValTP[] {ValTP.vt_bool},true).EXT_asBaseValStart(0x0180).EXT_asBaseAddrForceOnOff(0x0C00).asOctal(true).asValBit(true).asBaseAddrForceOnOff(TP_Y_FORCE_ONOFF))) ;
		
		setAddrDef(new FxAddrDef("M").asValTpSeg(new FxAddrSeg(TP_MC_START,"Auxiliary Relays",0,3071,4,new ValTP[] {ValTP.vt_bool},true).EXT_asBaseValStart(0x0000).EXT_asBaseAddrForceOnOff(0x0000).asValBit(true).asBaseAddrForceOnOff(TP_M_FORCE_ONOFF))
				.asValTpSeg(new FxAddrSeg(TP_MS_START,"Special Aux. Relays",8000,8255,4,new ValTP[] {ValTP.vt_bool},true).EXT_asBaseValStart(0x0130).EXT_asBaseAddrForceOnOff(0x2D40).asValBit(true).asBaseValStart(8000).asBaseAddrForceOnOff(TP_MS_FORCE_ONOFF))
				) ;

		setAddrDef(new FxAddrDef("TS").asValTpSeg(new FxAddrSeg(TP_TC_START,"Timer Contacts",0,255,3,new ValTP[] {ValTP.vt_bool},false).EXT_asBaseValStart(0x0200).asValBit(true))) ; 
		setAddrDef(new FxAddrDef("CS").asValTpSeg(new FxAddrSeg(TP_CC_START,"Counter Contacts",0,255,3,new ValTP[] {ValTP.vt_bool},false).EXT_asBaseValStart(0x01E0).asValBit(true))) ;
		

		setAddrDef(new FxAddrDef("TR").asValTpSeg(new FxAddrSeg(TP_TR_START,"Timer Reset",0,255,3,new ValTP[] {ValTP.vt_bool},true).EXT_asBaseValStart(0x0700).EXT_asBaseAddrForceOnOff(0x9800).asValBit(true))) ;
		setAddrDef(new FxAddrDef("CR").asValTpSeg(new FxAddrSeg(TP_CR_START,"Counter Reset",0,255,3,new ValTP[] {ValTP.vt_bool},true).EXT_asBaseValStart(0x06E0).EXT_asBaseAddrForceOnOff(0x9700).asValBit(true))) ;
		
		setAddrDef(new FxAddrDef("T").asValTpSeg(new FxAddrSeg(TP_TV_START,"Timer Value",0,255,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true).EXT_asBaseValStart(0x1000))) ;
		setAddrDef(new FxAddrDef("C").asValTpSeg(new FxAddrSeg(TP_CV16_START,"Counter Value",0,199,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true).EXT_asBaseValStart(0x0A00))
				.asValTpSeg(new FxAddrSeg(TP_CV32_START,"Counter Value 32Bit",200,255,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32},true).EXT_asBaseValStart(0x0C00).asAddrStepInt32(true).asBaseValStart(200))
				) ;
		
		setAddrDef(new FxAddrDef("D").asValTpSeg(new FxAddrSeg(TP_D_START,"Data Registers",0,7999,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true).EXT_asBaseValStart(0x4000))
				.asValTpSeg(new FxAddrSeg(TP_D_START,"Data Registers",0,7998,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float},true).EXT_asBaseValStart(0x4000).asAddrStepInt32(false)) 
				
				.asValTpSeg(new FxAddrSeg(TP_D_SPEC_START,"Special Data Registers",8000,8255,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true).EXT_asBaseValStart(0x0E00).asBaseValStart(8000)) 
				.asValTpSeg(new FxAddrSeg(TP_D_SPEC_START,"Special Data Registers",8000,8254,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float},true).EXT_asBaseValStart(0x0E00).asAddrStepInt32(false).asBaseValStart(8000))
				) ;
	}
	
}

// not test
class FxModel_FX0N extends FxModel
{

	public FxModel_FX0N()
	{
		super("fx0n", "FX0N");
		

		setAddrDef(new FxAddrDef("S").asValTpSeg(new FxAddrSeg(FxModel.TP_S_START,"States",0,127,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseAddrForceOnOff(TP_S_FORCE_ONOFF)) 
				) ;
		
		setAddrDef(new FxAddrDef("X").asValTpSeg(new FxAddrSeg(FxModel.TP_X_START,"Inputs",0,0x4f,3,new ValTP[] {ValTP.vt_bool},false).asOctal(true).asValBit(true))) ;
		setAddrDef(new FxAddrDef("Y").asValTpSeg(new FxAddrSeg(TP_Y_START,"Outputs",0,0x4f,3,new ValTP[] {ValTP.vt_bool},true).asOctal(true).asValBit(true).asBaseAddrForceOnOff(TP_Y_FORCE_ONOFF))) ;
		
		setAddrDef(new FxAddrDef("M").asValTpSeg(new FxAddrSeg(TP_MC_START,"Auxiliary Relays",0,0511,4,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseAddrForceOnOff(TP_M_FORCE_ONOFF))
				.asValTpSeg(new FxAddrSeg(TP_MS_START,"Special Aux. Relays",8000,8255,4,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseValStart(8000).asBaseAddrForceOnOff(TP_MS_FORCE_ONOFF))
				) ;
		
		
		
		setAddrDef(new FxAddrDef("TS").asValTpSeg(new FxAddrSeg(TP_TC_START,"Timer Contacts",0,63,2,new ValTP[] {ValTP.vt_bool},false).asValBit(true))) ; 
		setAddrDef(new FxAddrDef("CS").asValTpSeg(new FxAddrSeg(TP_CC_START,"Counter Contacts",0,31,2,new ValTP[] {ValTP.vt_bool},false).asValBit(true))
				.asValTpSeg(new FxAddrSeg(TP_CC_START,"Counter Contacts",235,254,3,new ValTP[] {ValTP.vt_bool},false).asValBit(true))
				) ;
		

		setAddrDef(new FxAddrDef("TR").asValTpSeg(new FxAddrSeg(TP_TR_START,"Timer Reset",0,63,2,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseAddrForceOnOff(TP_T_FORCE_ONOFF))) ;
		setAddrDef(new FxAddrDef("CR").asValTpSeg(new FxAddrSeg(TP_CR_START,"Counter Reset",0,31,2,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseAddrForceOnOff(TP_C_FORCE_ONOFF))) ;
		
		setAddrDef(new FxAddrDef("T").asValTpSeg(new FxAddrSeg(TP_TV_START,"Timer Value",0,63,2,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true))) ;
		setAddrDef(new FxAddrDef("C").asValTpSeg(new FxAddrSeg(TP_CV16_START,"Counter Value",0,31,2,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true))
				.asValTpSeg(new FxAddrSeg(TP_CV32_START,"Counter Value 32Bit",235,254,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32},true).asAddrStepInt32(true).asBaseValStart(235))
				) ;
		
		setAddrDef(new FxAddrDef("D").asValTpSeg(new FxAddrSeg(TP_D_START,"Data Registers",0,255,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true))
				.asValTpSeg(new FxAddrSeg(TP_D_START,"Data Registers",0,254,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float},true).asAddrStepInt32(false)) 
				
				.asValTpSeg(new FxAddrSeg(TP_D_SPEC_START,"Special Data Registers",8000,8255,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true).asBaseValStart(8000)) 
				.asValTpSeg(new FxAddrSeg(TP_D_SPEC_START,"Special Data Registers",8000,8254,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float},true).asAddrStepInt32(false).asBaseValStart(8000))
				) ;
	}
	
}

class FxModel_FX0 extends FxModel
{

	public FxModel_FX0()
	{
		super("fx0", "FX0");
		

		setAddrDef(new FxAddrDef("S").asValTpSeg(new FxAddrSeg(FxModel.TP_S_START,"States",0,63,2,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseAddrForceOnOff(TP_S_FORCE_ONOFF)) 
				) ;
		
		setAddrDef(new FxAddrDef("X").asValTpSeg(new FxAddrSeg(FxModel.TP_X_START,"Inputs",0,0xF,3,new ValTP[] {ValTP.vt_bool},false).asOctal(true).asValBit(true))) ;
		setAddrDef(new FxAddrDef("Y").asValTpSeg(new FxAddrSeg(TP_Y_START,"Outputs",0,0xD,3,new ValTP[] {ValTP.vt_bool},true).asOctal(true).asValBit(true).asBaseAddrForceOnOff(TP_Y_FORCE_ONOFF))) ;
		
		setAddrDef(new FxAddrDef("M").asValTpSeg(new FxAddrSeg(TP_MC_START,"Auxiliary Relays",0,0511,4,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseAddrForceOnOff(TP_M_FORCE_ONOFF))
				.asValTpSeg(new FxAddrSeg(TP_MS_START,"Special Aux. Relays",8000,8255,4,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseValStart(8000).asBaseAddrForceOnOff(TP_MS_FORCE_ONOFF))
				) ;
		
		
		
		setAddrDef(new FxAddrDef("TS").asValTpSeg(new FxAddrSeg(TP_TC_START,"Timer Contacts",0,55,2,new ValTP[] {ValTP.vt_bool},false).asValBit(true))) ; 
		setAddrDef(new FxAddrDef("CS").asValTpSeg(new FxAddrSeg(TP_CC_START,"Counter Contacts",0,15,2,new ValTP[] {ValTP.vt_bool},false).asValBit(true))
				.asValTpSeg(new FxAddrSeg(TP_CC_START,"Counter Contacts",235,254,3,new ValTP[] {ValTP.vt_bool},false).asValBit(true))
				) ;
		

		setAddrDef(new FxAddrDef("TR").asValTpSeg(new FxAddrSeg(TP_TR_START,"Timer Reset",0,55,2,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseAddrForceOnOff(TP_T_FORCE_ONOFF))) ;
		setAddrDef(new FxAddrDef("CR").asValTpSeg(new FxAddrSeg(TP_CR_START,"Counter Reset",0,15,2,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseAddrForceOnOff(TP_C_FORCE_ONOFF))
				.asValTpSeg(new FxAddrSeg(TP_CR_START,"Counter Reset",235,254,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseAddrForceOnOff(TP_C_FORCE_ONOFF))
				) ;
		
		setAddrDef(new FxAddrDef("T").asValTpSeg(new FxAddrSeg(TP_TV_START,"Timer Value",0,55,2,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true))) ;
		setAddrDef(new FxAddrDef("C").asValTpSeg(new FxAddrSeg(TP_CV16_START,"Counter Value",0,15,2,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true))
				.asValTpSeg(new FxAddrSeg(TP_CV32_START,"Counter Value 32Bit",235,254,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32},true).asAddrStepInt32(true).asBaseValStart(235))
				) ;
		
		setAddrDef(new FxAddrDef("D").asValTpSeg(new FxAddrSeg(TP_D_START,"Data Registers",0,31,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true))
				.asValTpSeg(new FxAddrSeg(TP_D_START,"Data Registers",0,30,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float},true).asAddrStepInt32(false)) 
				
				.asValTpSeg(new FxAddrSeg(TP_D_SPEC_START,"Special Data Registers",8000,8069,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true).asBaseValStart(8000)) 
				.asValTpSeg(new FxAddrSeg(TP_D_SPEC_START,"Special Data Registers",8000,8068,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float},true).asAddrStepInt32(false).asBaseValStart(8000))
				) ;
	}
	
}


class FxModel_FX extends FxModel
{

	public FxModel_FX()
	{
		super("fx","FX");
		

		setAddrDef(new FxAddrDef("S").asValTpSeg(new FxAddrSeg(FxModel.TP_S_START,"States",0,999,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseAddrForceOnOff(TP_S_FORCE_ONOFF)) 
				) ;
		
		setAddrDef(new FxAddrDef("X").asValTpSeg(new FxAddrSeg(FxModel.TP_X_START,"Inputs",0,0xFF,3,new ValTP[] {ValTP.vt_bool},false).asOctal(true).asValBit(true))) ;
		setAddrDef(new FxAddrDef("Y").asValTpSeg(new FxAddrSeg(TP_Y_START,"Outputs",0,0xFF,3,new ValTP[] {ValTP.vt_bool},true).asOctal(true).asValBit(true).asBaseAddrForceOnOff(TP_Y_FORCE_ONOFF))) ;
		
		setAddrDef(new FxAddrDef("M").asValTpSeg(new FxAddrSeg(TP_MC_START,"Auxiliary Relays",0,1535,4,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseAddrForceOnOff(TP_M_FORCE_ONOFF))
				.asValTpSeg(new FxAddrSeg(TP_MS_START,"Special Aux. Relays",8000,8255,4,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseValStart(8000).asBaseAddrForceOnOff(TP_MS_FORCE_ONOFF))
				) ;
		
		
		
		setAddrDef(new FxAddrDef("TS").asValTpSeg(new FxAddrSeg(TP_TC_START,"Timer Contacts",0,255,3,new ValTP[] {ValTP.vt_bool},false).asValBit(true))) ; 
		setAddrDef(new FxAddrDef("CS").asValTpSeg(new FxAddrSeg(TP_CC_START,"Counter Contacts",0,255,3,new ValTP[] {ValTP.vt_bool},false).asValBit(true))
				) ;
		

		setAddrDef(new FxAddrDef("TR").asValTpSeg(new FxAddrSeg(TP_TR_START,"Timer Reset",0,255,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseAddrForceOnOff(TP_T_FORCE_ONOFF))) ;
		setAddrDef(new FxAddrDef("CR").asValTpSeg(new FxAddrSeg(TP_CR_START,"Counter Reset",0,255,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseAddrForceOnOff(TP_C_FORCE_ONOFF))
				) ;
		
		setAddrDef(new FxAddrDef("T").asValTpSeg(new FxAddrSeg(TP_TV_START,"Timer Value",0,255,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true))) ;
		setAddrDef(new FxAddrDef("C").asValTpSeg(new FxAddrSeg(TP_CV16_START,"Counter Value",0,199,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true))
				.asValTpSeg(new FxAddrSeg(TP_CV32_START,"Counter Value 32Bit",200,255,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32},true).asAddrStepInt32(true).asBaseValStart(200))
				) ;
		
		setAddrDef(new FxAddrDef("D").asValTpSeg(new FxAddrSeg(TP_D_START,"Data Registers",0,999,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true))
				.asValTpSeg(new FxAddrSeg(TP_D_START,"Data Registers",0,998,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float},true).asAddrStepInt32(false)) 
				
				.asValTpSeg(new FxAddrSeg(TP_D_SPEC_START,"Special Data Registers",8000,8255,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true).asBaseValStart(8000)) 
				.asValTpSeg(new FxAddrSeg(TP_D_SPEC_START,"Special Data Registers",8000,8254,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float},true).asAddrStepInt32(false).asBaseValStart(8000))
				) ;
	}
	
}