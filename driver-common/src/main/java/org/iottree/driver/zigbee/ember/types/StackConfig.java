package org.iottree.driver.zigbee.ember.types;

public class StackConfig
{
	 public String CONCENTRATOR_RAM_TYPE = "high"; //"high" | "low";
/**
 * Minimum Time between broadcasts (in seconds) <1-60>
 * Default: 10
 * The minimum amount of time that must pass between MTORR broadcasts.
 */
	 public int CONCENTRATOR_MIN_TIME = 10 ;//: number;
/**
 * Maximum Time between broadcasts (in seconds) <30-300>
 * Default: 60
 * The maximum amount of time that can pass between MTORR broadcasts.
 */
	 public int CONCENTRATOR_MAX_TIME = 60 ;//: number;
/**
 * Route Error Threshold <1-100>
 * Default: 3
 * The number of route errors that will trigger a re-broadcast of the MTORR.
 */
	 public int CONCENTRATOR_ROUTE_ERROR_THRESHOLD=3 ;//: number;
/**
 * Delivery Failure Threshold <1-100>
 * Default: 1
 * The number of APS delivery failures that will trigger a re-broadcast of the MTORR.
 */
	 public int CONCENTRATOR_DELIVERY_FAILURE_THRESHOLD=1 ;//: number;
/**
 * Maximum number of hops for Broadcast <0-30>
 * Default: 0
 * The maximum number of hops that the MTORR broadcast will be allowed to have.
 * A value of 0 will be converted to the EMBER_MAX_HOPS value set by the stack.
 */
	 public int CONCENTRATOR_MAX_HOPS = 0 ;//: number;
/** <6-64> (Default: 6) @see EzspConfigId.MAX_END_DEVICE_CHILDREN */
	 public int MAX_END_DEVICE_CHILDREN = 6 ;//: number;
/** <-> (Default: 10000) @see EzspValueId.TRANSIENT_DEVICE_TIMEOUT */
	 public int TRANSIENT_DEVICE_TIMEOUT = 10000 ;//: number;
/** <0-14> (Default: 8) @see EzspConfigId.END_DEVICE_POLL_TIMEOUT */
	 public int END_DEVICE_POLL_TIMEOUT = 8 ;//: number;
/** <0-65535> (Default: 300) @see EzspConfigId.TRANSIENT_KEY_TIMEOUT_S */
	 public int TRANSIENT_KEY_TIMEOUT_S = 300 ;//: number;
/**@see Ezsp.ezspSetRadioIeee802154CcaMode */
	 public IEEE802154CcaMode CCA_MODE = null;//IEEE802154CcaMode.RSSI ; //?: keyof typeof IEEE802154CcaMode;
	 

	public static final StackConfig DEFAULT_STACK_CONFIG = new StackConfig() ;
	static
	{
		DEFAULT_STACK_CONFIG.CONCENTRATOR_RAM_TYPE= "high";
		DEFAULT_STACK_CONFIG.CONCENTRATOR_MIN_TIME= 5; // zigpc: 10
		DEFAULT_STACK_CONFIG.CONCENTRATOR_MAX_TIME= 60; // zigpc: 60
		DEFAULT_STACK_CONFIG.CONCENTRATOR_ROUTE_ERROR_THRESHOLD= 3; // zigpc: 3
		DEFAULT_STACK_CONFIG.CONCENTRATOR_DELIVERY_FAILURE_THRESHOLD= 1; // zigpc: 1, ZigbeeMinimalHost: 3
		DEFAULT_STACK_CONFIG.CONCENTRATOR_MAX_HOPS= 0; // zigpc: 0
		DEFAULT_STACK_CONFIG.MAX_END_DEVICE_CHILDREN= 32; // zigpc: 6, nabucasa: 32, Dongle-E (Sonoff firmware): 32
		DEFAULT_STACK_CONFIG.TRANSIENT_DEVICE_TIMEOUT= 10000;
		DEFAULT_STACK_CONFIG.END_DEVICE_POLL_TIMEOUT= 8; // zigpc: 8
		DEFAULT_STACK_CONFIG.TRANSIENT_KEY_TIMEOUT_S= 300; // zigpc: 65535
		DEFAULT_STACK_CONFIG.CCA_MODE = null;// // not set by default
	}
}
