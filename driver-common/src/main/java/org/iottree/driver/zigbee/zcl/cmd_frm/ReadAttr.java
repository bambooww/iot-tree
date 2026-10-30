package org.iottree.driver.zigbee.zcl.cmd_frm;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.json.JSONObject;

public class ReadAttr extends ZCLFrame
{
	List<Integer> attrIds = null ;
	
	public ReadAttr()
	{}
	
	public ReadAttr(List<Integer> attr_ids)
	{
		this.attrIds = attr_ids ;
		asFrameCtrlGenCmd(false) ; //client -> server
	}
	
	public List<Integer> getAttrIds()
	{
		return this.attrIds ;
	}
	
	@Override
	public int getCmdID()
	{
		return 0x00;
	}
	
	@Override
	public String getCmdName()
	{
		return "read_attr" ;
	}
	
	@Override
	public int getCmdIDResp()
	{
		return 0x01 ;
	}

	@Override
	protected int getPayloadLen()
	{
		if(attrIds==null)
			return 0 ;
		return attrIds.size()*2;
	}

	@Override
	protected int packPayloadDataTo(byte[] data,int offset)
	{
		int pldlen = getPayloadLen() ;
		if(pldlen<=0)
			return pldlen ;
//		byte[] ret = new byte[pldlen] ;
//		if(attrIds==null)
//			return ret ;
		for(int i = 0 ; i < attrIds.size() ; i ++)
		{
			DataUtil.setUInt16(this.attrIds.get(i), data, offset+ i*2);
		}
		return pldlen;
	}

	@Override
	protected boolean unpackPayloadData(byte[] data, int offset, StringBuilder failedr)
	{
		int len = data.length - offset ;
		if(len % 2 !=0)
		{
			failedr.append("attr ids len must 2*N") ;
			return false;
		}
		int n = len/2 ;
		ArrayList<Integer> ids = new ArrayList<>(n) ;
		for(int i = 0 ; i < n ; i ++)
		{
			ids.add(DataUtil.getUInt16(data, offset+i*2)) ;
		}
		this.attrIds = ids ;
		return true;
	}
	
	@Override
	protected JSONObject getPayloadJO()
	{
		return new JSONObject().putOpt("attr_ids", this.attrIds) ;
	}

	@Override
	public String toString()
	{
		String ret = super.toString() ;
		return ret + " attr_ids="+Convert.combineWith(this.attrIds, ',') ;
	}
}
