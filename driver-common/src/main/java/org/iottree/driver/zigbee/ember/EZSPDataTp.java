package org.iottree.driver.zigbee.ember;

/**
 *  Type Definitions

Bool	uint8_t	Boolean type with values true and false.
sl_zigbee_ezsp_config_id_t	uint8_t	Identifies a configuration value.
sl_zigbee_ezsp_value_id_t	uint8_t	Identifies a value.
sl_zigbee_ezsp_extended_value_id_t	uint8_t	Identifies a value based on specified characteristics. Each set of characteristics is unique to that value and is specified during the call to get the extended value.
sl_zigbee_ezsp_endpoint_flags_t	uint16_t	Flags associated with the endpoint data configured on the NCP.
EmberConfigTxPowerMode	uint16_t	Values for SL_ZIGBEE_EZSP_CONFIG_TX_POWER_MODE.
sl_zigbee_ezsp_policy_id_t	uint8_t	Identifies a policy.
sl_zigbee_ezsp_decision_bitmask_t	uint16_t	This is the policy decision bitmask that controls the trust center decision strategies. The bitmask is modified and extracted from the sl_zigbee_ezsp_decision_id_t for supporting bitmask operations.
sl_zigbee_ezsp_decision_id_t	uint8_t	Identifies a policy decision.
sl_zigbee_ezsp_mfg_token_id_t	uint8_t	Manufacturing token IDs used by sl_zigbee_ezsp_get_mfg_token().
sl_zigbee_ezsp_status_t	uint8_t	Status values used by EZSP.
sl_zigbee_af_status_t	uint8_t	A ZCL status. See relevant header files for enumeration and possible values.
sl_zigbee_event_units_t	uint8_t	Either marks an event as inactive or specifies the units for the event execution time.
sl_zigbee_node_type_t 	uint8_t	The type of the node.
sl_zigbee_network_status_t	uint8_t	The possible join states for a node.
sl_zigbee_incoming_message_type_t	 uint8_t	Incoming message types.
sl_zigbee_outgoing_message_type_t	 uint8_t	Outgoing message types.
sl_zigbee_mac_passthrough_type_t	uint8_t	MAC passthrough message type flags.
sl_zigbee_binding_type_t	uint8_t	Binding types.
sl_zigbee_aps_option_t	uint16_t	Options to use when sending a message.
sl_zigbee_ezsp_network_scan_type_t 	uint8_t	Network scan types.
sl_zigbee_join_decision_t	uint8_t	Decision made by the trust center when a node attempts to join.
sl_zigbee_leave_network_option_t	uint8_t	Use in case zigbee leave network with options.
sl_zigbee_initial_security_bitmask_t	uint16_t	This is the Initial Security Bitmask that controls the use of various security features.
sl_zigbee_current_security_bitmask_t	uint16_t	This is the Current Security Bitmask that details the use of various security features.
sl_zigbee_key_type_t	uint8_t	Describes the type of ZigBee security key.
sl_zigbee_key_struct_bitmask_t	 uint16_t	Describes the presence of valid data within the EmberKeyStruct structure.
sl_zigbee_device_update_t	uint8_t	The status of the device update.
sl_zigbee_key_status_t	 uint8_t	The status of the attempt to establish a key.
sl_zigbee_counter_type_t	uint8_t	Defines the events reported to the application by the readAndClearCounters command.
sl_zigbee_join_method_t	uint8_t	The type of method used for joining.
sl_zigbee_zdo_configuration_flags_t	uint8_t	Flags for controlling which incoming ZDO requests are passed to the application. To see if the application is required to send a ZDO response to an incoming message, the application must check the APS options bitfield within the incomingMessageHandler callback to see if the EMBER_APS_OPTION_ZDO_RESPONSE_REQUIRED flag is set.
EmberConcentratorType	 uint16_t	Type of concentrator.
sl_zigbee_zll_state_t	uint16_t	ZLL device state identifier.
sl_zigbee_zll_key_index_t	 uint8_t	ZLL key encryption algorithm enumeration.
sl_zigbee_ezsp_zll_network_operation_t	uint8_t	Differentiates among ZLL network operations.
sl_zigbee_network_init_bitmask_t	uint16_t	Bitmask options for sli_zigbee_stack_network_init()
sl_zigbee_multi_phy_nwk_config_t	 uint8_t	Network configuration for the desired radio interface for multi-phy network.
sl_zigbee_duty_cycle_state_t 	uint8_t	Duty cycle states.
sl_zigbee_radio_power_mode_t	uint8_t	Radio power modes.
sl_zigbee_entropy_source_t	uint8_t	Entropy sources.
sl_zigbee_sec_man_key_type_t	uint8_t	Key types recognized by Zigbee Security Manager.
sl_zigbee_sec_man_derived_key_type_t	uint16_t	Derived key types recognized by Zigbee Security Manager.
sl_zigbee_sec_man_flags_t	uint8_t	Flags for key operations.
sl_zigbee_leave_request_flags_t	uint8_t	Flags for NWK leave request command.
sl_802154_short_addr_t	uint16_t	16-bit ZigBee network address.
sl_status_t	uint32_t	See sl_status.h for an enumerated list.
sl_zigbee_gp_status_t	uint8_t	See enumeration in gp-types.h
sl_802154_pan_id_t	 uint16_t	802.15.4 PAN ID.
sl_zigbee_multicast_id_t	uint16_t	16-bit ZigBee multicast group identifier.
sl_802154_long_addr_t	uint8_t[8]	EUI 64-bit ID (an IEEE address).
sl_zigbee_mac_interface_id_t	uint8_t	The 8-bit identifier to uniquely identify the interface.
sl_zigbee_manufacturing_string_t	uint8_t[16]	A 16-byte array for the manufacturing string.
sl_zigbee_duty_cycle_hecto_pct_t	uint16_t	The percent of duty cycle for a limit. Duty Cycle, Limits, and Thresholds are reported in units of Percent * 100 (i.e. 10000 = 100.00%, 1 = 0.01%).
sl_zigbee_library_id_t	 uint8_t	A library identifier
sl_zigbee_library_status_t 	uint8_t	The presence and status of the Ember library.
sl_zigbee_gp_security_level_t	uint8_t	The security level of the GPD.
sl_zigbee_gp_key_type_t	uint8_t	The type of security key to use for the GPD.
sl_zigbee_gp_proxy_table_entry_status_t	uint8_t	The proxy table entry status
sl_zigbee_gp_security_frame_counter_t	uint32_t	The security frame counter
sl_zigbee_gp_sink_table_entry_status_t	uint8_t	The sink table entry status


 * @author jason.zhu
 *
 */
