package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.types.SecManFlag;
import org.json.JSONObject;

/**
 * Import a transient link key.
 * 
 * @author jason.zhu
 */
public class SecManImpTransientKeyCmd extends EZSPCmd
{
	public static final int FRAME_ID = 0x0111 ;
	public static final String FRAME_NAME = "secManImportTransientKey" ;

	String eui64 ;
	
	String txtkey ;
	
	SecManFlag flag = null ;
	
	public SecManImpTransientKeyCmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	public SecManImpTransientKeyCmd(EmberManager emgr,String eui64,String txtkey,SecManFlag flag)
	{
		super(emgr) ;
		
		this.eui64 = eui64 ;
		this.txtkey = txtkey ;
		this.flag = flag!=null?flag:SecManFlag.NONE ;
	}
	
	@Override
	public int getFrmID()
	{
		return FRAME_ID;
	}

	@Override
	public String getFrmName()
	{
		return FRAME_NAME ;
	}

	
	public String getEUI64()
	{
		return this.eui64 ;
	}
	
	public String getPlainTxtKey()
	{
		return this.txtkey ;
	}
	
	public SecManFlag getFlag()
	{
		return this.flag ;
	}
	
	@Override
	protected int getParamsLen()
	{
		return 8+16 + ((this.getEZSPVersion() < 0x0e)?1:0);
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<2)
			return false;
		DataUtil.transStr2IEEEAddr(this.eui64, bs, offset) ; // 0 - 7
		DataUtil.transStr2SecManKey(this.txtkey, bs,offset+8) ; // 8 - 23
		if (this.getEZSPVersion() < 0x0e) {
            bs[24] = (byte)this.flag.getIntVal() ;
        }
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<2)
			return false;
		this.eui64 = DataUtil.transIEEEAddr(bs, offset) ;
		this.txtkey = DataUtil.transSecManKey(bs, offset+8) ;
		if (this.getEZSPVersion() < 0x0e) {
            this.flag = SecManFlag.valOfInt(bs[24]&0xFF) ;
        }
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().putOpt("eui64",this.eui64).putOpt("txtkey",this.txtkey).putOpt("flags", this.flag);
	}

}
