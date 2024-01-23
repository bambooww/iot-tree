package org.iottree.driver.mitsubishi.fx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.DevDriver;
import org.iottree.core.UAVal.ValTP;

public class FxModel extends DevDriver.Model
{
	private HashMap<String,FxAddrDef> prefix2addrdef = new HashMap<>() ; 
			
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
		//Integer iv = addrseg.matchAddr(num_str) ;
		if(iv==null)
		{
			failedr.append("no AddrSeg match with ValTP="+vtp.name()) ;
			return null ;
		}
		return new FxAddr(prefix+num_str,vtp,this,prefix,iv,addrseg.bValBit,addrseg.digitNum,addrseg.bOctal)
				.asDef(def, addrseg);
	}
	
	
	public HashMap<FxAddrSeg,List<FxAddr>> filterAndSortAddrs(String prefix,List<FxAddr> addrs)
	{
		FxAddrDef def = this.getAddrDef(prefix) ;
		if(def==null)
			return null ;
		HashMap<FxAddrSeg,List<FxAddr>> rets = new HashMap<>() ;
		ArrayList<FxAddr> r = new ArrayList<>() ;
		for(FxAddr ma:addrs)
		{
			if(!prefix.equals(ma.prefix))
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
}

class FxModel_FX3U extends FxModel
{

	public FxModel_FX3U()
	{
		super("fx3u", "FX3U");

		setAddrDef(new FxAddrDef("X").asValTpSeg(new FxAddrSeg(FxAddr.TP_X_START,"Inputs",0,0xff,3,new ValTP[] {ValTP.vt_bool},false).asOctal(true).asValBit(true))) ;  //test ok
		setAddrDef(new FxAddrDef("Y").asValTpSeg(new FxAddrSeg(FxAddr.TP_Y_START,"Outputs",0,0xff,3,new ValTP[] {ValTP.vt_bool},true).asOctal(true).asValBit(true))) ;  //test ok
		setAddrDef(new FxAddrDef("M").asValTpSeg(new FxAddrSeg(FxAddr.TP_MC_START,"Auxiliary Relays",0,1023,4,new ValTP[] {ValTP.vt_bool},true).asValBit(true))  //test ok
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_PM_START,"Auxiliary Relays",1024,2047,4,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseValStart(1024)) // test failed - need FX3U doc
				//.asValTpSeg(new AddrSeg(FxAddr.,"Auxiliary Relays",1023,2046,4,new ValTP[] {ValTP.vt_bool},true).asValBit(true)) // TBD
				
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_MS_START,"Special Aux. Relays",8000,8255,4,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseValStart(8000)) //test ok
				//.asValTpSeg(new AddrSeg(FxAddr.TP_MS_START,"Special Aux. Relays",8256,8511,4,new ValTP[] {ValTP.vt_bool},true).asValBit(true)) // TBD
				) ;
		
		setAddrDef(new FxAddrDef("S").asValTpSeg(new FxAddrSeg(FxAddr.TP_S_START,"States",0,999,4,new ValTP[] {ValTP.vt_bool},true).asValBit(true))  //test ok
				//.asValTpSeg(new FxAddrSeg(FxAddr.TP_S_START,"States",0,4095,4,new ValTP[] {ValTP.vt_bool},true).asValBit(true))  //TBD
				) ;
		
		setAddrDef(new FxAddrDef("TS").asValTpSeg(new FxAddrSeg(FxAddr.TP_TC_START,"Timer Contacts",0,511,3,new ValTP[] {ValTP.vt_bool},false).asValBit(true))) ; //test ok
		setAddrDef(new FxAddrDef("CS").asValTpSeg(new FxAddrSeg(FxAddr.TP_CC_START,"Counter Contacts",0,255,3,new ValTP[] {ValTP.vt_bool},false).asValBit(true))) ; //test ok
		
		setAddrDef(new FxAddrDef("TC").asValTpSeg(new FxAddrSeg(FxAddr.TP_TCOIL_START,"Timer Coil",0,511,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true))) ;  //0-255 ok    255-511 no ok
		setAddrDef(new FxAddrDef("CC").asValTpSeg(new FxAddrSeg(FxAddr.TP_CCOIL_START,"Counter Coil",0,255,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true))) ; //test ok
		
		setAddrDef(new FxAddrDef("TR").asValTpSeg(new FxAddrSeg(FxAddr.TP_TR_START,"Timer Reset",0,511,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true))) ; // not test
		setAddrDef(new FxAddrDef("CR").asValTpSeg(new FxAddrSeg(FxAddr.TP_CR_START,"Counter Reset",0,255,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true))) ; // not test
		
		setAddrDef(new FxAddrDef("T").asValTpSeg(new FxAddrSeg(FxAddr.TP_TV_START,"Timer Value",0,511,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true))) ; //0-255 ok   255-511 not ok
		setAddrDef(new FxAddrDef("C").asValTpSeg(new FxAddrSeg(FxAddr.TP_CV16_START,"Counter Value",0,199,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true)) // test ok
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_CV32_START,"Counter Value 32Bit",200,255,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32},true).asAddrStepInt32(true).asBaseValStart(200)) //test ok
				) ;
		
		setAddrDef(new FxAddrDef("D").asValTpSeg(new FxAddrSeg(FxAddr.TP_D_START,"Data Registers",0,7999,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true)) //0-7999 test ok
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_D_START,"Data Registers",0,7998,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float},true).asAddrStepInt32(false))  //0-7998 test ok
				
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_D_SPEC_START,"Special Data Registers",8000,8511,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true).asBaseValStart(8000))  //0-255 test ok 256-511 not ok
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_D_SPEC_START,"Special Data Registers",8000,8510,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float},true).asAddrStepInt32(false).asBaseValStart(8000)) //0-255 test ok 256-511 not ok
				) ;
	}
	
}

