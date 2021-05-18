package org.iottree.core.util.xmldata;

import org.json.*;

public interface IJSONObj
{
	public JSONObject toJSONObj() ;
	
	public boolean fromJSONObj(JSONObject job) ;
}
