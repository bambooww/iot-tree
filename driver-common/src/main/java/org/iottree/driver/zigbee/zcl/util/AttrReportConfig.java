package org.iottree.driver.zigbee.zcl.util;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zcl.ZCLAttr;
import org.iottree.driver.zigbee.zcl.ZCLCluster;
import org.json.JSONObject;

public class AttrReportConfig
{
	/**
	 * 0=The receiver of the Configure Reporting command SHALL Configure Reporting
to send to each destination as resolved by the bindings for the cluster hosting the
attributes to be reported.  设置后续的属性值
	 * 1=This indicates to the receiver of the Configure Reporting command that the
sender has configured its reporting mechanism to transmit reports and that, based
on the current state of the sender’s bindings, the sender will send reports to the
receiver 报告设备此属性值，后续只需要attrid即可
	 */
	int dir ;
	
	int attrId ;
	
	int attrDataTp ;
	
	private transient DataType attrDTP = null ; 
	/**
	 * The minimum reporting interval field is 16 bits in length and SHALL contain the minimum interval, in
seconds, between issuing reports of the specified attribute.
If this value is set to 0x0000, then there is no minimum limit, unless one is imposed by the specification of the
cluster using this reporting mechanism or by the application.

	 */
	int minReportIntv ;
	
	/**
	 * The maximum reporting interval field is 16 bits in length and SHALL contain the maximum interval, in
seconds, between issuing reports of the specified attribute.
If this value is set to 0xffff, then the device SHALL not issue reports for the specified attribute, and the
configuration information for that attribute need not be maintained. (Note: in an implementation using dynamic memory allocation, the memory space for that information may then be reclaimed).
If this value is set to 0x0000 and the minimum reporting interval field equals 0xffff, then the device SHALL
revert back to its default reporting configuration. The reportable change field, if present, SHALL be set to
zero.
	 */
	int maxReportIntv ;
	
	/**
	 * The reportable change field SHALL contain the minimum change to the attribute that will result in a report
being issued. This field is of variable length. For attributes with 'analog' data type (see Table 2-10) the field
has the same data type as the attribute. The sign (if any) of the reportable change field is ignored.

	 * 此数据长度由attrDataTP确定
	 * If the Maximum Reporting Interval Field is set to 0x0000 and the minimum reporting interval field equals
0xffff, indicating a default reporting configuration, then this field SHALL be set to zero upon transmission
and ignored upon reception. 
	 */
	Object reportableChg ; //超过此变化此就产生report。比如温度，可以设置0x0032 // 0.50℃
	
	/**
	 * The timeout period field is 16 bits in length and SHALL contain the maximum expected time, in seconds,
between received reports for the attribute specified in the attribute identifier field. If more time than this
elapses between reports, this may be an indication that there is a problem with reporting.
If this value is set to 0x0000, reports of the attribute are not subject to timeout.
	 */
	int timeoutPeriod = 0;
	
	public AttrReportConfig() {}
	
	public AttrReportConfig(int dir,int attrid)
	{
		this.dir = dir ;
		this.attrId = attrid ;
	}
	
	public int getDirect()
	{
		return this.dir ;
	}
	
	public int getAttrId()
	{
		return this.attrId ;
	}
	
	public int getAttrDataTp()
	{
		return this.attrDataTp ;
	}
	
	public int getMinReportIntv()
	{
		return this.minReportIntv ;
	}
	
	public int getMaxReportIntv()
	{
		return this.maxReportIntv ;
	}
	
	public Object getReportableChg()
	{
		return this.reportableChg ;
	}
	
	public int getTimeoutPeriod()
	{
		return this.timeoutPeriod ;
	}
	
	public JSONObject toJO()
	{
		JSONObject ret = new JSONObject().put("dir", this.dir).put("attr_id", this.attrId)
				.putOpt("attr_data_tp", this.attrDataTp).put("min_report_intv", this.minReportIntv)
				.putOpt("max_report_intv", this.maxReportIntv).putOpt("reportable_chg", this.reportableChg)
				.putOpt("timeout_period", this.timeoutPeriod) ;
		return ret ;
	}
	
	public int calcPackedLen()
	{
		if(dir==1)
			return 3 ;
		int vlen = this.attrDTP.calcVarOctLen(this.reportableChg);
		return vlen + 10 ;
	}
	
	public byte[] packTo()
	{
		if(dir==1)
		{
			byte[] r = new byte[3] ;
			r[0] = (byte)this.dir;
			DataUtil.setUInt16(this.attrId, r, 1);
			return r ;
		}
		
		if(dir!=0)
			throw new RuntimeException("unknown dir="+this.dir) ;
		if(this.reportableChg==null)
			throw new RuntimeException("reportable chg cannot be null") ;
		int vlen = this.attrDTP.calcVarOctLen(this.reportableChg);
		
		byte[] r = new byte[vlen+10] ;
		r[0] = (byte)this.dir;
		DataUtil.setUInt16(this.attrId, r, 1); //1-2
		r[3] = (byte)attrDataTp ;
		DataUtil.setUInt16(this.minReportIntv, r, 4); //4-5
		DataUtil.setUInt16(this.maxReportIntv, r, 6); //6-7
		DataType.packObjValTo(this.attrDTP, this.reportableChg,r, 8) ;
		DataUtil.setUInt16(this.timeoutPeriod, r, r.length-2); //len-2 len-1
		return r ;
	}
	
	public int parseFrom(byte[] data,int offset)
	{
		if(data.length-offset<3)
			return -1 ;
		this.dir = (byte)data[0] ;
		this.attrId = DataUtil.getUInt16(data, 1) ; //1-2
		if(dir==1)
			return 3 ;
		if(dir!=0)
			throw new RuntimeException("unknown dir="+this.dir) ;
		this.attrDataTp = (byte)data[3] ;
		this.attrDTP = DataType.valOfInt(this.attrDataTp) ;
		if(this.attrDTP==null)
			throw new RuntimeException("unknown attr data type="+this.attrDataTp) ;
		
		this.minReportIntv = DataUtil.getUInt16(data, 4); //4-5
		this.maxReportIntv = DataUtil.getUInt16(data, 6); //6-7
		this.reportableChg = this.attrDTP.parseObjVal(data, 8) ;
		int vlen = this.attrDTP.calcVarOctLen(this.reportableChg) ; //8-   8+vlen-1
		
		this.timeoutPeriod = DataUtil.getUInt16(data, 8+vlen) ; // 8+vlen   9+vlen
		return 10+vlen ;
	}
	
	public static AttrReportConfig createGetIns(int attrid)
	{
		return new AttrReportConfig(1,attrid) ;
	}
	
	public static AttrReportConfig createSetIns(ZCLCluster cluster,int attrid,
			int min_report_intv,int max_report_intv,Object reportable_chg,int timeout_period)
	{
		ZCLAttr attr = cluster.getAttributeById(attrid) ;
		if(attr==null)
			return null ;
		AttrReportConfig r = new AttrReportConfig(0,attrid) ;
		r.attrDataTp = attr.getID() ;
		r.attrDTP = attr.getDataType() ;
		if(r.attrDTP==null)
			return null ;
		r.minReportIntv = min_report_intv ;
		r.maxReportIntv = max_report_intv ;
		r.reportableChg = reportable_chg ;
		r.timeoutPeriod = timeout_period ;
		return r ;
	}
}
