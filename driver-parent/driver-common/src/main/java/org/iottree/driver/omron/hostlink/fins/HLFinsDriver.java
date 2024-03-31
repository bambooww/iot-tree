package org.iottree.driver.omron.hostlink.fins;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.ConnPt;
import org.iottree.core.DevAddr;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.DevDriver.Model;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.conn.ConnPtStream;
import org.iottree.core.util.Lan;
import org.iottree.driver.omron.hostlink.HLAddr;
import org.iottree.driver.omron.hostlink.HLDriver;
import org.iottree.driver.omron.hostlink.HLModel;

public abstract class HLFinsDriver extends HLDriver
{
	long readTimeout = 3000 ;
	
	
	private long cmdInterval = 10 ;

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtStream.class;
	}

	@Override
	public boolean supportDevFinder()
	{
		return false;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevDef()
	{
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		
		PropGroup gp = null;
		
		Lan lan = Lan.getPropLangInPk(this.getClass()) ;
		gp = new PropGroup("timing",lan);//"Timing");

		gp.addPropItem(new PropItem("req_to",lan,PValTP.vt_int,false,null,null,1000)); //"Request Timeout(millisecond)",""
		gp.addPropItem(new PropItem("failed_tryn",lan,PValTP.vt_int,false,null,null,3)); //"Fail after successive times",""
		gp.addPropItem(new PropItem("recv_to",lan,PValTP.vt_int,false,null,null,200)); //"Receive response timeout(millisecond)",""
		gp.addPropItem(new PropItem("inter_req",lan,PValTP.vt_int,false,null,null,0)); //"Inter-request millisecond",""
		pgs.add(gp) ;
		
		gp = new PropGroup("block_size",lan);//"Block Sizes");
		gp.addPropItem(new PropItem("bit",lan,PValTP.vt_int,false,null,null,32)); //"Output Coils",""
		gp.addPropItem(new PropItem("word",lan,PValTP.vt_int,false,null,null,32)); //"Internal Registers",""
		pgs.add(gp) ;
		
		return pgs;
	}

	@Override
	public List<PropGroup> getPropGroupsForCh(UACh ch)
	{
		return null;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevInCh(UADev d)
	{
		return null;
	}
	

	@Override
	public List<Model> getDevModels()
	{
		return HLModel.getModelsAll() ;
	}

	private static HLAddr HL_Addr = new HLAddr() ;
	
	private static ValTP[] LIMIT_VTPS = new ValTP[] {ValTP.vt_bool,ValTP.vt_int16,ValTP.vt_uint16,ValTP.vt_int32,ValTP.vt_uint32} ;

	@Override
	public DevAddr getSupportAddr()
	{
		return HL_Addr;
	}
	
	@Override
	public ValTP[] getLimitValTPs(UADev dev)
	{
		return LIMIT_VTPS ;
	}


	public long getReadTimeout()
	{
		return this.readTimeout ;
	}
	
	public long getCmdInterval()
	{
		return this.cmdInterval ;
	}
}
