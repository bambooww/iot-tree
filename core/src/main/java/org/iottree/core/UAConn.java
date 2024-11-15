package org.iottree.core;

import java.util.List;

import org.iottree.core.util.xmldata.*;
import org.json.JSONObject;

/**
 * UAConn belong to Rep
 * 
 * @author jason.zhu
 *
 */
@data_class
public class UAConn extends UANode
{
	public static final String MEMBER_TP = "ua_conn" ;
	
	@data_val(param_name = "tp")
	String connTp = "" ;// tcp client  ,tcp server,com etc

	
	public UAConn()
	{}
	
	public UAConn(String name,String title,String desc,String conntp)
	{
		super(name,title,desc);
		this.connTp = conntp ;
	}
	
	public String getNodeTp()
	{
		return "";
	}
	
	public String getMemberTp()
	{
		return MEMBER_TP ;
	}
	
	public String getConnTp()
	{
		return connTp ;
	}


	@Override
	public boolean OC_supportSub()
	{
		return false;
	}

	@Override
	public List<IOCBox> OC_getSubs()
	{
		return null;
	}
	
	public void start()
	{}
	
	public void stop()
	{
		
	}
	
	public boolean isRunning()
	{
		return false;
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
	public List<UANode> getSubNodes()
	{
		// TODO Auto-generated method stub
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
}
