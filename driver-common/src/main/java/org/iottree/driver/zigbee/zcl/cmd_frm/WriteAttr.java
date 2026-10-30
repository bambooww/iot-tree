package org.iottree.driver.zigbee.zcl.cmd_frm;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.iottree.driver.zigbee.zcl.util.AttrVal;
import org.json.JSONArray;
import org.json.JSONObject;

public class WriteAttr extends ZCLFrame
{
	List<AttrVal> w_records = null ;
	
	public WriteAttr()
	{}
	
	public WriteAttr(List<AttrVal> w_recs)
	{
		this.w_records = w_recs ;
		asFrameCtrlGenCmd(false) ; //client -> server
	}
	
	public List<AttrVal> getAttrWriteRecords()
	{
		return this.w_records ;
	}
	
	@Override
	public int getCmdID()
	{
		return 0x02;
	}
	
	@Override
	public String getCmdName()
	{
		return "write_attr" ;
	}
	
	@Override
	public int getCmdIDResp()
	{
		return 0x04 ;
	}

	@Override
	protected int getPayloadLen()
	{
		if(w_records==null)
			return 0 ;
		int r = 0 ;
		for(AttrVal wr: w_records)
		{
			r += wr.getPackLen() ;
		}
		return r ;
	}
	
	@Override
	protected JSONObject getPayloadJO()
	{
		JSONObject r = new JSONObject();
		JSONArray jarr = new JSONArray() ;
		r.put("records",jarr) ;
		if(this.w_records!=null)
		{
			for(AttrVal rsv:this.w_records)
			{
				jarr.put(rsv.toJO()) ;
			}
		}
		return r ;
	}

	@Override
	protected int packPayloadDataTo(byte[] data,int offset)
	{
		int pldlen = getPayloadLen() ;
		if(w_records==null)
			return pldlen ;
		int sidx = offset ;
		for(int i = 0 ; i < w_records.size() ; i ++)
		{
			AttrVal awr = w_records.get(i) ;
			byte[] bs = awr.getPackData() ;
			System.arraycopy(bs, 0, data, sidx, bs.length);
			sidx += bs.length ;
		}
		return pldlen;
	}

	@Override
	protected boolean unpackPayloadData(byte[] data, int offset, StringBuilder failedr)
	{
		int len = data.length - offset ;
		if(len<=0)
		{
			failedr.append("len must >0") ;
			return false;
		}
		
		ArrayList<AttrVal> awrs = new ArrayList<>() ;
		int sidx = offset ;
		while(sidx<data.length)
		{
			AttrVal awr = AttrVal.parseFrom(data, sidx) ;
			if(awr==null)
				break ;
			awrs.add(awr) ;
			sidx += awr.getPackLen() ;
		}
		this.w_records = awrs ;
		return true;
	}

	@Override
	public String toString()
	{
		String ret = super.toString() ;
		return ret + " attr_write_recs="+Convert.combineWith(this.w_records, ',') ;
	}

}
