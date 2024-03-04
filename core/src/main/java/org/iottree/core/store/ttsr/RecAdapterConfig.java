package org.iottree.core.store.ttsr;

import java.util.HashMap;

import org.iottree.core.Config;
import org.iottree.core.UAPrj;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlHelper;
import org.w3c.dom.Element;

public class RecAdapterConfig //extends RecAdapter
{
//	private static final HashMap<String,RecAdapterConfig> name2recm = new HashMap<>() ;
//	
//	public static RecAdapterConfig getInstance(UAPrj prj)
//	{
//		String name = prj.getName();
//		RecAdapterConfig recm = name2recm.get(name) ;
//		if(recm!=null)
//			return recm ;
//		
//		synchronized(RecAdapter.class)
//		{
//			recm = name2recm.get(name) ;
//			if(recm!=null)
//				return recm ;
//			
//			recm = new RecAdapterConfig(name) ;
//			name2recm.put(name,recm) ;
//			return recm ;
//		}
//	}
//	
//	private static final String INNER= "_inner" ;
//	
//	protected RecAdapterConfig(String name)
//	{
//		super(name) ;
//		
//		Element ele = getConfigEle(name) ;
//		if(ele==null)
//		{
//			if(!INNER.equals(name))
//				throw new IllegalArgumentException("no recorders/recorder with name="+name+" found in config") ;
//		}
//		
//		this.name = name ;
//		
//		if(ele!=null)
//		{
//			this.maxPointNum = Convert.parseToInt32(ele.getAttribute("max_pt_num"), this.maxPointNum) ;
//		}
//	}
//	
//
//	private static Element getConfigEle(String name)
//	{
//		Element rcs_ele = Config.getConfElement("recorders") ;
//		if(rcs_ele==null)
//			return null ;
//		for(Element ele:XmlHelper.getSubChildElementList(rcs_ele, "recorder"))
//		{
//			if(name.equals(ele.getAttribute("name")))
//				return ele ;
//		}
//		return null ;
//	}
//
//
//	@Override
//	public boolean init(StringBuilder failedr)
//	{
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//
//	@Override
//	protected RecIO getIO()
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
}
