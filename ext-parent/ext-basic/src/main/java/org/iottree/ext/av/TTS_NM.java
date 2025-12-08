package org.iottree.ext.av;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.json.JSONObject;

/**
 * Text to speech
 * @author jason.zhu
 *
 */
public class TTS_NM extends MNNodeMid
{

	@Override
	public String getTP()
	{
		return "tts";
	}

	@Override
	public String getTPTitle()
	{
		return g("tts");
	}

	@Override
	public String getColor()
	{
		return "#11caff";
	}

	@Override
	public String getIcon()
	{
		return "\\uf027";
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
