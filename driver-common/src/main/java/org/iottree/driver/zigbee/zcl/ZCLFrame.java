package org.iottree.driver.zigbee.zcl;

import org.iottree.driver.zigbee.DataUtil;
import org.json.JSONObject;

public abstract class ZCLFrame
{
	public static final int FRM_CTRL_GEN_CMD = 0 ;
	public static final int FRM_CTRL_CLUSTER_CMD = 1 ;
	
	public static final int DIR_CLIENT_TO_SERVER = 0;
	public static final int DIR_SERVER_TO_CLIENT = 1;
	
	/**
	 * 8bits    5-7:Reserved
	 */
	int frmCtrl ;
	
	/**
	 * Manufacturer Code Field
	 */
	int facCode ;
	
	int transSeqN ;
	
	public ZCLFrame()
	{}
	
	//public ZLCFrame()
	/**
	 * 00 - Profile-wide Command,Command is global for all clusters, including manufacturer specific clusters
	 * 01 - Command is specific or local to a cluster (no fac spec)
	 * other values - reserved
	 * @return
	 */
	public int getFrameTP()
	{
		return this.frmCtrl & 0x03 ; //bit 0-1
	}
	
	public String getFrameTPT()
	{
		int ft = getFrameTP() ;
		switch(ft)
		{
		case FRM_CTRL_GEN_CMD:
			return "gen_cmd";
		case FRM_CTRL_CLUSTER_CMD:
			return "cluster_cmd";
		}
		return "" ;
	}
	
	public static final boolean checkFrameGenCmd(int frame_ctl)
	{
		 return (frame_ctl & 0x03) == FRM_CTRL_GEN_CMD;
	}
	
	public static final boolean checkFrameClusterCmd(int frame_ctl)
	{
		 return (frame_ctl & 0x03) == FRM_CTRL_CLUSTER_CMD;
	}
	
	//Command is global for all clusters, including manufacturer specific clusters2
	public boolean isGlobal()
	{
		return this.getFrameTP()==FRM_CTRL_GEN_CMD ;
	}
	
	//Command is specific or local to a cluster
	public boolean isSpecific() //isFrameTP_Cluster()
	{
		return this.getFrameTP()==FRM_CTRL_CLUSTER_CMD ;
	}
	
	public boolean hasFacSpec()
	{
		return (this.frmCtrl & 0x04) > 0 ; // bit 2
	}
	
	public static final boolean checkHasFacSpec(int frame_ctl)
	{
		 return (frame_ctl & 0x04) > 0;
	}
	/**
	 * 1 - sent from the server side of a cluster to the client side of a cluster
	 * 0 - the command is being sent from the client side of a cluster to the server side of a cluster. 
	 * @return true-server2client false-client2server
	 */
	public boolean isDirServer2Client()
	{
		return (this.frmCtrl & 0x08) > 0 ; //bit 3
	}
	
	/**
	 * false-the Default Response command will be returned, under the conditions specified in 2.5.12.2
	 * true - the Default Response command will only be returned if there is an error, also under the conditions specified in 2.5.12.2
	 * @return
	 */
	public boolean isDisableDefaultResp()
	{
		return (this.frmCtrl & 0x10) > 0 ; //bit 4
	}
	
	protected final ZCLFrame asFrameCtrl(int frame_tp,boolean fac_spec,int fac_code,boolean dir_s2c,boolean disable_def_resp)
	{
		if(frame_tp!=0 && frame_tp!=1)
			throw new IllegalArgumentException("frame type must 0,1") ;
		//this.transSeqN = seq_n ;
		this.frmCtrl = frame_tp & 0x03 ;
		if(fac_spec)
		{
			this.frmCtrl |= 0x04 ;
			this.facCode = fac_code ;
		}
		if(dir_s2c)
			this.frmCtrl |= 0x08 ;
		if(disable_def_resp)
			this.frmCtrl |= 0x10 ;
		
		return this ;
	}
	
	protected final ZCLFrame asFrameCtrlGenCmd(boolean dir_s2c)
	{
		return asFrameCtrl(FRM_CTRL_GEN_CMD,false,0,dir_s2c,true) ;
	}
	
	protected final ZCLFrame asFrameCtrlClusterCmd(boolean dir_s2c)
	{
		return asFrameCtrl(FRM_CTRL_CLUSTER_CMD,false,0,dir_s2c,true) ;
	}
	
	public ZCLFrame asSeqNum(int seq_n)
	{
		this.transSeqN = seq_n ;
		return this ;
	}
	
	public int getFacCode()
	{
		return this.facCode ;
	}
	