public abstract class EZSPDataTp
{
	
	// ------------
	
//	private static HashMap<Integer,EZSPParams> FRMID_CMD2PARAMS = new HashMap<>() ;
//	private static HashMap<Integer,EZSPParams> FRMID_RSP2PARAMS = new HashMap<>() ;
//	
//	private static void register_frm_params(EZSPParams cmd,EZSPParams rsp)
//	{
//		if(cmd!=null)
//			FRMID_CMD2PARAMS.put(cmd.getFrmID(),cmd) ;
//		if(rsp!=null)
//			FRMID_RSP2PARAMS.put(rsp.getFrmID(),rsp) ;
//	}
//	
//	static
//	{
//		register_frm_params(new VersionCmd(),new VersionRsp()) ;
//		register_frm_params(new GetConfigValCmd(),new GetConfigValRsp()) ;
//		register_frm_params(new SetConfigValCmd(),new SetConfigValRsp()) ;
//		register_frm_params(new GetLibStatusCmd(),new GetLibStatusRsp()) ;
//		register_frm_params(new GetValueCmd(),new GetValueRsp()) ;
//	}
//	
//	public static EZSPParams createCmdByFrmID(int frm_id)
//	{
//		EZSPParams pm = FRMID_CMD2PARAMS.get(frm_id) ;
//		if(pm==null)
//			return null ;
//		try
//		{
//			return pm.getClass().getConstructor().newInstance() ;
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			return null ;
//		}
//	}
//	
//	public static EZSPParams createRspByFrmID(int frm_id)
//	{
//		EZSPParams pm = FRMID_RSP2PARAMS.get(frm_id) ;
//		if(pm==null)
//			return null ;
//		try
//		{
//			return pm.getClass().getConstructor().newInstance() ;
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			return null ;
//		}
//	}
}
