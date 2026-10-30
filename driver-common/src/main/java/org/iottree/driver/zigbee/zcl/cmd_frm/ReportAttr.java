package org.iottree.driver.zigbee.zcl.cmd_frm;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.iottree.driver.zigbee.zcl.util.AttrVal;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * The Report Attributes command is used by a device to report the values of one or more of its attributes to
another device. Individual clusters, defined elsewhere in the ZCL, define which attributes are to be reported
and at what interval. See 2.5.7 to determine the destination of the Report Attributes command.
 * 
 * @author jason.zhu
 */
public class ReportAttr extends ZCLFrame
{
	List<AttrVal> attr_vals = null ;
	
	public ReportAttr()
	{}
	
	public ReportAttr(List<AttrVal> w_recs)
	{
		asFrameCtrlGenCmd(false) ; //client -> server
		this.attr_vals = w_recs ;
	}
	
	public List<AttrVal> getAttrVals()
	{
		return this.attr_vals ;
	}
	
	@Override
	public int getCmdID()
	{
		return 0x0a;
	}
	
	@Override
	public String getCmdName()
	{
		return "report_attr" ;
	}

	@Override
	protected int getPayloadLen()
	{
		if(attr_vals==null)
			return 0 ;
		int r = 0 ;
		for(AttrVal wr: attr_vals)
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
		r.put("attr_vals",jarr) ;
		if(this.attr_vals!=null)
		{
			for(AttrVal rsv:this.attr_vals)
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
		if(attr_vals==null)
			return pldlen ;
		int sidx = offset ;
		for(int i = 0 ; i < attr_vals.size() ; i ++)
		{
			AttrVal awr = attr_vals.get(i) ;
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
		this.attr_vals = awrs ;
		return true;
	}

	@Override
	public String toString()
	{
		String ret = super.toString() ;
		return ret + " attr_vals="+Convert.combineWith(this.attr_vals, ',') ;
	}
}
