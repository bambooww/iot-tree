package org.iottree.portal;

import org.json.JSONObject;

/**
 * 外界实现此接口，提供页面动态数据。
 * 
 * @author zzj
 *
 */
public interface IPortalPageBlkDDyn
{
	public String getPageBlkDDynUID() ;
	
	public JSONObject RT_getPageBlkData() throws Exception ;
}
