package org.iottree.driver.zigbee.ember.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EmberConsts;
import org.iottree.driver.zigbee.zspec.ZSpecConsts;
import org.json.JSONObject;

/**
 * sl_zigbee_network_parameters_t
 * 
 * uint8_t[8] extendedPanId   | The network's extended PAN identifier.

uint16_t panId  | The network's PAN identifier.

uint8_t radioTxPower | A power setting, in dBm.

uint8_t radioChannel  | A radio channel.

sl_zigbee_join_method_t joinMethod | The method used to initially join the network.

sl_802154_short_addr_t nwkManagerId | NWK Manager ID. The ID of the network manager in the current network. This may only be set at joining when using SL_ZIGBEE_USE_CONFIGURED_NWK_STATE as the join method.

uint8_t nwkUpdateId | NWK Update ID. The value of the ZigBee nwkUpdateId known by the stack. This is used to determine the newest instance of the network after a PAN ID or channel change. This may only be set at joining when using SL_ZIGBEE_USE_CONFIGURED_NWK_STATE as the join method.

uint32_t channels | NWK channel mask. The list of preferred channels that the NWK manager has told this device to use when searching for the network. This may only be set at joining when using SL_ZIGBEE_USE_CONFIGURED_NWK_STATE as the join method.

 * @author jason.zhu
 */
public class NwkParams
{
	public String extPanId = null ; //byte[] extPanId = new byte[8] ; //uint8_t[8]
	
	public String getExtPanIdStr()
	{
		return extPanId ;
	}
	
	public int panId =ZSpecConsts.INVALID_PAN_ID ; //uint16
	
	public int radio_tx_power = 0 ; //uint8_t radioTxPower A power setting, in dBm.

	public int radio_ch =EmberConsts.INVALID_RADIO_CHANNEL  ; //uint8_t radioChannel A radio channel.

	//sl_zigbee_join_method_t	uint8_t	The type of method used for joining.
	public EmberJoinMethod join_method = EmberJoinMethod.MAC_ASSOCIATION ;// sl_zigbee_join_method_t joinMethod 

	//sl_802154_short_addr_t	uint16_t	16-bit ZigBee network address.
	public int nwk_short_addr = ZSpecConsts.NULL_NODE_ID ; //sl_802154_short_addr_t nwkManagerId
	
	public int nwk_updateid=0 ; //uint8
	
	public long channels = ZSpecConsts.ALL_802_15_4_CHANNELS_MASK ; //uint32 NWK channel mask.
	
	public List<Integer> listChannels()
	{
		return uint32MaskToChannels(this.channels) ;
	}
	
	public int pack(byte[] bs,int offset)
	{
		//System.arraycopy(this.extPanId, 0, bs, offset, 8); //0-7
		DataUtil.transStr2PanId(this.extPanId, bs, 0) ;
		DataUtil.setUInt16(this.panId,bs, offset+8) ; //8-9
		bs[offset+10] = (byte)this.radio_tx_power ; //10
		bs[offset+11] = (byte)this.radio_ch ; //11
		bs[offset+12]  = (byte)this.join_method.getIntVal() ; //12
		DataUtil.setUInt16(this.nwk_short_addr,bs, offset+13) ; //13-14
		bs[offset+15] = (byte)this.nwk_updateid ; //15
		DataUtil.setUInt32(this.channels,bs, offset+16) ; //16-19
		return 20 ;
	}
	
	public boolean unpack(byte[] bs,int offset)
	{
		//System.arraycopy(bs, offset, this.extPanId, 0, 8); //0-7
		this.extPanId = DataUtil.transPanId(bs, 0) ;
		this.panId = DataUtil.getUInt16(bs, offset+8) ; //8-9
		this.radio_tx_power = bs[offset+10] & 0xFF; //10
		this.radio_ch = bs[offset+11] & 0xFF; //11
		this.join_method = EmberJoinMethod.valOfInt(bs[offset+12] & 0xFF); //12
		this.nwk_short_addr = DataUtil.getUInt16(bs, offset+13) ; //13-14
		this.nwk_updateid = bs[offset+15] & 0xFF ; //15
		this.channels = DataUtil.getUInt32(bs, offset+16) ; //16-19
		return true ;
	}
	
	public JSONObject toJO()
	{
		return new JSONObject().putOpt("ext_panid", extPanId).put("panid", this.panId)
				.put("tx_power", this.radio_tx_power).put("radio_ch", this.radio_ch)
				.put("short_addr", this.nwk_short_addr).put("nwk_updateid", this.nwk_updateid)
				.put("channels", this.channels) ;
	}
	
	public static final int STRUCT_LEN = 20 ;
	
	public static List<Integer> uint32MaskToChannels(long mask)
	{
		List<Integer> channels = new ArrayList<>() ;

	    for (int channel : ALL_802_15_4_CHANNELS) {
	        if (((1<<channel) & mask)>0) {
	            channels.add(channel);
	        }
	    }

	    return channels;
	};
	
	private static List<Integer> ALL_802_15_4_CHANNELS = Arrays.asList(11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26);
}
