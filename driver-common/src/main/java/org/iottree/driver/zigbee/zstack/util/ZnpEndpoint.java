package org.iottree.driver.zigbee.zstack.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.iottree.driver.zigbee.zcl.ZCLClusterManager;
import org.iottree.driver.zigbee.zspec.ZSpecConsts;
import org.iottree.driver.zigbee.zstack.types.AFConsts;

public class ZnpEndpoint
{
	public int endpoint ;
	public int appprofid ;
	public int appdeviceid = 0x0005;
	public int appdevver = 0;
	public int appnuminclusters = 0;
	public List<Integer> appinclusterlist;
	public int appnumoutclusters = 0 ;
	public List<Integer> appoutclusterlist;
	public int latencyreq = AFConsts.networkLatencyReq.NO_LATENCY_REQS;
	
	private ZnpEndpoint(int endpoint,int appprofid)
	{
		this.endpoint = endpoint ;
		this.appprofid = appprofid ;
	}
	
	static ArrayList<ZnpEndpoint> DEFS = new ArrayList<>();
	static
	{
		ZCLClusterManager zclm = ZCLClusterManager.getInstance() ;
		
		DEFS.add(new ZnpEndpoint(1,0x0104)) ;
		DEFS.add(new ZnpEndpoint(2,0x0101)) ;
		DEFS.add(new ZnpEndpoint(3,0x0104)) ;
		DEFS.add(new ZnpEndpoint(4,0x0107)) ;
		DEFS.add(new ZnpEndpoint(5,0x0108)) ;
		DEFS.add(new ZnpEndpoint(6,0x0109)) ;
		DEFS.add(new ZnpEndpoint(8,0x0104)) ;
		DEFS.add(new ZnpEndpoint(10,0x0104)) ;
		
		ZnpEndpoint zep = new ZnpEndpoint(11,0x0104) ;
		zep.appdeviceid= 0x0400;
		zep.appnumoutclusters= 2;
		zep.appoutclusterlist= Arrays.asList(zclm.getClusterByName("ssIasZone").getID(), zclm.getClusterByName("ssIasWd").getID());
		zep.appnuminclusters= 2;
		zep.appinclusterlist= Arrays.asList(zclm.getClusterByName("ssIasAce").getID(), zclm.getClusterByName("genTime").getID());
		DEFS.add(zep) ;

		DEFS.add(new ZnpEndpoint(0x6e,0x0104)) ;
		DEFS.add(new ZnpEndpoint(12,0xc05e)) ;

		
		zep = new ZnpEndpoint(13,0x0104) ;
		zep.appnuminclusters= 1;
		zep.appinclusterlist= Arrays.asList(zclm.getClusterByName("genOta").getID());
		DEFS.add(zep) ;
		
		DEFS.add(new ZnpEndpoint(47,0x0104)) ;
	    // Required for shelly wifi and rpc clusters
		DEFS.add(new ZnpEndpoint(239,ZSpecConsts.CUSTOM_SHELLY_PROFILE_ID)) ;
		DEFS.add(new ZnpEndpoint(242,0xa1e0)) ;
	}
	
	public static ZnpEndpoint findByProfieId(int prof_id)
	{
		for(ZnpEndpoint ep:DEFS)
		{
			if(ep.appprofid == prof_id)
				return ep ;
		}
		return null ;
	}
	
	public static ZnpEndpoint findByEndpoint(int endpoint)
	{
		for(ZnpEndpoint ep:DEFS)
		{
			if(ep.endpoint == endpoint)
				return ep ;
		}
		return null ;
	}
	
	public static Integer findEndpointDefaultByProfileId(int prof_id)
	{
		ZnpEndpoint ae = findByProfieId(prof_id) ;
		if(ae==null)
			return null ;
		return ae.endpoint ;
	}
	
	public static Integer findProfileIdDefaultByEndpoint(int endpoint)
	{
		ZnpEndpoint ae = findByEndpoint(endpoint) ;
		if(ae==null)
			return null ;
		return ae.appprofid ;
	}
}
