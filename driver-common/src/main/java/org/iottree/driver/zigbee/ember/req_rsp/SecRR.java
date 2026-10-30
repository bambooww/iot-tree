package org.iottree.driver.zigbee.ember.req_rsp;

import org.iottree.driver.zigbee.DataBuffer;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.ASHFrmData;
import org.iottree.driver.zigbee.ember.EZSPBuffer;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.ezsp.Common_Cmd;
import org.iottree.driver.zigbee.ember.ezsp.GetNwkKeyInfRsp;
import org.iottree.driver.zigbee.ember.ezsp.SecManExportKeyRsp;
import org.iottree.driver.zigbee.ember.ezsp.SecManExportLinkKeyByIdxRsp;
import org.iottree.driver.zigbee.ember.ezsp.SecManImpTransientKeyCmd;
import org.iottree.driver.zigbee.ember.types.EmberInitSecState;
import org.iottree.driver.zigbee.ember.types.EzspFrameID;
import org.iottree.driver.zigbee.ember.types.EzspValueId;
import org.iottree.driver.zigbee.ember.types.SecManContext;
import org.iottree.driver.zigbee.ember.types.SecManFlag;
import org.iottree.driver.zigbee.ember.types.SecManKeyType;
import org.iottree.driver.zigbee.ember.types.SecManNwkKeyInf;
import org.iottree.driver.zigbee.ember.util.EzspStatus;
import org.iottree.driver.zigbee.ember.util.SLStatus;

/**
 * Security RR
 * @author jason.zhu
 *
 */
public class SecRR
{
	/**
     * Export the link key at given index from the key table.
     * @param uint8_t  Index of key to export.
     * @returns Status of key export operation.
     * @returns sl_zigbee_sec_man_context_t * Context referencing the exported key. Contains information like the EUI64 address it is associated with.
     * @returns sl_zb_sec_man_key_t * The exported key.
     * @returns sl_zb_sec_man_aps_key_metadata_t * Metadata about the key.
     */
    public static SecManExportLinkKeyByIdxRsp exportLinkKeyByIndex(EmberManager em, int index) throws Exception
    {
        /**
         * Export an APS link key by index.
         *
         * @param index uint8_t
         * @param address EUI64
         * @param plaintext_key sl_zb_sec_man_key_t*
         * @param key_data sl_zb_sec_man_aps_key_metadata_t*
         */
        byte[] bs = new byte[] {(byte)index} ;
        Common_Cmd cmd = new Common_Cmd(em,EzspFrameID.EXPORT_LINK_KEY_BY_INDEX,bs) ;
        CommonRR<SecManExportLinkKeyByIdxRsp> rr = new CommonRR<>(em, cmd,SecManExportLinkKeyByIdxRsp.class) ;
		if (!em.RT_synReqRsp(rr, null))
			return null;
        return rr.getReturnRsp() ;
    }
	/**
     * Import an application link key into the key table.
     * @param index uint8_t Index where this key is to be imported to.
     * @param address EUI64 this key is associated with.
     * @param plaintextKey sl_zb_sec_man_key_t * The key data to be imported.  uint[16]
	 * @throws Exception 
     * @returns Status of key import operation.
     */
    public static SLStatus importLinkKey(EmberManager em,int index, String ieee_addr, String enc_key) throws Exception
    {
        /**
         * Import a link key, or SL_ZB_SEC_MAN_KEY_TYPE_APP_LINK key, into storage.
         *
         * @param index uint8_t [IN] The index to set or overwrite in the key table for keys of
         * type SL_ZB_SEC_MAN_KEY_TYPE_APP_LINK.  If index is set to 0xFF (255), then
         * the key will either overwrite whichever key table entry has an EUI of address
         * (if one exists) or write to the first available key table entry.  The index
         * that the key was placed into will not be returned by this API.
         * @param address EUI64 [IN] The EUI belonging to the key.
         * @param plaintext_key sl_zb_sec_man_key_t* [IN] A pointer to the key to import.
         *
         * @return SLStatus.OK upon success, a valid error code otherwise.
         *
         */
//        const sendBuffalo = this.startCommand(EzspFrameID.IMPORT_LINK_KEY);
//        sendBuffalo.writeUInt8(index);
//        sendBuffalo.writeIeeeAddr(address);
//        sendBuffalo.writeSecManKey(plaintextKey);

        byte[] bs = new byte[] {(byte)index} ;
        DataBuffer buf = DataBuffer.alloc(1+8+16) ;
        buf.writeUInt8(index);
        buf.writeIEEEAddr(ieee_addr);
        buf.writeSecManKey(enc_key);
		Common_Cmd cmd = new Common_Cmd(em,EzspFrameID.IMPORT_LINK_KEY,buf.getBuffer()) ;
		SetCommonRR rr = new SetCommonRR(em, cmd) ;
		if (!em.RT_synReqRsp(rr, null))
			return null;
		return rr.slstRsp.getSLStatus();
    }

    
	/**
     * This function erases the data in the key table entry at the specified index.
     * If the index is invalid, false is returned.
     * @param index uint8_t This indicates the index of entry to erase.
     * @returns The success or failure of the operation.
     */
    public static SLStatus eraseKeyTableEntry(EmberManager em,int index) throws Exception
    {
        byte[] bs = new byte[] {(byte)index} ;
		Common_Cmd cmd = new Common_Cmd(em,EzspFrameID.ERASE_KEY_TABLE_ENTRY,bs) ;
		SetCommonRR rr = new SetCommonRR(em, cmd) ;
		if (!em.RT_synReqRsp(rr, null))
			return null;
		return rr.slstRsp.getSLStatus();
    }
    
