package org.iottree.driver.zigbee.zspec;

public class ZDOUtil
{
	/**
	 * Get a the response cluster ID corresponding to a request.
	 * @param requestClusterId
	 * @returns Response cluster ID or undefined if unknown/invalid
	 */
	public static int getRespClusterId(int req_cluster_id)
	{
	    if (0x8000 < req_cluster_id || req_cluster_id == ZDOClusterId.END_DEVICE_ANNOUNCE.getIntVal()) {
	        return -1;
	    }

	    int ret_cid = req_cluster_id + 0x8000;

	    ZDOClusterId zdocid = ZDOClusterId.valOfInt(ret_cid) ;
	    if(zdocid==null)
	    	return -1 ;
	    
	    return ret_cid;
	}
	
	public static ZDOClusterId getRespClusterId(ZDOClusterId req_cid)
	{
		if (0x8000 < req_cid.getIntVal() || req_cid == ZDOClusterId.END_DEVICE_ANNOUNCE)
		{
	        return null;
	    }

	    int ret_cid = req_cid.getIntVal() + 0x8000;
	    return ZDOClusterId.valOfInt(ret_cid) ;
	}
}
