package org.iottree.core.msgnet.store.evt_alert;

import java.util.Date;

import org.iottree.core.store.gdb.xorm.XORMClass;
import org.iottree.core.store.gdb.xorm.XORMProperty;
import org.iottree.core.util.xmldata.DataTranserJSON;
import org.iottree.core.util.xmldata.data_val;
import org.json.JSONObject;

@XORMClass(table_name = "evt_alert")
public class EvtAlertItem
{
	@XORMProperty(name = "AutoId",has_col=true,is_pk=true,max_len=20)
	@data_val(param_name = "AutoId")
	String autoId;

	@XORMProperty(name = "TriggerDT",has_col=true)
	@data_val(param_name = "TriggerDT")
	Date triggerDT;

	@XORMProperty(name = "ReleaseDT",has_col=true)
	@data_val(param_name = "ReleaseDT")
	Date releaseDT;

	@XORMProperty(name = "PrjName",has_col=true,max_len=40)
	@data_val(param_name = "PrjName")
	String prjName;

	@XORMProperty(name = "Tag",has_col=true,max_len=200)
	@data_val(param_name = "Tag")
	String tag;

	@XORMProperty(name = "AlertTP",has_col=true,max_len=20)
	@data_val(param_name = "AlertTP")
	String alertTP;

	@XORMProperty(name = "Value",has_col=true,max_len=20)
	@data_val(param_name = "Value")
	String val;

	@XORMProperty(name = "Level",has_col=true)
	@data_val(param_name = "Level")
	short lvl;

	@XORMProperty(name = "Prompt",max_len=200,has_col=true)
	@data_val(param_name = "Prompt")
	String prompt;

	public String getTag()
	{
		return this.tag;
	}

	public Date getTriggerDT()
	{
		return triggerDT;
	}

	public Date getReleaseDT()
	{
		return releaseDT;
	}
	
	public JSONObject toJO() throws Exception
	{
		JSONObject jo = DataTranserJSON.extractJSONFromObj(this) ;
		if(this.triggerDT!=null)
			jo.put("trigger_ms", this.triggerDT.getTime()) ;
		if(releaseDT!=null)
			jo.put("release_ms", this.releaseDT.getTime()) ;
		if(this.triggerDT!=null&&this.releaseDT!=null)
			jo.put("dur_ms", this.releaseDT.getTime()-this.triggerDT.getTime()) ;
		return jo ;
	}
}
