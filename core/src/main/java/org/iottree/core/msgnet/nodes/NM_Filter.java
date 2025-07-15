package org.iottree.core.msgnet.nodes;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.ILang;
import org.json.JSONObject;

/**
 * filter json obj to simple value
 * @author jason.zhu
 *
 */
public class NM_Filter extends MNNodeMid implements ILang
{
	@Override
	public String getTP()
	{
		return "filter";
	}

	@Override
	public String getTPTitle()
	{
		return g("filter");
	}

	@Override
	public String getColor()
	{
		return "#3f9a62";
	}

	@Override
	public String getIcon()
	{
		return "\\uf0b0";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return false;
	}

	@Override
	public JSONObject getParamJO()
	{
		return null;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		
	}

	@Override
	public int getOutNum()
	{
		return 1;
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		return null;
	}

}
