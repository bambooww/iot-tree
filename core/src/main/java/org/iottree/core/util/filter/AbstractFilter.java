package org.iottree.core.util.filter;

import java.util.HashMap;

import org.iottree.core.util.xmldata.IXmlDataable;
import org.iottree.core.util.xmldata.XmlData;


/**
 * 
 * @author jasonzhu
 *
 */
public abstract class AbstractFilter implements IXmlDataable
{
	public abstract String getFilterName() ;
	
	public abstract String getFilterTitle() ;
	
	/**
	 * ����������-ʹ�÷�Χ����Ч������
	 * @return
	 */
	public abstract String getFilterDesc() ;
	
	
	public void initFilter(XmlData xd)
	{
		
	}
	
	
	public abstract double filter(double d);
	
	
	public XmlData toXmlData()
	{
		XmlData xd = new XmlData() ;
		xd.setParamValue("_n", getFilterName());
		return xd ;
	}
	
	public void fromXmlData(XmlData xd)
	{
		initFilter(xd);
	}
}
