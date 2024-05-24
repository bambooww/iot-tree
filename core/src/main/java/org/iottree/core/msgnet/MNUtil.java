package org.iottree.core.msgnet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;

public class MNUtil
{
	private static HashMap<String,String> nodetp2pmui = new HashMap<>() ;
	
	public static String UI_getOrLoadNodePM(String node_tp) throws IOException
	{
		if(Config.isDebug())
			return UI_loadNodePM(node_tp) ;
		
		String tmps = nodetp2pmui.get(node_tp) ;
		if(tmps!=null)
			return tmps ;
		
		tmps = UI_loadNodePM(node_tp) ;
		if(tmps!=null)
			nodetp2pmui.put(node_tp,tmps) ;
		return tmps ;
	}
	
	private static String UI_loadNodePM(String node_tp) throws IOException
	{
		File f = new File(Config.getWebappBase()+"/admin/mn/nodes/"+node_tp+"_pm.html") ;
		return Convert.readFileTxt(f) ; 
	}
}
