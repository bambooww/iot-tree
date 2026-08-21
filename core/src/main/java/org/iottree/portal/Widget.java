package org.iottree.portal;

import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.UAPrj;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.msgnet.MNBase;
import org.iottree.core.msgnet.MNManager;
import org.iottree.core.msgnet.MNNet;
import org.iottree.core.msgnet.annotion.outer_api;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.json.JSONObject;

/**
 * Widget can be page or parts of portal page
 * @author jason.zhu
 *
 */
public abstract class Widget
{
	public static class ParamDef
	{
		public String name ;
		public ValTP valTP = ValTP.vt_str;
		
		public String defaultStrVal = null;
		
		public boolean nullable = false;
		
		public ParamDef(String name,ValTP vtp)
		{
			this.name = name ;
			this.valTP = vtp ;
		}
	}
	
	WidgetCatable owner ;
	
	
//	String name ;
//	
//	String title ;
//	
//	String desc ;
	
	public Widget(WidgetCatable owner)
	{
		this.owner = owner ;
//		this.name = oa.name() ;
//		if("cn".equals(Lan.getUsingLang()))
//		{
//			this.title = oa.title_cn() ;
//			this.desc = oa.desc_cn() ;
//			if(Convert.isNullOrEmpty(this.title))
//				this.title = oa.title_en() ;
//			if(Convert.isNullOrEmpty(this.desc))
//				this.desc = oa.desc_en() ;
//		}
//		else
//		{
//			this.title = oa.title_en() ;
//			this.desc = oa.desc_en() ;
//		}
	}
	
	public WidgetCatable getOwner()
	{
		return this.owner ;
	}
	
	public String getTPUid()
	{
		return this.owner.getWidgetCatTPUID()+"-"+this.getTPName() ;
	}
	
	public abstract String getTPJsPath();
	
	public abstract String getTPJsClz() ;
	
	public abstract String getTPName() ;
	
	public abstract String getTPTitle() ;
	
	public abstract String getInsName() ;
	
	public abstract String getInsTitle() ;
	
	public String getInsUID()
	{
		return this.owner.getWidgetCatPrefix()+"-"+this.owner.getWidgetCatInsUID()+"-"+this.getInsName() ;
	}
	
	public abstract List<ParamDef> getParamDefs() ;
	
	
	public String getDesc()
	{
		return "" ;
	}
	
	public static class EventResult
	{
		public boolean bUpdateV ; //true will trigger view update (call MODEL_readData)
		
		public String error ; //may trigger dlg prompt
		
		public EventResult(boolean b_updatev,String err)
		{
			this.bUpdateV = b_updatev ;
			this.error = err ;
		}
	}
	
	/**
	 * mvc model read data to update view
	 * @return
	 */
	public abstract JSONObject MODEL_readData(JSONObject view_pm) ;
	
	/**
	 * on view triggered event
	 * @param event
	 * @param evt_pm
	 */
	public abstract EventResult CTRL_onEvent(String event,JSONObject evt_pm) ;
	
//	@Override
//	public int compareTo(WidgetTP o)
//	{
//		return this.name.compareTo(o.name);
//	}
	
	
	// -- all widget 
	
	public static Widget getWidgetByInsUID(UAPrj prj,String ins_uid)
	{
		List<String> ss = Convert.splitStrWith(ins_uid, "-") ;
		if(ss.size()!=3)
			return null ;
		String catp = ss.get(0) ;
		String catins = ss.get(1) ;
		String ins_n = ss.get(2) ;
		switch(catp)
		{
		case "mn":
			List<String> net_noden = Convert.splitStrWith(catins, ".") ;
			if(net_noden.size()!=2)
				return null;
			MNNet net = prj.getMNManager().getNetByName(net_noden.get(0)) ;
			if(net==null)
				return null ;
			MNBase node = net.getItemById(net_noden.get(1)) ;
			if(node==null)
				return null ;
			LinkedHashMap<String,Widget> n2w = node.getWidgets();
			if(n2w==null)
				return null ;
			return n2w.get(ins_n) ;
		}
		return null;
	}
}