//not test
class FxModel_FX2N extends FxModel
{

	public FxModel_FX2N()
	{
		super("fx2n","FX2N");
		

		setAddrDef(new FxAddrDef("X").asValTpSeg(new FxAddrSeg(FxAddr.TP_X_START,"Inputs",0,0xff,3,new ValTP[] {ValTP.vt_bool},false).asOctal(true).asValBit(true))) ;  //
		setAddrDef(new FxAddrDef("Y").asValTpSeg(new FxAddrSeg(FxAddr.TP_Y_START,"Outputs",0,0xff,3,new ValTP[] {ValTP.vt_bool},true).asOctal(true).asValBit(true))) ;  //
		setAddrDef(new FxAddrDef("M").asValTpSeg(new FxAddrSeg(FxAddr.TP_MC_START,"Auxiliary Relays",0,1023,4,new ValTP[] {ValTP.vt_bool},true).asValBit(true))  //
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_PM_START,"Auxiliary Relays",1024,3071,4,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseValStart(1024)) // need FX2U doc
				//.asValTpSeg(new AddrSeg(FxAddr.,"Auxiliary Relays",1023,2046,4,new ValTP[] {ValTP.vt_bool},true).asValBit(true)) // TBD
				
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_MS_START,"Special Aux. Relays",8000,8255,4,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseValStart(8000)) //
				) ;
		
		setAddrDef(new FxAddrDef("S").asValTpSeg(new FxAddrSeg(FxAddr.TP_S_START,"States",0,999,4,new ValTP[] {ValTP.vt_bool},true).asValBit(true))  //
				) ;
		
		setAddrDef(new FxAddrDef("TS").asValTpSeg(new FxAddrSeg(FxAddr.TP_TC_START,"Timer Contacts",0,255,3,new ValTP[] {ValTP.vt_bool},false).asValBit(true))) ;
		setAddrDef(new FxAddrDef("CS").asValTpSeg(new FxAddrSeg(FxAddr.TP_CC_START,"Counter Contacts",0,255,3,new ValTP[] {ValTP.vt_bool},false).asValBit(true))) ; //
		
		setAddrDef(new FxAddrDef("TC").asValTpSeg(new FxAddrSeg(FxAddr.TP_TCOIL_START,"Timer Coil",0,255,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true))) ;  //
		setAddrDef(new FxAddrDef("CC").asValTpSeg(new FxAddrSeg(FxAddr.TP_CCOIL_START,"Counter Coil",0,255,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true))) ; //
		
		setAddrDef(new FxAddrDef("TR").asValTpSeg(new FxAddrSeg(FxAddr.TP_TR_START,"Timer Reset",0,255,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true))) ; //
		setAddrDef(new FxAddrDef("CR").asValTpSeg(new FxAddrSeg(FxAddr.TP_CR_START,"Counter Reset",0,255,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true))) ; // 
		
		setAddrDef(new FxAddrDef("T").asValTpSeg(new FxAddrSeg(FxAddr.TP_TV_START,"Timer Value",0,255,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true))) ; //
		setAddrDef(new FxAddrDef("C").asValTpSeg(new FxAddrSeg(FxAddr.TP_CV16_START,"Counter Value",0,199,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true)) //
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_CV32_START,"Counter Value 32Bit",200,255,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32},true).asAddrStepInt32(true).asBaseValStart(200))
				) ;
		
		setAddrDef(new FxAddrDef("D").asValTpSeg(new FxAddrSeg(FxAddr.TP_D_START,"Data Registers",0,7999,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true)) //
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_D_START,"Data Registers",0,7998,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float},true).asAddrStepInt32(false))  //
				
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_D_SPEC_START,"Special Data Registers",8000,8255,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true).asBaseValStart(8000))  //
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_D_SPEC_START,"Special Data Registers",8000,8254,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float},true).asAddrStepInt32(false).asBaseValStart(8000)) //
				) ;
	}
	
}

