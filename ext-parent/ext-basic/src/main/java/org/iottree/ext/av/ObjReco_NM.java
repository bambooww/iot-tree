package org.iottree.ext.av;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.json.JSONObject;

/**
 * Object Recognition
 * 
 * @author jason.zhu
 *
 */
public class ObjReco_NM extends MNNodeMid
{

	@Override
	public String getTP()
	{
		return "obj_reco";
	}

	@Override
	public String getTPTitle()
	{
		return g("obj_reco");
	}

	@Override
	public String getColor()
	{
		return "#11caff";
	}

	@Override
	public String getIcon()
	{
		return "\\uf192";
	}
	
	@Override
	public int getOutNum()
	{
		return 1;
	}


	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JSONObject getParamJO()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		
	}


	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

}
