package org.iottree.core.msgnet.nodes;

import java.util.HashSet;
import java.util.List;

import org.iottree.core.*;
import org.iottree.core.util.*;
import org.iottree.core.basic.ValEvent;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.nodes.NS_TagEvtTrigger.MsgOutSty;
import org.iottree.core.util.Lan;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONArray;
import org.json.JSONObject;

public class NM_TagEvtReader extends MNNodeMid
{
	private HashSet<String> evt_ids = new HashSet<>();

	private boolean noOutWhenNoEvt = false;
	
	@Override
	public int getOutNum()
	{
		return 1;
	}

	@Override
	public String getTP()
	{
		return "tag_evt_reader";
	}

	@Override
	public String getTPTitle()
	{
		return g("tag_evt_reader");
	}

	@Override
	public String getColor()
	{
		return "#ff8566";
	}

	@Override
	public String getIcon()
	{
		return "\\uf0a2";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if (this.evt_ids == null || evt_ids.size() <= 0)
		{
			failedr.append("no tag event set");
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		jo.putOpt("evt_ids", evt_ids);
		jo.put("no_out_no_evt", this.noOutWhenNoEvt) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		JSONArray jarr = jo.optJSONArray("evt_ids");
		HashSet<String> ss = new HashSet<>();
		if (jarr != null)
		{
			int n = jarr.length();
			for (int i = 0; i < n; i++)
			{
				String id = jarr.getString(i);
				ss.add(id);
			}
		}
		this.evt_ids = ss;

		this.noOutWhenNoEvt = jo.optBoolean("no_out_no_evt",false) ;
		// clearCache();
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		if(this.evt_ids==null|this.evt_ids.size()<=0)
			return null;
		
		UAPrj prj = this.getPrj();
		JSONArray jarr = new JSONArray() ;
		for (UATag tag : prj.listTagsAll())
		{
			List<ValEvent> vas = tag.getValAlerts();
			if (vas == null || vas.size() <= 0)
				continue;
			for (ValEvent va : vas)
			{
				String id = va.getUid();
				if(!this.evt_ids.contains(id))
					continue ;
				if(!va.isEnable())
					continue ;
				if(!va.RT_is_triggered())
					continue ;
				JSONObject triggered_jo = va.RT_get_triggered_jo() ;
				if(triggered_jo==null)
					continue ;
				jarr.put(triggered_jo) ;
			}
		}
		
		if(noOutWhenNoEvt && jarr.length()<=0)
			return null ;

		return RTOut.createOutIdx().asIdxMsg(0,new MNMsg().asPayload(jarr));
	}

}