// not test
class FxModel_FX0N extends FxModel
{

	public FxModel_FX0N()
	{
		super("fx0n", "FX0N");
		

		setAddrDef(new FxAddrDef("X").asValTpSeg(new FxAddrSeg(FxAddr.TP_X_START,"Inputs",0,0x7f,3,new ValTP[] {ValTP.vt_bool},false).asOctal(true).asValBit(true))) ;  //
		setAddrDef(new FxAddrDef("Y").asValTpSeg(new FxAddrSeg(FxAddr.TP_Y_START,"Outputs",0,0x7f,3,new ValTP[] {ValTP.vt_bool},true).asOctal(true).asValBit(true))) ;  //
		setAddrDef(new FxAddrDef("M").asValTpSeg(new FxAddrSeg(FxAddr.TP_MC_START,"Auxiliary Relays",0,511,4,new ValTP[] {ValTP.vt_bool},true).asValBit(true))  //
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_MS_START,"Special Aux. Relays",8000,8255,4,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseValStart(8000)) //
				) ;
		
		setAddrDef(new FxAddrDef("S").asValTpSeg(new FxAddrSeg(FxAddr.TP_S_START,"States",0,127,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true))  //
				) ;
		
		setAddrDef(new FxAddrDef("TS").asValTpSeg(new FxAddrSeg(FxAddr.TP_TC_START,"Timer Contacts",0,63,2,new ValTP[] {ValTP.vt_bool},false).asValBit(true))) ;
		setAddrDef(new FxAddrDef("CS").asValTpSeg(new FxAddrSeg(FxAddr.TP_CC_START,"Counter Contacts",0,31,2,new ValTP[] {ValTP.vt_bool},false).asValBit(true))
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_CC_START,"Counter Contacts",235,254,3,new ValTP[] {ValTP.vt_bool},false).asValBit(true).asBaseValStart(235))
				) ; //
		
		setAddrDef(new FxAddrDef("TC").asValTpSeg(new FxAddrSeg(FxAddr.TP_TCOIL_START,"Timer Coil",0,63,2,new ValTP[] {ValTP.vt_bool},true).asValBit(true))) ;  //
		setAddrDef(new FxAddrDef("CC").asValTpSeg(new FxAddrSeg(FxAddr.TP_CCOIL_START,"Counter Coil",0,31,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true))
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_CCOIL_START,"Counter Coil",235,254,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseValStart(235))
				) ; //
		
		setAddrDef(new FxAddrDef("TR").asValTpSeg(new FxAddrSeg(FxAddr.TP_TR_START,"Timer Reset",0,63,2,new ValTP[] {ValTP.vt_bool},true).asValBit(true))) ; //
		setAddrDef(new FxAddrDef("CR").asValTpSeg(new FxAddrSeg(FxAddr.TP_CR_START,"Counter Reset",0,31,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true))
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_CR_START,"Counter Reset",0,235,254,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseValStart(235))
				) ; // 
		
		setAddrDef(new FxAddrDef("T").asValTpSeg(new FxAddrSeg(FxAddr.TP_TV_START,"Timer Value",0,63,2,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true))) ; //
		setAddrDef(new FxAddrDef("C").asValTpSeg(new FxAddrSeg(FxAddr.TP_CV16_START,"Counter Value",0,31,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true)) //
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_CV32_START,"Counter Value 32Bit",235,254,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32},true).asAddrStepInt32(true).asBaseValStart(235))
				) ;
		
		setAddrDef(new FxAddrDef("D").asValTpSeg(new FxAddrSeg(FxAddr.TP_D_START,"Data Registers",0,255,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true)) //
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_D_START,"Data Registers",0,254,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float},true).asAddrStepInt32(false))  //
				
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_D_SPEC_START,"Special Data Registers",8000,8255,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true).asBaseValStart(8000))  //
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_D_SPEC_START,"Special Data Registers",8000,8254,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float},true).asAddrStepInt32(false).asBaseValStart(8000)) //
				) ;
	}
	
}

