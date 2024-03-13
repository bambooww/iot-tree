package org.iottree.core.ui;

import java.util.List;

import org.iottree.core.util.Convert;

/**
 * 
 * @author jason.zhu
 *
 */
public abstract class UICat
{
	UIManager uimgr = null ;
	
	public UICat(UIManager uim)
	{
		this.uimgr = uim ;
	}
	
	public abstract String getName() ;
	
	public abstract String getTitle() ;

	public abstract String getDesc() ;
	
	public abstract List<UIItem> listUIItems() ;
}
