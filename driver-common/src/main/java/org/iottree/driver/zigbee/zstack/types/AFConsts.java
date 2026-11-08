package org.iottree.driver.zigbee.zstack.types;

public class AFConsts
{
	public static final int  BEACON_MAX_DEPTH = 0x0f;
	public static final int  DEF_NWK_RADIUS = 2 * BEACON_MAX_DEPTH;
	
	public static class interpanCtl
	{
		public static final int CTL = 0;
		public static final int SET= 1;
		public static final int REG= 2;
		public static final int CHK= 3;
	}
	
	public static class networkLatencyReq
	{
		public static final int NO_LATENCY_REQS= 0;
		public static final int FAST_BEACONS= 1;
		public static final int SLOW_BEACONS= 2;
    }
    
	public static class options
	{
		public static final int PREPROCESS = 4;
		public static final int  LIMIT_CONCENTRATOR = 8;
		public static final int ACK_REQUEST = 16;
		public static final int DISCV_ROUTE = 32;
		public static final int EN_SECURITY = 64;
		public static final int SKIP_ROUTING = 128;
    }
    
    public static final int DEFAULT_RADIUS = DEF_NWK_RADIUS;
}