class FxModel_FX0 extends FxModel
{

	public FxModel_FX0()
	{
		super("fx0", "FX0");
		

		setAddrDef(new FxAddrDef("X").asValTpSeg(new FxAddrSeg(FxAddr.TP_X_START,"Inputs",0,0xf,3,new ValTP[] {ValTP.vt_bool},false).asOctal(true).asValBit(true))) ;  //
		setAddrDef(new FxAddrDef("Y").asValTpSeg(new FxAddrSeg(FxAddr.TP_Y_START,"Outputs",0,0xf,3,new ValTP[] {ValTP.vt_bool},true).asOctal(true).asValBit(true))) ;  //
		setAddrDef(new FxAddrDef("M").asValTpSeg(new FxAddrSeg(FxAddr.TP_MC_START,"Auxiliary Relays",0,511,4,new ValTP[] {ValTP.vt_bool},true).asValBit(true))  //
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_MS_START,"Special Aux. Relays",8000,8255,4,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseValStart(8000)) //
				) ;
		
		setAddrDef(new FxAddrDef("S").asValTpSeg(new FxAddrSeg(FxAddr.TP_S_START,"States",0,63,2,new ValTP[] {ValTP.vt_bool},true).asValBit(true))  //
				) ;
		
		setAddrDef(new FxAddrDef("TS").asValTpSeg(new FxAddrSeg(FxAddr.TP_TC_START,"Timer Contacts",0,55,2,new ValTP[] {ValTP.vt_bool},false).asValBit(true))) ;
		setAddrDef(new FxAddrDef("CS").asValTpSeg(new FxAddrSeg(FxAddr.TP_CC_START,"Counter Contacts",0,15,3,new ValTP[] {ValTP.vt_bool},false).asValBit(true))
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_CC_START,"Counter Contacts",235,254,3,new ValTP[] {ValTP.vt_bool},false).asValBit(true).asBaseValStart(235))
				) ; //
		
		setAddrDef(new FxAddrDef("TC").asValTpSeg(new FxAddrSeg(FxAddr.TP_TCOIL_START,"Timer Coil",0,55,2,new ValTP[] {ValTP.vt_bool},true).asValBit(true))) ;  //
		setAddrDef(new FxAddrDef("CC").asValTpSeg(new FxAddrSeg(FxAddr.TP_CCOIL_START,"Counter Coil",0,15,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true))
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_CCOIL_START,"Counter Coil",235,254,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseValStart(235))
				) ; //
		
		setAddrDef(new FxAddrDef("TR").asValTpSeg(new FxAddrSeg(FxAddr.TP_TR_START,"Timer Reset",0,55,2,new ValTP[] {ValTP.vt_bool},true).asValBit(true))) ; //
		setAddrDef(new FxAddrDef("CR").asValTpSeg(new FxAddrSeg(FxAddr.TP_CR_START,"Counter Reset",0,15,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true))
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_CR_START,"Counter Reset",0,235,254,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseValStart(235))
				) ; // 
		
		setAddrDef(new FxAddrDef("T").asValTpSeg(new FxAddrSeg(FxAddr.TP_TV_START,"Timer Value",0,55,2,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true))) ; //
		setAddrDef(new FxAddrDef("C").asValTpSeg(new FxAddrSeg(FxAddr.TP_CV16_START,"Counter Value",0,15,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true)) //
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_CV32_START,"Counter Value 32Bit",235,254,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32},true).asAddrStepInt32(true).asBaseValStart(235))
				) ;
		
		setAddrDef(new FxAddrDef("D").asValTpSeg(new FxAddrSeg(FxAddr.TP_D_START,"Data Registers",0,31,2,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true)) //
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_D_START,"Data Registers",0,30,2,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float},true).asAddrStepInt32(false))  //
				
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_D_SPEC_START,"Special Data Registers",8000,8069,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true).asBaseValStart(8000))  //
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_D_SPEC_START,"Special Data Registers",8000,8068,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float},true).asAddrStepInt32(false).asBaseValStart(8000)) //
				) ;
	}
	
}


