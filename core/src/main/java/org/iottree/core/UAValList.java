package org.iottree.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UAValList
{
	private LinkedList<UAVal> vals = new LinkedList<>() ;
	
	private int maxNum = 10 ;
	
	
	public UAValList(int maxnum)
	{
		this.maxNum = maxnum ;
	}
	
	public int getValNum()
	{
		return vals.size() ;
	}
	
	public synchronized void addVal(UAVal v)
	{
		vals.addLast(v);
		if(vals.size()>this.maxNum)
			vals.removeFirst() ;
	}
	
	public synchronized List<UAVal> getAllAndClear()
	{
		LinkedList<UAVal> r = vals ;
		vals = new LinkedList<>() ;
		return r ;
	}
	
	public List<UAVal> getVals(long lastdt)
	{
		ArrayList<UAVal> rets = new ArrayList<>() ;
		for(UAVal v:vals)
		{
			if(lastdt>0&& lastdt>=v.getValDT())
				continue ;
			rets.add(v) ;
		}
		return rets ;
	}
	
	
}
