package org.iottree.core.util.xmldata;

public interface IXmlDataable
{
	public XmlData toXmlData();
	
	public void fromXmlData(XmlData xd);
}
