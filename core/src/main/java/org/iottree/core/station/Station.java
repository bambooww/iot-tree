package org.iottree.core.station;

import java.util.List;

import org.iottree.core.IOCBox;
import org.iottree.core.UANode;
import org.json.JSONObject;

/**
 * a station is one iottree server。which can be connected to another station as it's parent station.
 * or it can accept other stations connection.
 * @author zzj
 *
 */
public class Station extends UANode
{

	@Override
	public List<UANode> getSubNodes()
	{
		
		return null;
	}

	@Override
	protected boolean chkValid()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onPropNodeValueChged()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public JSONObject OC_getPropsJSON()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void OC_setPropsJSON(JSONObject jo)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean OC_supportSub()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<IOCBox> OC_getSubs()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
