package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.types.EzspFrameID;
import org.iottree.driver.zigbee.ember.util.ICopyable;
import org.json.JSONObject;

public class Common_Cmd extends EZSPCmd implements ICopyable<Common_Cmd>
{
	private EzspFrameID ezsp_fid ;

	private byte[] cmd_bs  ;
	
	public Common_Cmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	public Common_Cmd(EmberManager emgr,EzspFrameID frm_id,byte[] cmd_bs)
	{
		super(emgr) ;
		this.ezsp_fid = frm_id;
		this.cmd_bs = cmd_bs ;
	}
	
	@Override
	public Common_Cmd copyMe(EmberManager emgr)
	{
		return new Common_Cmd(emgr,this.ezsp_fid,null);
	}

	
	@Override
	public int getFrmID()
	{
		return ezsp_fid.getIntVal();
	}

	@Override
	public String getFrmName()
	{
		return ezsp_fid.name() ;
	}

	
	public byte[] getCmdBS()
	{
		return this.cmd_bs ;
	}
	
	@Override
	protected int getParamsLen()
	{
		if(cmd_bs==null)
			return 0 ;
		return cmd_bs.length;
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(cmd_bs==null||cmd_bs.length<=0)
			return true ;
		if(len<cmd_bs.length)
			return false;
		System.arraycopy(this.cmd_bs, 0, bs, offset, cmd_bs.length);
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<=0)
			return true;
		this.cmd_bs = new byte[len] ;
		System.arraycopy(bs, offset,this.cmd_bs, 0, len);
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().putOpt("cmd_bs",Convert.byteArray2HexStr(this.cmd_bs, "-")) ;
	}

}
