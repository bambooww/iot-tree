package org.iottree.driver.zigbee.ember.types;

public class LinkKeyData
{
	public String dev_ieee_addr ;
	
	public String sec_key ;
	
	public long out_frm_cc ;
	
	public long in_frm_cc ;
	
	public LinkKeyData()
	{}
	
	public LinkKeyData(String dev_ieee,String sec_key,long out_frm_cc,long in_frm_cc)
	{
		this.dev_ieee_addr = dev_ieee ;
		this.sec_key = sec_key ;
		this.out_frm_cc = out_frm_cc ;
		this.in_frm_cc = in_frm_cc ;
	}
}
