package org.iottree.ext.ai.edge;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.json.JSONObject;

public class IOTTE_ImgBlkOCR_NM extends MNNodeMid
{
	@Override
	public String getTP()
	{
		return "iottree_edge_imgblkocr";
	}

	@Override
	public String getTPTitle()
	{
		return g("iottree_edge_imgblkocr");
	}

	@Override
	public String getColor()
	{
		return "#5B9CD7";
	}
	
	@Override
	public String getTitleColor()
	{
		return "#eeeeee";
	}

	@Override
	public String getIcon()
	{
		return "\\uf5cb";
	}
	
	@Override
	public int getOutNum()
	{
		return 2;
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
		// TODO Auto-generated method stub
		
	}


	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		return null;
	}

}
