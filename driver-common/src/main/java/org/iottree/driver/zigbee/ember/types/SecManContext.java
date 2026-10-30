package org.iottree.driver.zigbee.ember.types;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EmberConsts;
import org.json.JSONObject;

/**
 * This data structure houses the context when interacting with the Zigbee
 * Security Manager APIs. For example, when importing a key into storage, the various
 * fields of this structure are used to determine which type of key is being stored.
 */
public class SecManContext
{
	//uint8_t
	public SecManKeyType coreKeyType = SecManKeyType.NONE;
	
	/** uint8_t */
	public int keyIndex;

	//uint16_t
	public SecManDerivedKeyType derivedType = SecManDerivedKeyType.NONE;

	/** Eui64; */
	public String eui64 = null;
	
	/** uint8_t */
	public int multiNetworkIndex;

	/** uint8_t*/
	public SecManFlag flags = SecManFlag.NONE;
	/**
	 * Unused for classic key storage. The algorithm type should be brought in
	 * by psa/crypto_types.h. Zigbee Security Manager uses
	 * PSA_ALG_ECB_NO_PADDING for keys with AES-ECB encryption, and defines
	 * ZB_PSA_ALG as AES-CCM with a 4-byte tag, used as this field's default
	 * value otherwise. uint32_t
	 */
	public long psaKeyAlgPermission =EmberConsts.ZB_PSA_ALG;
	
	public static SecManContext initContext()
	{
		SecManContext smc = new SecManContext() ;
		smc.keyIndex = 0 ;
//		for(int i = 0 ; i < 8 ; i ++)
//			smc.eui64[i] = 0 ;
		smc.multiNetworkIndex = 0 ;
		return smc ;
	}
	
	public int pack(byte[] bs,int offset)
	{
		bs[offset] = (byte)this.coreKeyType.getIntVal() ; //0
		bs[offset+1] = (byte)keyIndex;
		DataUtil.setUInt16(this.derivedType.getIntVal(),bs, offset+2) ; //2-3
		System.arraycopy(this.eui64, 0, bs, offset+4, 8); //4-11
		bs[offset+12] = (byte)this.multiNetworkIndex ; //12
		bs[offset+13] = (byte)this.flags.getIntVal() ; //12
		DataUtil.setUInt32(psaKeyAlgPermission,bs, offset+14) ; //14-17
		return 18 ;
	}
	
	public boolean unpack(byte[] bs,int offset)
	{
		this.coreKeyType = SecManKeyType.valOfInt(bs[offset]&0xFF); //0
		this.keyIndex = bs[offset+1] & 0xFF ;
		this.derivedType = SecManDerivedKeyType.valOfInt( DataUtil.getUInt16(bs, offset+2)) ; //2-3
		this.eui64 = DataUtil.transIEEEAddr(bs, offset+4); //System.arraycopy(bs, offset+4,this.eui64, 0,  8); //4-11
		this.multiNetworkIndex = bs[offset+12] & 0xFF ; //12
		this.flags = SecManFlag.valOfInt(bs[offset+13] & 0xFF);
		psaKeyAlgPermission = DataUtil.getUInt32(bs, offset+14) ; //14-17
		return true ;
	}
	
	public JSONObject toJO()
	{
		return new JSONObject().putOpt("core_key_tp", coreKeyType)
				.put("key_idx", this.keyIndex)
				.put("derived_tp", this.derivedType).putOpt("eui64", this.eui64)
				.put("multi_nwk_idx", this.multiNetworkIndex).putOpt("flags", this.flags)
				.put("psa_key_per", this.psaKeyAlgPermission) ;
	}
	
	public static final int STRUCT_LEN = 18 ;
}
