package org.iottree.driver.zigbee.ember.types;


/**
 * sl_zigbee_aps_option_t options
 * 
 * @author jason.zhu
 */
public class ApsOption
{
	// No options.
    public static final int APS_OPTION_NONE = 0x0000;
    // UNKNOWN: Discovered while receiving data
    public static final int APS_OPTION_UNKNOWN = 0x0008;
    // Send the message using APS Encryption, using the Link Key shared with the
    // destination node to encrypt the data at the APS Level.
    public static final int APS_OPTION_ENCRYPTION = 0x0020;
    // Resend the message using the APS retry mechanism.
    public static final int APS_OPTION_RETRY = 0x0040;
    // Causes a route discovery to be initiated if no route to the destination
    // is known.
    public static final int APS_OPTION_ENABLE_ROUTE_DISCOVERY = 0x0100;
    // Causes a route discovery to be initiated even if one is known.
    public static final int APS_OPTION_FORCE_ROUTE_DISCOVERY = 0x0200;
    // Include the source EUI64 in the network frame.
    public static final int APS_OPTION_SOURCE_EUI64 = 0x0400;
    // Include the destination EUI64 in the network frame.
    public static final int APS_OPTION_DESTINATION_EUI64 = 0x0800;
    // Send a ZDO request to discover the node ID of the destination, if it is
    // not already know.
    public static final int APS_OPTION_ENABLE_ADDRESS_DISCOVERY = 0x1000;
    // Reserved.
    public static final int APS_OPTION_POLL_RESPONSE = 0x2000;
    // This incoming message is a ZDO request not handled by the EmberZNet
    // stack, and the application is responsible for sending a ZDO response.
    // This flag is used only when the ZDO is configured to have requests
    // handled by the application. See the CONFIG_APPLICATION_ZDO_FLAGS
    // configuration parameter for more information.
    public static final int APS_OPTION_ZDO_RESPONSE_REQUIRED = 0x4000;
    // This message is part of a fragmented message. This option may only be set
    // for unicasts. The groupId field gives the index of this fragment in the
    // low-order byte. If the low-order byte is zero this is the first fragment
    // and the high-order byte contains the number of fragments in the message.
    public static final int APS_OPTION_FRAGMENT = 0x8000;
}