class FxModel_FX extends FxModel
{

	public FxModel_FX()
	{
		super("fx","FX");
		

		setAddrDef(new FxAddrDef("X").asValTpSeg(new FxAddrSeg(FxAddr.TP_X_START,"Inputs",0,0xff,3,new ValTP[] {ValTP.vt_bool},false).asOctal(true).asValBit(true))) ;  //
		setAddrDef(new FxAddrDef("Y").asValTpSeg(new FxAddrSeg(FxAddr.TP_Y_START,"Outputs",0,0xff,3,new ValTP[] {ValTP.vt_bool},true).asOctal(true).asValBit(true))) ;  //
		setAddrDef(new FxAddrDef("M").asValTpSeg(new FxAddrSeg(FxAddr.TP_MC_START,"Auxiliary Relays",0,1535,4,new ValTP[] {ValTP.vt_bool},true).asValBit(true))  //
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_MS_START,"Special Aux. Relays",8000,8255,4,new ValTP[] {ValTP.vt_bool},true).asValBit(true).asBaseValStart(8000)) //
				) ;
		
		setAddrDef(new FxAddrDef("S").asValTpSeg(new FxAddrSeg(FxAddr.TP_S_START,"States",0,999,4,new ValTP[] {ValTP.vt_bool},true).asValBit(true))  //
				) ;
		
		setAddrDef(new FxAddrDef("TS").asValTpSeg(new FxAddrSeg(FxAddr.TP_TC_START,"Timer Contacts",0,255,3,new ValTP[] {ValTP.vt_bool},false).asValBit(true))) ;
		setAddrDef(new FxAddrDef("CS").asValTpSeg(new FxAddrSeg(FxAddr.TP_CC_START,"Counter Contacts",0,255,3,new ValTP[] {ValTP.vt_bool},false).asValBit(true))) ; //
		
		setAddrDef(new FxAddrDef("TC").asValTpSeg(new FxAddrSeg(FxAddr.TP_TCOIL_START,"Timer Coil",0,255,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true))) ;  //
		setAddrDef(new FxAddrDef("CC").asValTpSeg(new FxAddrSeg(FxAddr.TP_CCOIL_START,"Counter Coil",0,255,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true))) ; //
		
		setAddrDef(new FxAddrDef("TR").asValTpSeg(new FxAddrSeg(FxAddr.TP_TR_START,"Timer Reset",0,255,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true))) ; //
		setAddrDef(new FxAddrDef("CR").asValTpSeg(new FxAddrSeg(FxAddr.TP_CR_START,"Counter Reset",0,255,3,new ValTP[] {ValTP.vt_bool},true).asValBit(true))) ; // 
		
		setAddrDef(new FxAddrDef("T").asValTpSeg(new FxAddrSeg(FxAddr.TP_TV_START,"Timer Value",0,255,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true))) ; //
		setAddrDef(new FxAddrDef("C").asValTpSeg(new FxAddrSeg(FxAddr.TP_CV16_START,"Counter Value",0,199,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true)) //
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_CV32_START,"Counter Value 32Bit",200,255,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32},true).asAddrStepInt32(true).asBaseValStart(200))
				) ;
		
		setAddrDef(new FxAddrDef("D").asValTpSeg(new FxAddrSeg(FxAddr.TP_D_START,"Data Registers",0,999,3,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true)) //
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_D_START,"Data Registers",0,998,3,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float},true).asAddrStepInt32(false))  //
				
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_D_SPEC_START,"Special Data Registers",8000,8255,4,new ValTP[] {ValTP.vt_int16,ValTP.vt_uint16},true).asBaseValStart(8000))  //
				.asValTpSeg(new FxAddrSeg(FxAddr.TP_D_SPEC_START,"Special Data Registers",8000,8254,4,new ValTP[] {ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_float},true).asAddrStepInt32(false).asBaseValStart(8000)) //
				) ;
	}
	
}