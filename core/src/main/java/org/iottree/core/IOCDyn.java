package org.iottree.core;

import org.json.JSONObject;

/**
 * object implements IOC may implements this ether
 * and can provider dyn data in IOC tree struct
 * @author zzj
 *
 */
public interface IOCDyn extends IOC
{
	public JSONObject OC_getDynJSON(long lastdt) ;
}
