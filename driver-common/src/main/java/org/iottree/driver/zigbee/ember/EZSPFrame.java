package org.iottree.driver.zigbee.ember;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.util.SLStatus;
import org.json.JSONObject;

/**
 * EZSP=EmberZNet Serial Protocol
 * 
 * EZSP version >= 8
 * 
 * @author jason.zhu
 */
public abstract class EZSPFrame
{
	protected int seq;

	protected int frmCtrlLow;

	protected int frmCtrlHigh = 1;

	// protected int frmID ;

	// protected byte[] paramBS = null ;

	// protected EZSPParams params = null ;

	protected final EmberManager owner ;
	
	public EZSPFrame(EmberManager owner)
	{
		this.owner = owner ;
	}

	public int getSeq()
	{
		return this.seq;
	}

	public int getFrmCtrlLow()
	{
		return this.frmCtrlLow;
	}

	public int getFrmCtrlHigh()
	{
		return this.frmCtrlHigh;
	}

	public int getEZSPVersion()
	{
		return this.owner.getEZSPVersion();
	}

	public abstract int getFrmID();

	public abstract String getFrmName();

	public boolean isCommand()
	{
		return (this.frmCtrlLow & 0b1000_0000) == 0;
	}

	public boolean isResponse()
	{
		return (this.frmCtrlLow & 0b1000_0000) == 1;
	}

	public int getNetworkIndex()
	{
		return (this.frmCtrlLow >> 5) & 0b11; // 5-6
	}

	public boolean isSecurityEnabled()
	{
		return (this.frmCtrlHigh & 0b1000_0000) > 0;
	}

	public boolean isPaddingEnabled()
	{
		return (this.frmCtrlHigh & 0b0100_0000) > 0;
	}

	/**
	 * Version 0,1,Reserved,Reserved
	 * 
	 * @return
	 */
	public int getFrameFormatVersion()
	{
		return this.frmCtrlHigh & 0b11;
	}

	public final int getEZSPFrameLen(boolean b_legacy)
	{
		if(b_legacy)
			return 3+this.getParamsLen() ;
		else
			return 5+this.getParamsLen() ;
	}
	
	protected abstract int getParamsLen() ;

	final boolean pack(byte[] bs, int offset, int len, StringBuilder failedr)
	{
		if (len < 5)
		{
			return false;
		}

		bs[offset + 0] = (byte) this.seq;
		bs[offset + 1] = (byte) this.frmCtrlLow;
		bs[offset + 2] = (byte) this.frmCtrlHigh;
		DataUtil.setUInt16(this.getFrmID(), bs, offset + 3); // 3-4

		return this.packParams(bs, offset + 5, len - 5, failedr);
	}

	final boolean packLegacy(byte[] bs, int offset, int len, StringBuilder failedr)
	{
		if (len < 3)
			return false;

		bs[offset + 0] = (byte) this.seq;
		bs[offset + 1] = (byte) this.frmCtrlLow;
		bs[offset + 2] = (byte) this.getFrmID();
		return this.packParams(bs, offset + 3, len - 3, failedr);
	}
	
	public byte[] packEZSPParams(StringBuilder failedr)
	{
		byte[] r = new byte[this.getParamsLen()] ;
		boolean b ;
		if(this.owner.isLegacy())
			b = this.packLegacy(r, 0, r.length,failedr) ;
		else
			b = this.pack(r, 0, r.length,failedr) ;
		if(b)
			return r ;
		return null ;
	}
	
	public String toEZSPPackOrErr()
	{
		StringBuilder failedr = new StringBuilder() ;
		byte[] bs = packEZSPParams(failedr) ;
		if(bs!=null)
			return Convert.byteArray2HexStr(bs, "-") ;
		else
			return failedr.toString() ;
	}
	

	public static EZSPFrame unpack(EmberManager emgr,byte[] bs, int offset, int len,int[] ret_frm_id, StringBuilder failedr)
	{
		if (bs.length < 5)
		{
			if (failedr != null)
				failedr.append("len must >=5 with ezsp frame");
			return null;
		}

		int frm_ctl = bs[offset + 1] & 0xFF;
		int frm_id = DataUtil.getUInt16(bs, 3 + offset); // 3-4
		if(ret_frm_id!=null)
			ret_frm_id[0] = frm_id ;
		EZSPFrame ret = null;
		if ((frm_ctl & 0b1000_0000) == 0)
			ret = EZSPManager.createCmdByFrmID(emgr,frm_id);// .c new EZSPCmd() ;
		else
			ret = EZSPManager.createRspByFrmID(emgr,frm_id);
		if (ret == null)
		{
			if (failedr != null)
				failedr.append("unknown EZSP frm_id=" + DataUtil.transHex(frm_id));
			return null;
		}

		ret.seq = bs[offset + 0] & 0xFF;
		ret.frmCtrlLow = frm_ctl;
		ret.frmCtrlHigh = bs[offset + 2] & 0xFF;

		if (!ret.unpackParams(bs, offset + 5, len - 5, failedr))
			return null;
		return ret;
	}

