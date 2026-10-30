package org.iottree.driver.zigbee.zspec.zdo_def;

import java.util.List;

import org.iottree.driver.zigbee.zspec.ZDOBuffer;

public interface Tlv
{
	public interface LocalTLVReader
	{
		public TlvLocal read_tlv_local(ZDOBuffer buf,int len) ;
	}
	/**
	 * Defined outside the Zigbee specification. Only TLV that can be added more
	 * than once to same frame.
	 */
	public static class ManufacturerSpecificGlobalTLV implements Tlv
	{
		/** 2-byte */
		public int zigbeeManufacturerId;
		public byte[] additionalData;
	};

	public static class SupportedKeyNegotiationMethodsGlobalTLV implements Tlv
	{
		/**
		 * Bits: - 0 Static Key Request (Zigbee 3.0 Mechanism) - 1 SPEKE using
		 * Curve25519 with Hash AES-MMO-128 - 2 SPEKE using Curve25519 with Hash
		 * SHA-256 - 3 – 7 Reserved
		 */
		public int keyNegotiationProtocolsBitmask;
		/**
		 * Bits: - 0 Symmetric Authentication Token - This is a token unique to
		 * the Trust Center and network that the device is running on, and is
		 * assigned by the Trust center after joining. The token is used to
		 * renegotiate a link key using the Key Negotiation protocol and is good
		 * for the life of the device on the network. - 1 Install Code Key -
		 * 128-bit pre-configured link-key derived from install code - 2
		 * Passcode Key - A variable length passcode for PAKE protocols. This
		 * passcode can be shorter for easy entry by a user. - 3 Basic Access
		 * Key - This key is used by other Zigbee specifications for joining
		 * with an alternate pre-shared secret. The definition and usage is
		 * defined by those specifications. The usage is optional by the core
		 * Zigbee specification. - 4 Administrative Access Key - This key is
		 * used by other Zigbee specifications for joining with an alternate
		 * pre-shared secret. The definition and usage is defined by those
		 * specifications. The usage is optional by the core Zigbee
		 * specification. - 5-7 Reserved -
		 */
		public int preSharedSecretsBitmask;
		/** XXX: Assumed optional from minimum length of TLV in spec */
		public String sourceDeviceEui64;
	}

	public static class PanIdConflictReportGlobalTLV implements Tlv
	{
		/** 2-byte */
		public int nwkPanIdConflictCount;
	}

	public static class NextPanIdChangeGlobalTLV implements Tlv
	{
		/**
		 * 2-bytes in length and indicates the next channel that will be used
		 * once a Network Update command is received to change PAN IDs.
		 */
		public int panId;
	};

	public static class NextChannelChangeGlobalTLV implements Tlv
	{
		/**
		 * 4-bytes in length and indicates the next channel that will be used
		 * once a start channel change command is received.
		 */
		public long channel;
	};

	public static class SymmetricPassphraseGlobalTLV implements Tlv
	{
		/** 16-byte 128-bit */
		public byte[] passphrase;
	};

	public static class RouterInformationGlobalTLV implements Tlv
	{
		/**
		 * Bits: - 0 Hub Connectivity - This bit indicates the state of
		 * nwkHubConnectivity from the NIB of the local device. It advertises
		 * whether the router has connectivity to a Hub device as defined by the
		 * higher-level application layer. A value of 1 means there is
		 * connectivity, and a value of 0 means there is no current Hub
		 * connectivity. 1 Uptime - This 1-bit value indicates the uptime of the
		 * router. A value of 1 indicates the router has been up for more than
		 * 24 hours. A value of 0 indicates the router has been up for less than
		 * 24 hours. 2 Preferred Parent - This bit indicates the state of
		 * nwkPreferredParent from the NIB of the local device. When supported,
		 * it extends Hub Connecivity, advertising the devices capacity to be
		 * the parent for an additional device. A value of 1 means that this
		 * device should be preferred. A value of 0 indicates that it should not
		 * be preferred. Devices that do not make this determination SHALL
		 * always report a value of 0. 3 Battery Backup - This bit indicates
		 * that the router has battery backup and thus will not be affected by
		 * temporary losses in power. 4 Enhanced Beacon Request Support - When
		 * this bit is set to 1, it indicates that the router supports
		 * responding to Enhanced beacon requests as defined by IEEE Std
		 * 802.15.4. A zero for this bit indicates the device has no support for
		 * responding to enhanced beacon requests. 5 MAC Data Poll Keepalive
		 * Support - This indicates that the device has support for the MAC Data
		 * Poll Keepalive method for End Device timeouts. 6 End Device Keepalive
		 * Support - This indicates that the device has support for the End
		 * Device Keepalive method for End Device timeouts. 7 Power Negotiation
		 * Support - This indicates the device has support for Power Negotiation
		 * with end devices. 8-15 Reserved These bits SHALL be set to 0.
		 */
		public int bitmask;
	};

	public static class FragmentationParametersGlobalTLV implements Tlv
	{
		/**
		 * This indicates the node ID of the device that the subsequent
		 * fragmentation parameters apply to.
		 */
		public int nwkAddress; // : NodeId;
		/**
		 * This bitfield indicates what fragmentation options are supported by
		 * the device. It has the following enumerated bits: - Bit 0 = APS
		 * Fragmentation Supported. Set to 1 to indicate support; 0 to indicate
		 * no support If set to 1, the maximum reassembled message size is
		 * indicated by the Maximum Incoming Transfer Unit. - Bit 1-7 = Reserved
		 * for future use
		 *
		 * XXX: Assumed optional from minimum length of TLV in spec
		 */
		public Integer fragmentationOptions = null; // ?: number;
		/**
		 * This is a copy of the local device’s apsMaxSizeASDU AIB value. This
		 * indicates the maximum reassembled message size at the application
		 * layer after fragmentation has been applied on the message at the
		 * lower layers. A device supporting fragmentation would set this field
		 * to be larger than the normal payload size of the underlying NWK and
		 * MAC layer.
		 *
		 * XXX: Assumed optional from minimum length of TLV in spec
		 */
		public Integer maxIncomingTransferUnit = null; // ?: number;
	};

	public static class JoinerEncapsulationGlobalTLV implements Tlv
	{
		public List<TlvTag> additionalTLVs;
	};

	public static class BeaconAppendixEncapsulationGlobalTLV implements Tlv
	{
		/**
		 * At least `SupportedKeyNegotiationMethodsGlobalTLV`,
		 * `FragmentationParametersGlobalTLV`
		 */
		public List<TlvTag> additionalTLVs;
	};

	public static class ConfigurationParametersGlobalTLV implements Tlv
	{
		/**
		 * 2-bytes in length and indicates various parameters about how the
		 * stack SHALL behave Bit: - 0 AIB apsZdoRestrictedMode - 1 Device
		 * Security Policy requireLinkKeyEncryptionForApsTransportKey - 2 NIB
		 * nwkLeaveRequestAllowed - 3–15 Reserved Reserved
		 */
		public int configurationParameters;
	};

	public static class DeviceCapabilityExtensionGlobalTLV implements Tlv
	{
		public byte[] data; // : Buffer;
	};

}
