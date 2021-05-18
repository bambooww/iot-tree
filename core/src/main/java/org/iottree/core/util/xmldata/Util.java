package org.iottree.core.util.xmldata;

import java.util.Arrays;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.*;

import org.iottree.core.util.xmldata.XmlData.XmlDataParam;

public class Util
{
//	public static DefaultMutableTreeNode toTreeNode(XmlData xd)
//	{
//		DefaultMutableTreeNode tn = new DefaultMutableTreeNode("[]");
//		toTreeNode(tn);
//		return tn;
//	}
	
	
	public static void toTreeNode(XmlData xd,DefaultTreeModel tm,DefaultMutableTreeNode tn)
	{
		tn.removeAllChildren();
		String[] names = new String[xd.pname2val.size()];
		xd.pname2val.keySet().toArray(names);
		Arrays.sort(names);
		
		for(String n:names)
		{
			XmlVal xv = xd.pname2val.get(n);
			if(xv==null)
				continue ;
			
			String sv = "";
			if(xv.bArray)
			{
				if(xv.strVals!=null)
				{
					for(String s:xv.strVals)
					{
						sv += "|"+s;
					}
				}
			}
			else
			{
				sv = xv.strVal;
			}
			
			DefaultMutableTreeNode tmpn = new DefaultMutableTreeNode(n+"="+sv,false);
			tm.insertNodeInto(tmpn, tn, tn.getChildCount());
		}
		
		names = new String[xd.pname2data.size()];
		xd.pname2data.keySet().toArray(names);
		Arrays.sort(names);
		for(String n:names)
		{
			XmlDataParam xdp = xd.pname2data.get(n);
			if(xdp==null)
				continue ;
			
			if(xdp.bArray)
			{
				if(xdp.xmlDatas!=null)
				{
					int c = xdp.xmlDatas.size();
					for(int i = 0 ; i < c ; i ++)
					{
						XmlData tmpxd = xdp.xmlDatas.get(i);
						DefaultMutableTreeNode tmpn = new DefaultMutableTreeNode(n+"["+i+"]");
						tm.insertNodeInto(tmpn, tn, tn.getChildCount());
						toTreeNode(tmpxd,tm,tmpn);
					}
				}
			}
			else
			{
				if(xdp.xmlData!=null)
				{
					DefaultMutableTreeNode tmpn = new DefaultMutableTreeNode(n);
					tm.insertNodeInto(tmpn, tn, tn.getChildCount());
					toTreeNode(xdp.xmlData,tm,tmpn);
				}
			}
		}
	}
}
