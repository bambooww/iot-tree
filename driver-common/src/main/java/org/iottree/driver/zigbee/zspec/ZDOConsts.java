package org.iottree.driver.zigbee.zspec;

public class ZDOConsts
{
	/** The endpoint where the Zigbee Device Object (ZDO) resides. */
	public static final int ZDO_ENDPOINT = 0;

	/** The profile ID used by the Zigbee Device Object (ZDO). */
	public static final int ZDO_PROFILE_ID = 0x0000;

	/** ZDO messages start with a sequence number. */
	public static final int ZDO_MESSAGE_OVERHEAD = 1;

	public static final int MULTICAST_BINDING = 0x01;
	public static final int UNICAST_BINDING = 0x03;

	/** 64-bit challenge value used by CHALLENGE_REQUEST/CHALLENGE_RESPONSE clusters */
	public static final int CHALLENGE_VALUE_SIZE = 8;
	/** The 256-bit Curve 25519 public point. */
	public static final int CURVE_PUBLIC_POINT_SIZE = 32;
}