	public int getTransSeqNum()
	{
		return this.transSeqN ;
	}
	
	/**
	 * The Command Identifier field is 8 bits in length and specifies the cluster command being used. If the frame
type sub-field of the frame control field is set to 0b00, the command identifier corresponds to one of the
non-reserved values of Table 2-3. If the frame type sub-field of the frame control field is set to 0b01, the
command identifier corresponds to a cluster specific command. The cluster specific command identifiers can
be found in each individual document describing the clusters (see also 2.2.1.1).
	 * @return
	 */
	public abstract int getCmdID();
	
	public abstract String getCmdName() ;
	
	public int getCmdIDResp()
	{
		return -1 ;
	}
	
	public ZCLGenCmdID getZCLGenCmdID()
	{
		return new ZCLGenCmdID(this.getCmdID()) ;
	}
	
	public final int getHeadLen()
	{
		if(this.hasFacSpec())
			return 5 ;
		else
			return 3 ;
	}
	
	protected abstract int getPayloadLen() ;
	
	protected abstract int packPayloadDataTo(byte[] data,int offset) ;
	
	protected abstract boolean unpackPayloadData(byte[] data,int offset,StringBuilder failedr) ;
	
	protected abstract JSONObject getPayloadJO();
	
	public final int packFrame(byte[] data,int offset)
	{
		int hlen = getHeadLen() ;
		int pld_len = getPayloadLen() ;
		int plen = hlen + pld_len ;
		if(plen<=0)
			return 0 ;
		if(data.length - offset < plen)
			throw new IllegalArgumentException("data len is small") ;
		int sidx = offset ;
		data[sidx] = (byte)this.frmCtrl ; // 0
		sidx ++ ;
		if(this.hasFacSpec())
		{
			DataUtil.setUInt16(this.facCode, data, sidx); // 1-2
			sidx += 2 ;
		}
		data[sidx] = (byte)this.transSeqN ;
		sidx ++ ;
		data[sidx] = (byte)this.getCmdID() ;
		sidx ++ ;
		if(pld_len>0)
		{
			//byte[] pld_data = this.getPayloadData() ;
			this.packPayloadDataTo(data,sidx) ;
			//System.arraycopy(pld_data, 0, data, sidx, pld_len);
		}
		return plen ;
	}
	
	public final byte[] packFrame()
	{
		int hlen = getHeadLen() ;
		int pld_len = getPayloadLen() ;
		int plen = hlen + pld_len ;
		byte[] rets = new byte[plen] ;
		if(plen<=0)
			return rets ;
		int rlen = packFrame(rets,0) ;
		if(rlen==plen)
			return rets ;
		else
			return null ;
	}
	
	public final boolean unpackFrame(byte[] data,int offset,StringBuilder failedr)
	{
		int sidx = offset ;
		this.frmCtrl = data[sidx] & 0xFF ;
		sidx ++ ;
		if(this.hasFacSpec())
		{
			this.facCode = DataUtil.getUInt16(data, sidx) ;
			sidx += 2 ;
		}
		this.transSeqN = data[sidx] & 0xFF ;
		sidx ++ ;
		int cmd_id = data[sidx] & 0xFF ;
		if(cmd_id!=this.getCmdID())
		{
			failedr.append("cmd id is not matched") ;
			return false;
		}
		sidx ++ ;
		return unpackPayloadData(data,sidx,failedr) ;
	}
	
	public JSONObject toJO()
	{
		JSONObject ret = new JSONObject().put("frame_tp", getFrameTP()).put("frame_tpt", this.getFrameTPT()).put("fac_spec", this.hasFacSpec())
				.put("dir_s2c", this.isDirServer2Client()).put("seq_n", this.getTransSeqNum());
		if(this.hasFacSpec())
			ret.putOpt("fac_code", this.getFacCode()) ;
		ret.put("cmd_id", getCmdID()).put("pld_len", this.getPayloadLen());
		if(this.isGlobal())
			ret.putOpt("cmd_t", this.getZCLGenCmdID().getCmdTitle());
		else if(this.isSpecific())
			ret.putOpt("cmd_t", "ClusterCmd:"+this.getCmdID());
		JSONObject pldjo = this.getPayloadJO();
		ret.putOpt("pld", pldjo) ;
		return ret ;
	}
	
	@Override
	public String toString()
	{
		return "frm_tp="+this.getFrameTP()+":"+this.getFrameTPT()+" fac_spec="+this.hasFacSpec()+" dir_s2c="+this.isDirServer2Client() ;
	}
}
