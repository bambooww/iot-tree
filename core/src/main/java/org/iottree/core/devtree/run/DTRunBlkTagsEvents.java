package org.iottree.core.devtree.run;

import java.util.LinkedHashMap;

import org.iottree.core.devtree.DTNode;
import org.iottree.core.devtree.DTRunBlk;
import org.iottree.core.devtree.DTRunBlkCat;
import org.iottree.core.devtree.DTRunEvent;
import org.iottree.core.devtree.DTRunProp;

public class DTRunBlkTagsEvents extends DTRunBlk
{

	public DTRunBlkTagsEvents(DTRunBlkCat cat,String name)
	{
		super(cat,name);
	}

	@Override
	public LinkedHashMap<String, DTRunProp> getProps()
	{
		return null;
	}

	@Override
	public LinkedHashMap<String, DTRunEvent> getEvents()
	{
		return null;
	}

	@Override
	public boolean RT_run(DTNode nd)
	{
		return false;
	}

}