	public static EZSPFrame unpackLegacy(EmberManager emgr,byte[] bs, int offset, int len,int[] ret_frm_id, StringBuilder failedr)
	{
		if (bs.length < 3)
		{
			if (failedr != null)
				failedr.append("len must >=3 with legacy ezsp frame");
			return null;
		}

		int frm_ctl = bs[offset + 1] & 0xFF;
		int frm_id = bs[offset + 2] & 0xFF;
		if(ret_frm_id!=null)
			ret_frm_id[0] = frm_id ;
		EZSPFrame ret = null;
		if ((frm_ctl & 0b1000_0000) == 0)
			ret = EZSPManager.createCmdByFrmID(emgr,frm_id);// .c new EZSPCmd() ;
		else
			ret = EZSPManager.createRspByFrmID(emgr,frm_id);
		if (ret == null)
		{
			if (failedr != null)
				failedr.append("unknown EZSP frm_id=" + DataUtil.transHex(frm_id));
			return null;
		}

		ret.seq = bs[offset + 0] & 0xFF;
		ret.frmCtrlLow = frm_ctl;
		//ret.frmCtrlHigh = bs[offset + 2] & 0xFF;

		if (!ret.unpackParams(bs, offset + 3, len - 3, failedr))
			return null;
		return ret;
	}

	protected abstract boolean packParams(byte[] bs, int offset, int len, StringBuilder failedr);

	protected abstract boolean unpackParams(byte[] bs, int offset, int len, StringBuilder failedr);

	public String toString()
	{
		String r = this.isCommand() ? "cmd-" : "rsp-";
		r += "seq:"+this.seq+";sec:" + this.isSecurityEnabled() + ";nwk_idx:" + this.getNetworkIndex() + ";pad_en:"
				+ this.isPaddingEnabled() + ";fmt_ver:" + this.getFrameFormatVersion() + ";frm_id:"
				+ DataUtil.transHexAddr(this.getFrmID()) + ";frm_n:" + this.getFrmName();

		r += ";pm_len:" + this.getParamsLen();
		r += ";pm_jo:" + this.toJO();
		return r;
	}
	
	public String toShortString()
	{
		String r = this.isCommand() ? "cmd-" : "rsp-";
		r += "frm_id:"+ DataUtil.transHexAddr(this.getFrmID()) + ";frm_n:" + this.getFrmName();

		r += ";pm_len:" + this.getParamsLen();
		r += ";pm_jo:" + this.toJO();
		return r ;
	}

	public JSONObject toJO()
	{
		return new JSONObject();// .put("frm_id",this.getFrmID()).putOpt("frm_n",
								// this.getFrmName()) ;
	}

	protected static int UTIL_readStatus(int ezsp_ver,byte[] bs, int offset,SLStatus[] ret_st)
	{
		if (ezsp_ver < 0x0e)
		{
			int status = bs[offset] & 0xFF;
			// skip lookup if SUCCESS (always zero)
			if (status == SLStatus.OK.getIntVal())
			{
				ret_st[0] = SLStatus.OK;
				return 1 ;
			}
			// use mapped value, or pass as-is if none found
			SLStatus r = SLStatus.EMBER_TO_SL_STATUS_MAP.get(status);
			if (r != null)
			{
				ret_st[0] = r;
				return 1 ;
			}

			// EzspStatus mapping to SLStatus is always same code
			ret_st[0] =  SLStatus.ZIGBEE_EZSP_ERROR;
			return 1 ;
		}

		int v = (int) DataUtil.getUInt32(bs, offset);
		SLStatus r = SLStatus.valOfInt(v);
		if (r != null)
		{
			ret_st[0] = r;
			return 4 ;
		}
		ret_st[0] = SLStatus.ZIGBEE_EZSP_ERROR;
		return 4 ;
	}
	
	protected static int UTIL_writeStatus(int ezsp_ver,int status_v,byte[] bs, int offset)
	{
		if (ezsp_ver < 0x0e)
		{
			bs[offset] = (byte)status_v;
			return 1 ;
		}
		DataUtil.setUInt32(status_v, bs, offset);
		return 4 ;
	}
}
