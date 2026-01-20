package org.iottree.core.msgnet.caller;

import org.iottree.core.msgnet.ResCaller;
import org.iottree.core.msgnet.ResCat;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;

public class JsCaller extends ResCaller
{

	public JsCaller(ResCat cat)
	{
		super(cat);
	}

	@Override
	public String getCallerName()
	{
		return "js_caller";
	}

	@Override
	public String getCallerTitle()
	{
		return "JS Caller";
	}

	@Override
	public JSONObject RT_onResCall(JSONObject input) throws Exception
	{
		return null;
	}

	@Override
	public JSONObject getParamJsonSchema()
	{
		return null;
	}

}
