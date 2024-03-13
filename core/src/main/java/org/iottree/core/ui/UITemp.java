package org.iottree.core.ui;

import java.util.List;

import org.iottree.core.UATag;

public abstract class UITemp implements IUITemp
{
//	String name = null ;
//	
//	String title = null ;
//	
//	String desc = null ;
//	
//	int tagMaxNum = 1 ;
//	int tagMinNum = 1 ;
	
	public UITemp() //(String name,String title,String desc)
	{
//		this.name = name ;
//		this.title = title ;
//		this.desc = desc ;
	}
	
	public abstract String getName();

	public abstract String getTitle();

	public String getDesc()
	{
		return "";
	}

	@Override
	public int supportInputTagMaxNum()
	{
		return 1;
	}

	@Override
	public int supportInputMinNum()
	{
		return 1;
	}

	@Override
	public int getWidth()
	{
		return 800;
	}

	@Override
	public int getHeight()
	{
		return 600;
	}

}
