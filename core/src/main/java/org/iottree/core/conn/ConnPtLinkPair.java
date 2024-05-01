package org.iottree.core.conn;

import org.iottree.core.ConnPt;

/**
 * TODO
 * 
 * 特殊的接入。用来对接两个Stream接入，形成中间监视节点
 * 
 * 此接入可以使得IOTTree成为一个现有链路透明设备，可以介入已有链接，并对原有的设备通信不造成影响。
 * 
 * 1）可以用来分析现有设备数据抓取和协议
 * 2）可以支持特殊驱动，实现更高级的功能。
 * 
 * 可以用来实现一些特殊嵌入模块，比如触摸屏通过RS232和PLC对接，如果需要提取数据，就需要这个专门设备和功能
 * 
 * @author jason.zhu
 *
 */
public class ConnPtLinkPair extends ConnPt
{
	ConnPtStream link1 = null ;
	
	ConnPtStream link2 = null ;

	@Override
	public String getConnType()
	{
		return "link_pair";
	}

	@Override
	public String getStaticTxt()
	{
		return null;
	}

	@Override
	public void RT_checkConn()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isConnReady()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getConnErrInfo()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