    /**
     * This function clears the key table of the current network.
     * @returns The success or failure of the operation.
     */
    public static SLStatus clearKeyTable(EmberManager em) throws Exception
    {
        Common_Cmd cmd = new Common_Cmd(em,EzspFrameID.CLEAR_KEY_TABLE,null) ;
		SetCommonRR rr = new SetCommonRR(em, cmd) ;
		if (!em.RT_synReqRsp(rr, null))
			return null;
		return rr.slstRsp.getSLStatus();
    }
    
    /**
     * Sets the security state that will be used by the device when it forms or
     * joins the network. This call should not be used when restoring saved network
     * state via networkInit as this will result in a loss of security data and will
     * cause communication problems when the device re-enters the network.
     * @param state EmberInitialSecurityState * The security configuration to be set.
     * @returns The success or failure code of the operation.
     */
    public static SLStatus setInitSecState(EmberManager em,EmberInitSecState state) throws Exception
    {
    	EZSPBuffer buf = EZSPBuffer.alloc(EmberInitSecState.STRUCT_LEN) ;
    	buf.writeEmberInitSecState(state);
    	Common_Cmd cmd = new Common_Cmd(em,EzspFrameID.SET_INITIAL_SECURITY_STATE,buf.getBuffer()) ;
		SetCommonRR rr = new SetCommonRR(em, cmd) ;
		if (!em.RT_synReqRsp(rr, null))
			return null;
		return rr.slstRsp.getSLStatus();
    }
    
    /**
     * Wrapper for `ezspSetValue`.
     * @param mask
     * @throws Exception 
     * @returns
     */
    public static SLStatus setEmberExtSecBitmask(EmberManager em,int mask) throws Exception
    {
    	byte[] bs = new byte[2] ;
    	DataUtil.setUInt16(mask, bs, 0);
        return SetValueRR.setValue(em, EzspValueId.EXTENDED_SECURITY_BITMASK, bs);
    }
    
