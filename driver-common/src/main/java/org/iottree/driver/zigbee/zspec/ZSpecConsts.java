package org.iottree.driver.zigbee.zspec;

public class ZSpecConsts
{
	/** Current supported Zigbee revision: https://csa-iot.org/wp-content/uploads/2023/04/05-3474-23-csg-zigbee-specification-compressed.pdf */
	public static final int ZIGBEE_REVISION = 23;

	/** The network ID of the coordinator in a Zigbee network is 0x0000. */
	public static final int COORDINATOR_ADDRESS = 0x0000;

	/** Min network address reserved for broadcasting (up to 0xffff) */
	public static final int BROADCAST_MIN = 0xfff8;

	/** Endpoint profile ID for Zigbee 3.0. "Home Automation" */
	public static final int HA_PROFILE_ID = 0x0104;
	/** Endpoint profile ID for Smart Energy */
	public static final int SE_PROFILE_ID = 0x0109;
	/** Endpoint profile ID for Green Power */
	public static final int GP_PROFILE_ID = 0xa1e0;
	/** The touchlink (Zigbee Light Link/ZLL) Profile ID. */
	public static final int TOUCHLINK_PROFILE_ID = 0xc05e;
	/** The profile ID used to address all the public profiles. */
	public static final int WILDCARD_PROFILE_ID = 0xffff;
	/** The profile ID used to access Shelly devices custom clusters. */
	public static final int CUSTOM_SHELLY_PROFILE_ID = 0xc001;
	/** The profile ID used to access Control4 devices custom clusters. */
	public static final int CUSTOM_CONTROL4_PROFILE_ID = 0xc25c;

	/** The default HA endpoint. */
	public static final int HA_ENDPOINT = 0x01;
	/** The GP endpoint, as defined in the Zigbee spec. */
	public static final int GP_ENDPOINT = 0xf2;

	public static final int GP_GROUP_ID = 0x0b84;

	/** The maximum 802.15.4 channel number is 26. */
	public static final int MAX_802_15_4_CHANNEL_NUMBER = 26;
	/** The minimum 2.4GHz 802.15.4 channel number is 11. */
	public static final int MIN_802_15_4_CHANNEL_NUMBER = 11;
	/** There are sixteen 802.15.4 channels. */
	public static final int NUM_802_15_4_CHANNELS = MAX_802_15_4_CHANNEL_NUMBER - MIN_802_15_4_CHANNEL_NUMBER + 1;
	/** A bitmask to scan all 2.4 GHz 802.15.4 channels. */
	public static final int ALL_802_15_4_CHANNELS_MASK = 0x07fff800;
	/** A bitmask of the preferred 2.4 GHz 802.15.4 channels to scan. */
	public static final int PREFERRED_802_15_4_CHANNELS_MASK = 0x0318c800;
	/** List of all Zigbee channels */
	public static final int[] ALL_802_15_4_CHANNELS = new int[] {11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
	/** List of preferred Zigbee channels */
	public static final int[] PREFERRED_802_15_4_CHANNELS = new int[] {11, 14, 15, 19, 20, 24, 25};

	/** A blank (also used as "wildcard") EUI64 hex string prefixed with 0x */
	public static final byte[] BLANK_EUI64 =new byte[] {(byte) 0xFF ,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF}; // "0xffffffffffffffff";
	
	public static final String BLANK_EUI64_STR = "0xffffffffffffffff";
	
	/** A blank extended PAN ID. (null/not present) */
	public static final byte[] BLANK_EXTENDED_PAN_ID = new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

	/** An invalid profile ID. This is a reserved profileId. */
	public static final int INVALID_PROFILE_ID = 0xffff;
	/** An invalid cluster ID. */
	public static final int INVALID_CLUSTER_ID = 0xffff;
	/** An invalid PAN ID. */
	public static final int INVALID_PAN_ID = 0xffff;

	/** A distinguished network ID that will never be assigned to any node. It is used to indicate the absence of a node ID. */
	public static final int NULL_NODE_ID = 0xffff;
	/** A distinguished binding index used to indicate the absence of a binding. */
	public static final int NULL_BINDING = 0xff;

	/** This key is "ZigBeeAlliance09" */
	public static final byte[] INTEROPERABILITY_LINK_KEY= new byte [] {
	    0x5a, 0x69, 0x67, 0x42, 0x65, 0x65, 0x41, 0x6c, 0x6c, 0x69, 0x61, 0x6e, 0x63, 0x65, 0x30, 0x39
	};

	public static final int PERMIT_JOIN_FOREVER = 0xff;
	public static final int PERMIT_JOIN_MAX_TIMEOUT = 0xfe;

	/** Size of EUI64 (an IEEE address) in bytes. */
	public static final int EUI64_SIZE = 8;
	/** Size of an PAN identifier in bytes. */
	public static final int PAN_ID_SIZE = 2;
	/** Size of an extended PAN identifier in bytes. */
	public static final int EXTENDED_PAN_ID_SIZE = 8;
	/** Size of an encryption key in bytes. */
	public static final int DEFAULT_ENCRYPTION_KEY_SIZE = 16;
	/** Size of a AES-128-MMO (Matyas-Meyer-Oseas) block in bytes. */
	public static final int AES_MMO_128_BLOCK_SIZE = 16;
	/**
	 * Valid install code sizes, including `INSTALL_CODE_CRC_SIZE`.
	 *
	 * NOTE: 18 is now standard, first for iterations, order after is important (8 before 10)!
	 */
	public static final int[] INSTALL_CODE_SIZES = new int[] {18, 8, 10, 14};
	/** Size of the CRC appended to install codes. */
	public static final int INSTALL_CODE_CRC_SIZE = 2;
}
