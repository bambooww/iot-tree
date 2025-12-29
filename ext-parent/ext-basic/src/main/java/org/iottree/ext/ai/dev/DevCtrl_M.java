package org.iottree.ext.ai.dev;

import org.iottree.core.util.*;
import org.iottree.core.util.temp.TxtTemplate;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.basic.ValEventTp;
import org.iottree.core.msgnet.MNManager;
import org.iottree.core.msgnet.MNModule;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * @author jason.zhu
 *
 */
public class DevCtrl_M extends MNModule
{
	static Lan lan = Lan.getLangInPk(ValEventTp.class) ;
	
	public static enum Role
	{
		normal,
		home_ai;
		
		public String getTitle()
		{
			return lan.g(this.name()) ;
		}
	}
	
	Role role = Role.normal ;
	
	//ArrayList<DevItem> devItems = null ;
	

	
	@Override
	public String getTP()
	{
		return "devctrl";
	}

	@Override
	public String getTPTitle()
	{
		return "AI Device Ctrl";
	}

	@Override
	public String getColor()
	{
		return "#3989D7";
	}
	
	@Override
	public String getTitleColor()
	{
		return "#eeeeee";
	}

	@Override
	public String getIcon()
	{
		return "\\uf0a0";
	}
	
	
	public List<DevCtrlDevItem_NS> listRelatedDevItem()
	{
		return this.listRelatedNodes(DevCtrlDevItem_NS.class) ;
	}
	
	public DevCtrlDevItem_NS getRelatedDevItem(String dev_id)
	{
		List<DevCtrlDevItem_NS> nss = listRelatedDevItem() ;
		for(DevCtrlDevItem_NS ns:nss)
		{
			if(dev_id.equals(ns.getDevId()))
				return ns ;
		}
		return null ;
	}
	
	public List<DevCtrlDevItem_NS> findRelatedDevItems(String dev_id)
	{
		List<DevCtrlDevItem_NS> nss = listRelatedDevItem() ;
		ArrayList<DevCtrlDevItem_NS> rets = new ArrayList<>();
		for(DevCtrlDevItem_NS ns:nss)
		{
			if(dev_id.equals(ns.getDevId()))
			{
				rets.add(ns) ;
			}
		}
		return rets ;
	}
	
	public JSONArray getRelatedDevItemJArr()
	{
		JSONArray jarr = new JSONArray() ;
		List<DevCtrlDevItem_NS> devs = listRelatedDevItem() ;
		if(devs==null)
			return jarr ;
		StringBuilder failedr = new StringBuilder() ;
		for(DevCtrlDevItem_NS dev:devs)
		{
			JSONObject tmpjo = dev.toPromptListJO(failedr) ;
			if(tmpjo==null)
				continue ;
			jarr.put(tmpjo) ;
		}
		return jarr ;
	}
	
	public String getSystemMsg()
	{
		TxtTemplate tt = getOrLoadPromptTemp() ;
		if(tt==null)
			return null ;
		HashMap<String,String> blk2val = new HashMap<>() ;
		JSONArray dev_list_jarr = getRelatedDevItemJArr();
		System.out.println(dev_list_jarr.toString(2));
		blk2val.put("device_list_json",dev_list_jarr.toString(2)) ;
		return tt.getContStr(blk2val) ;
	}
	
	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
//		if(Convert.isNullOrEmpty(this.systemMsg))
//		{
//			failedr.append("no system message set") ;
//			return false;
//		}
		
		List<DevCtrlDevItem_NS> devitems = listRelatedDevItem() ;
		if(devitems==null||devitems.size()<=0)
		{
			failedr.append("no Device Item node set") ;
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		//jo.putOpt("sys_msg",this.systemMsg) ;
		jo.put("role", this.role.name()) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		//this.systemMsg = jo.optString("sys_msg") ;
		this.role = Role.valueOf(jo.optString("role","normal")) ;
	}


	private static TxtTemplate promptTemp = null ;
	
	private static JSONObject schemeCmd = null ; 

	private static TxtTemplate getOrLoadPromptTemp()
	{
		if(promptTemp!=null)
			return promptTemp ;

		try(InputStream inps = DevCtrl_M.class.getResourceAsStream("/org/iottree/ext/ai/dev/DevCtrl_Prompt.md"))
		{
			if(inps==null)
				return null;
			
			byte[] buf = Convert.readStreamBuffer(inps) ;
			String txt = new String(buf,"UTF-8") ;
			return promptTemp =new TxtTemplate("",txt) ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			return null ;
		}
	}
	
	static JSONObject getOrLoadSchemaCmd()
	{
		if(schemeCmd!=null)
			return schemeCmd ;

		try(InputStream inps = DevCtrl_M.class.getResourceAsStream("/org/iottree/ext/ai/dev/DevCtrl_SchemeCmd.json"))
		{
			if(inps==null)
				return null;
			
			byte[] buf = Convert.readStreamBuffer(inps) ;
			String txt = new String(buf,"UTF-8") ;
			return schemeCmd = new JSONObject(txt) ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			return null ;
		}
	}
}
