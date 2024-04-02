package org.iottree.driver.omron.hostlink.fins;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.ConnPt;
import org.iottree.core.DevAddr;
import org.iottree.core.DevDriver;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UATag;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.ValChker;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.util.Lan;
import org.iottree.driver.omron.hostlink.HLDriver;

/**
 * CPU Unit on a Network
 * 
 * it can support multi deivces
 * 
 * @author jason.zhu
 *
 */
public class HLFinsDriverNet extends HLFinsDriver
{

	@Override
	public boolean checkPropValue(String groupn, String itemn, String strv, StringBuilder failedr)
	{
		return false;
	}

	@Override
	public DevDriver copyMe()
	{
		return new HLFinsDriverNet();
	}

	@Override
	public String getName()
	{
		return "hostlink_fins_net";
	}

	@Override
	public String getTitle()
	{
		return "Hostlink FINS Network(TCP)";
	}

	@Override
	public List<PropGroup> getPropGroupsForDevInCh(UADev d)
	{
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		Lan lan = Lan.getPropLangInPk(this.getClass()) ;
		PropGroup gp = new PropGroup("fins_net",lan);//"Modbus Device");
		
		PropItem pi = new PropItem("sna",lan,PValTP.vt_int,false,null,null,0) ; //
		gp.addPropItem(pi);
		pi = new PropItem("sa1",lan,PValTP.vt_int,false,null,null,1) ; //
		gp.addPropItem(pi);
		
		pi = new PropItem("dev_id_ip",lan,PValTP.vt_str,false,null,null,"") ; //
		gp.addPropItem(pi);
		
		pi = new PropItem("dna",lan,PValTP.vt_int,false,null,null,0) ; //
		gp.addPropItem(pi);
		
		pi = new PropItem("da1",lan,PValTP.vt_int,false,null,null,0) ; //
		gp.addPropItem(pi);
		pi = new PropItem("da2",lan,PValTP.vt_int,false,null,null,0) ; //
		gp.addPropItem(pi);
		
//		pi.setValChker(new ValChker<Number>() {
//
//			@Override
//			public boolean checkVal(Number v, StringBuilder failedr)
//			{
//				int vi = v.intValue() ;
//				if(vi>=1&&vi<=255)
//					return true ;
//				failedr.append("modbus device address must between 1-255") ;
//				return false;
//			}});
		
		
		pgs.add(gp) ;

		return pgs ;
	}

	
	
}
