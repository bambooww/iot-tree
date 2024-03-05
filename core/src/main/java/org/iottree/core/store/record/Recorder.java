package org.iottree.core.store.record;

import org.iottree.core.util.xmldata.data_val;

/**
 * 一个记录器代表一个记录流程
 * 1，可以包含输入绑定Tag
 * 2，可以包含RecPro，RecSaver等内容。
 * 
 * @author jason.zhu
 *
 */
public class Recorder
{
	@data_val
	String id = null ;
	
	@data_val(param_name = "en")
	boolean bEnable = true ;
	
	@data_val(param_name = "n")
	private String name = "" ;
	
	@data_val(param_name = "t")
	private String title = "" ;
	
	@data_val(param_name = "d")
	private String desc="" ;
	
	
	public void sendInput(RecPro.IProInput input)
	{
		
	}
}