    /**
     * Retrieve information about the current and alternate network key, excluding their contents.
     * @returns Success or failure of retrieving network key info.
     * @returns sl_zb_sec_man_network_key_info_t * Information about current and alternate network keys.
     */
    public static GetNwkKeyInfRsp getNetworkKeyInfo(EmberManager em) throws Exception
    {
        /**
         * Retrieve information about the network key and alternate network key.
         * It will not retrieve the actual network key contents.
         *
         * @param network_key_info sl_zb_sec_man_network_key_info_t* [OUT] The network key info struct used to store network key metadata,
         * containing information about whether the current and next network keys are set, and the
         * sequence numbers associated with each key.
         *
         * @return SLStatus SLStatus.OK
         *
         */
    	Common_Cmd cmd = new Common_Cmd(em,EzspFrameID.GET_NETWORK_KEY_INFO,null) ;
		CommonRR<GetNwkKeyInfRsp> rr = new CommonRR<>(em, cmd,GetNwkKeyInfRsp.class) ;
		if (!em.RT_synReqRsp(rr, null))
			return null;
		return rr.getReturnRsp() ;
    }

    
    /**
     * Exports a key from security manager based on passed context.
     * @param context sl_zb_sec_man_context_t * Metadata to identify the requested key.
     * @returns sl_zb_sec_man_key_t * Data to store the exported key in.
     * @returns SLStatus * The success or failure code of the operation.
     */
    public static SecManExportKeyRsp exportKey(EmberManager em,SecManContext cxt) throws Exception
	{
    	/**
         * Export a key from storage. Certain keys are indexed, while others are not, as described here.
         *
         * If context->core_key_type is..
         *
         * ..SL_ZB_SEC_MAN_KEY_TYPE_NETWORK, then context->key_index dictates whether to
         * export the current (active) network key (index 0) or the alternate network
         * key (index 1).
         *
         * ..SL_ZB_SEC_MAN_KEY_TYPE_TC_LINK, then context->eui64 is checked if
         * context->flags is set to ZB_SEC_MAN_FLAG_EUI_IS_VALID. If the EUI supplied
         * does not match the TC EUI stored on the local device (if it is known), then
         * an error is thrown.
         *
         * ..SL_ZB_SEC_MAN_KEY_TYPE_TC_LINK_WITH_TIMEOUT, then keys may be searched by
         * context->eui64 or context->key_index. context->flags determines how to search
         * (see ::sl_zigbee_sec_man_flags_t).
         *
         * ..SL_ZB_SEC_MAN_KEY_TYPE_APP_LINK, then keys may be searched by
         * context->eui64 or context->key_index. context->flags determines how to search
         * (see ::sl_zigbee_sec_man_flags_t).
         *
         * ..SL_ZB_SEC_MAN_KEY_TYPE_GREEN_POWER_PROXY_TABLE_KEY or
         * SL_ZB_SEC_MAN_KEY_TYPE_GREEN_POWER_SINK_TABLE_KEY, then context->key_index
         * dictates which key entry to export. These Green Power keys are indexed keys,
         * and there are EMBER_GP_PROXY_TABLE_SIZE/EMBER_GP_SINK_TABLE_SIZE many of them.
         *
         * For all other key types, both context->key_index and context->eui64 are not used.
         *
         * @param context sl_zb_sec_man_context_t* [IN/OUT] The context to set. The context dictates which key
         * type to export, which key_index (if applicable) into the relevant key
         * storage, which eui64 (if applicable), etc.
         * @param plaintext_key sl_zb_sec_man_key_t* [OUT] The key to export.
         *
         * @note The context->derived_type must be SL_ZB_SEC_MAN_DERIVED_KEY_TYPE_NONE.
         * Other values are ignored.
         *
         * @return SLStatus.OK upon success, a valid error code otherwise.
         */
    	 // NOTE: added for good measure
        if (cxt.coreKeyType == SecManKeyType.INTERNAL) {
            throw new Exception("ezspExportKey cannot use INTERNAL key type  "+EzspStatus.ERROR_INVALID_CALL);
        }
		SecManExportKeyRR rr =new SecManExportKeyRR(em,cxt) ;
		if (!em.RT_synReqRsp(rr, null))
			return null;
		return rr.getExportKeyRsp() ;
	}
    
    /**
     * Import a transient link key.
     * @param eui64 EUI64 associated with this transient key.
     * @param plaintextKey sl_zb_sec_man_key_t * The key to import.
     * @param flags sl_zigbee_sec_man_flags_t (unused in v14+) Flags associated with this transient key.
     * @returns Status of key import operation.
     */
    public static SLStatus importTransientKey(EmberManager em,String eui64,String plaintxtkey,SecManFlag flag) throws Exception
	{
    	SecManImpTransientKeyCmd cmd = new SecManImpTransientKeyCmd(em,eui64,plaintxtkey,flag) ;
    	SetCommonRR rr = new SetCommonRR(em,cmd) ;
    	if (!em.RT_synReqRsp(rr, null))
			return null;
    	return rr.getSLStatus() ;
	}
    
    /**
     * no response
     * @param em
     * @return
     * @throws Exception 
     */
    public static boolean clearTransientLinkKeys(EmberManager em) throws Exception
    {
    	Common_Cmd cmd = new Common_Cmd(em,EzspFrameID.CLEAR_TRANSIENT_LINK_KEYS,null) ;
    	ASHFrmData fd = ASHFrmData.createFrmDataCmd(em, cmd) ;
		em.RT_sendFrmData(fd) ;
		return true;
    }
}
