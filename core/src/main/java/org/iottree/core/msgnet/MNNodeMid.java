package org.iottree.core.msgnet;

import java.util.List;

import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

public abstract class MNNodeMid extends MNNode
{
	@Override
	public boolean supportInConn()
	{
		return true;
	}
}
