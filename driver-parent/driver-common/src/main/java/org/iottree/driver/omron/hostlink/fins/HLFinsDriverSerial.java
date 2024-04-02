package org.iottree.driver.omron.hostlink.fins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.iottree.core.ConnException;
import org.iottree.core.ConnPt;
import org.iottree.core.DevAddr;
import org.iottree.core.DevDriver;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UATag;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.basic.ValChker;
import org.iottree.core.conn.ConnPtStream;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.iottree.driver.mitsubishi.fx.FxCmd;
import org.iottree.driver.mitsubishi.fx.FxDevItem;
import org.iottree.driver.omron.hostlink.HLAddr;
import org.iottree.driver.omron.hostlink.HLDriver;
import org.iottree.driver.omron.hostlink.HLModel;

/**
 * CPU Unit Directly Connected to the Host Computer
 * 
 * may only one device
 * 
 * @author jason.zhu
 *
 */
public class HLFinsDriverSerial extends HLFinsDriver
{
	
	@Override
	public boolean checkPropValue(String groupn, String itemn, String strv, StringBuilder failedr)
	{
		return false;
	}

	@Override
	public DevDriver copyMe()
	{
		return new HLFinsDriverSerial();
	}

	@Override
	public String getName()
	{
		return "hostlink_fins_ser";
	}

	@Override
	public String getTitle()
	{
		return "Hostlink FINS Serial";
	}
	
	public List<PropGroup> getPropGroupsForDevInCh(UADev d)
	{
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		Lan lan = Lan.getPropLangInPk(this.getClass()) ;
		PropGroup gp = new PropGroup("fins_net",lan);//"Modbus Device");
		
		PropItem pi = new PropItem("dev_net_id",lan,PValTP.vt_str,false,null,null,"0.0.0") ; //
		gp.addPropItem(pi);
		
		
		pi.setValChker(new ValChker<String>() {

			@Override
			public boolean checkVal(String v, StringBuilder failedr)
			{
				if(Convert.isNullOrEmpty(v))
				{
					failedr.append(lan.g("pi_dev_net_id")+" "+lan.g("cannot_empty"));
					return false;
				}
				List<String> ss = Convert.splitStrWith(v, ".") ;
				if(ss.size()!=3)
				{
					failedr.append(lan.g("pi_dev_net_id")+lan.g("fmt_be")+" x.x.x "+lan.g("num_pos") );
					return false;
				}
				for(String s:ss)
				{
					try
					{
						int iv = Convert.parseToInt32(s, -1) ;
						if(iv<0)
						{
							failedr.append(lan.g("pi_dev_net_id")+lan.g("fmt_be")+" x.x.x (0.1.0),"+lan.g("num_pos") );
							return false;
						}
					}
					catch(Exception ee)
					{
						failedr.append(lan.g("pi_dev_net_id")+lan.g("fmt_be")+" x.x.x (0.1.0),"+lan.g("num_pos") );
						return false;
					}
				}
				return true;
			}});
		
		
		pgs.add(gp) ;

		return pgs ;
	}
	
	
}
