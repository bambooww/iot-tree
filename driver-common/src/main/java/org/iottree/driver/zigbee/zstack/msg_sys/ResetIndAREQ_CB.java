package org.iottree.driver.zigbee.zstack.msg_sys;

import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

/**
 * Callback SYS_RESET_IND
 * 3.8.2.1 SYS_RESET_IND  p94
 * 
 * @author jason.zhu
 *
 */
public class ResetIndAREQ_CB extends MsgRecv
{
	public static final int CMD0 = 0x41 ;
	public static final int CMD1 = 0x80 ;
	
	public ResetIndAREQ_CB()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "SYS_RESET_IND";
	}

	@Override
	public int getDataLenMin()
	{
		return 6;
	}

	/**
	 * 0x00 Power-up
	 * 0x01 External
	 * 0x02 Watch-dog
	 * @return
	 */
	public int getReson()
	{
		return this.data[0] & 0xFF ;
	}
	
	public String getResonT()
	{
		switch(this.getReson())
		{
		case 0x00:
			return "power-up";
		case 0x01:
			return "external";
		case 0x02:
			return "watch-dog";
		default:
			return "" ;
		}
	}
	
	public int getTransportRev()
	{
		return data[1] & 0xFF ;
	}
	
	public int getProductId()
	{
		return data[2] & 0xFF ;
	}
	
	public int getMajorRel()
	{
		return data[3] & 0xFF ;
	}
	
	public int getMinorRel()
	{
		return data[4] & 0xFF ;
	}
	
	public int getMaintRel()
	{
		return data[5] & 0xFF ;
	}
	
	@Override
	public JSONObject toRecvJO()
	{
		return new JSONObject().put("transport_rev",this.getTransportRev())
				.putOpt("product_id", this.getProductId())
				.put("majorrel", this.getMajorRel())
				.put("minorrel", this.getMinorRel())
				.put("maintrel", this.getMaintRel())
				.put("reson", this.getReson()).putOpt("reson_t", this.getResonT());
	}
	
//	@Override
//	public String toString()
//	{
//		String ret = super.toString() ;
//		return ret + " = [ Reson="+getResonT()+" TransportRev:"+getTransportRev()+" Product:"+this.getProductId()+ " "+this.getMajorRel()+"."+this.getMinorRel()+"."+this.getMaintRel()+"]";
//	}
}
